package edureach.service;

import edureach.data.JsonStore;
import edureach.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Detailed quiz checking (new module): per-item statistics across every
 * submission so instructors can see WHICH questions learners struggle with
 * and get a concrete suggestion on how to improve each one.
 */
public final class AnalyticsService {

    public static final double HARD_THRESHOLD_PCT = 50.0;

    public static final class QuestionStat {
        public final Question question;
        public final int attempts;
        public final int correct;
        public final double pct;
        public final String mostMissedOption;   // most-picked wrong choice

        QuestionStat(Question question, int attempts, int correct, String mostMissedOption) {
            this.question = question;
            this.attempts = attempts;
            this.correct = correct;
            this.pct = attempts == 0 ? 0 : Math.round(correct * 10000.0 / attempts) / 100.0;
            this.mostMissedOption = mostMissedOption;
        }

        public boolean isHard() { return attempts > 0 && pct < HARD_THRESHOLD_PCT; }
    }

    private AnalyticsService() {}

    public static List<QuestionStat> statsForQuiz(String quizId) {
        DB db = JsonStore.get().db();
        List<QuestionStat> stats = new ArrayList<>();
        for (Question q : db.questions.stream()
                .filter(x -> x.getQuizId().equals(quizId)).toList()) {

            List<SubmissionAnswer> answers = db.submissionAnswers.stream()
                    .filter(a -> a.getQuestionId().equals(q.getQuestionId())).toList();
            int attempts = answers.size();
            int correct = (int) answers.stream().filter(SubmissionAnswer::isCorrect).count();

            Map<String, Integer> wrongPicks = new HashMap<>();
            for (SubmissionAnswer a : answers) {
                if (!a.isCorrect()) {
                    wrongPicks.merge(a.getSelectedOptionId(), 1, Integer::sum);
                }
            }
            String missed = wrongPicks.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(e -> db.options.stream()
                            .filter(o -> o.getOptionId().equals(e.getKey()))
                            .map(QuestionOption::getOptionText).findFirst().orElse(""))
                    .orElse("");

            stats.add(new QuestionStat(q, attempts, correct, missed));
        }
        return stats;
    }

    public static List<QuestionStat> hardQuestions(String quizId) {
        return statsForQuiz(quizId).stream().filter(QuestionStat::isHard)
                .sorted(Comparator.comparingDouble(s -> s.pct))
                .toList();
    }

    /** Auto-generated improvement suggestion for a flagged question. */
    public static String suggestion(QuestionStat stat) {
        if (stat.attempts == 0) return "Not attempted yet - schedule it in the next session.";
        if (stat.pct < 35) {
            return "Re-teach the topic in class, then reword this item and add a worked example.";
        }
        if (stat.pct < HARD_THRESHOLD_PCT) {
            String base = "Review in class and add 2-3 practice items on this skill.";
            return stat.mostMissedOption.isBlank() ? base
                    : base + " Discuss why \"" + stat.mostMissedOption + "\" is a tempting wrong answer.";
        }
        return "Item is performing well - keep as is.";
    }

    /** Learners whose course average is below the passing mark. */
    public static List<Map.Entry<User, Double>> strugglingStudents(String courseId) {
        DB db = JsonStore.get().db();
        List<Map.Entry<User, Double>> out = new ArrayList<>();
        for (Enrollment e : db.enrollments.stream()
                .filter(x -> x.getCourseId().equals(courseId)).toList()) {
            User student = db.users.stream()
                    .filter(u -> u.getUserId().equals(e.getStudentId())).findFirst().orElse(null);
            if (student == null) continue;
            Double avg = ProgressService.courseAverage(student.getUserId(), courseId);
            if (avg != null && avg < ProgressService.PASSING_PCT) {
                out.add(Map.entry(student, avg));
            }
        }
        return out.stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).toList();
    }

    /** Weakest domains of one learner inside a course (for remedial plans). */
    public static List<SkillDomain> weakDomains(String studentId, String courseId) {
        DB db = JsonStore.get().db();
        List<String> domainIds = db.questions.stream()
                .filter(q -> db.quizzes.stream().anyMatch(z ->
                        z.getQuizId().equals(q.getQuizId()) && z.getCourseId().equals(courseId)))
                .map(Question::getDomainId).distinct().toList();
        List<SkillDomain> weak = new ArrayList<>();
        for (String d : domainIds) {
            Double m = ProgressService.domainMastery(studentId, d);
            if (m == null || m < ProgressService.PASSING_PCT) {
                db.domains.stream().filter(x -> x.getDomainId().equals(d))
                        .findFirst().ifPresent(weak::add);
            }
        }
        return weak;
    }
}
