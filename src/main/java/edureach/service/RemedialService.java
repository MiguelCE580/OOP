package edureach.service;

import edureach.data.DataAccess;
import edureach.data.JsonStore;
import edureach.model.*;
import edureach.util.IdGen;
import edureach.util.TimeUtil;

import java.util.List;
import java.util.Optional;

/**
 * Intervention module for failing students (new module): detects learners
 * below the passing mark, builds a remedial plan (weak domains + review
 * lessons + simplified remedial quiz) and tracks it to completion.
 */
public final class RemedialService {

    private RemedialService() {}

    public static List<RemedialPlan> allPlans() {
        return JsonStore.get().db().remedialPlans;
    }

    public static List<RemedialPlan> plansOf(String studentId) {
        return JsonStore.get().db().remedialPlans.stream()
                .filter(p -> p.getStudentId().equals(studentId)).toList();
    }

    public static Optional<RemedialPlan> plan(String planId) {
        return JsonStore.get().db().remedialPlans.stream()
                .filter(p -> p.getPlanId().equals(planId)).findFirst();
    }

    /** Creates plans for every struggling student of the course (idempotent). */
    public static int scanCourse(String courseId) {
        int created = 0;
        for (var entry : AnalyticsService.strugglingStudents(courseId)) {
            String studentId = entry.getKey().getUserId();
            boolean open = allPlans().stream().anyMatch(p -> p.getStudentId().equals(studentId)
                    && p.getCourseId().equals(courseId)
                    && !RemedialPlan.COMPLETED.equals(p.getStatus()));
            if (open) continue;

            RemedialPlan plan = new RemedialPlan(IdGen.next("plan"), studentId, courseId, TimeUtil.today());
            for (SkillDomain d : AnalyticsService.weakDomains(studentId, courseId)) {
                plan.getWeakDomainIds().add(d.getDomainId());
            }
            // Review lessons = lessons that reinforce the weak domains (max 3).
            List<Lesson> review = JsonStore.get().db().lessons.stream()
                    .filter(l -> l.getCourseId().equals(courseId)
                            && plan.getWeakDomainIds().contains(l.getDomainId()))
                    .limit(3).toList();
            for (Lesson l : review) plan.getReviewLessonIds().add(l.getLessonId());

            // Attach the course's remedial quiz, if the instructor prepared one.
            JsonStore.get().db().quizzes.stream()
                    .filter(q -> q.isRemedial() && q.getCourseId().equals(courseId))
                    .findFirst()
                    .ifPresent(q -> plan.setRemedialQuizId(q.getQuizId()));

            plan.setNote("Auto-generated after quiz average fell below "
                    + ProgressService.PASSING_PCT + "%.");
            JsonStore.get().db().remedialPlans.add(plan);
            created++;
            DataAccess.audit().log(studentId, "REMEDIAL_PLAN_CREATED course=" + courseId);
        }
        if (created > 0) JsonStore.get().save();
        return created;
    }

    /** Learner marks a review lesson as re-studied. */
    public static void markReviewDone(String planId, String lessonId) {
        plan(planId).ifPresent(p -> {
            p.setStatus(RemedialPlan.IN_PROGRESS);
            ProgressService.markLessonComplete(p.getStudentId(), lessonId);
            DataAccess.audit().log(p.getStudentId(), "REMEDIAL_REVIEW lesson=" + lessonId);
            JsonStore.get().save();
        });
    }

    /** Passing the remedial quiz closes the plan. */
    public static void onRemedialSubmitted(String studentId, Quiz remedialQuiz, double pct) {
        for (RemedialPlan p : plansOf(studentId)) {
            if (p.getRemedialQuizId().equals(remedialQuiz.getQuizId())
                    && !RemedialPlan.COMPLETED.equals(p.getStatus())) {
                if (pct >= remedialQuiz.getPassingPct()) {
                    p.setStatus(RemedialPlan.COMPLETED);
                    p.setNote("Completed remedial quiz with " + pct + "%.");
                    DataAccess.audit().log(studentId, "REMEDIAL_PLAN_COMPLETED plan=" + p.getPlanId());
                } else {
                    p.setNote("Remedial quiz " + pct + "% - below " + remedialQuiz.getPassingPct()
                            + "%. Plan stays open.");
                }
            }
        }
        JsonStore.get().save();
    }

    public static void setStatus(String planId, String status) {
        plan(planId).ifPresent(p -> {
            p.setStatus(status);
            JsonStore.get().save();
        });
    }
}
