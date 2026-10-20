package edureach.service;

import edureach.data.JsonStore;
import edureach.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Progress Tracker (proposal P3): completion %, averages, skill-domain
 * mastery, badges and the course status used by the gradebook.
 */
public final class ProgressService {

    public static final int PASSING_PCT = 70;
    public static final int CERT_READY_PCT = 85;
    public static final int MASTERY_PCT = 80;

    public static final String ST_NEW = "NEW";
    public static final String ST_NEEDS_REVIEW = "NEEDS_REVIEW";
    public static final String ST_PASSING = "PASSING";
    public static final String ST_CERT_READY = "CERT_READY";

    private ProgressService() {}

    public static Optional<Enrollment> enrollment(String studentId, String courseId) {
        return JsonStore.get().db().enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId))
                .findFirst();
    }

    public static List<Enrollment> enrollmentsOf(String studentId) {
        return JsonStore.get().db().enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId)).toList();
    }

    public static Course course(String courseId) {
        return JsonStore.get().db().courses.stream()
                .filter(c -> c.getCourseId().equals(courseId)).findFirst().orElse(null);
    }

    public static int completionPct(String studentId, String courseId) {
        List<Lesson> lessons = JsonStore.get().db().lessons.stream()
                .filter(l -> l.getCourseId().equals(courseId)).toList();
        if (lessons.isEmpty()) return 0;
        long done = enrollment(studentId, courseId)
                .map(e -> lessons.stream().filter(l -> e.getCompletedLessons().contains(l.getLessonId())).count())
                .orElse(0L);
        return (int) Math.round(done * 100.0 / lessons.size());
    }

    /** Latest submission per (non-remedial) quiz of the course. */
    public static Map<String, Submission> latestPerQuiz(String studentId, String courseId) {
        Map<String, Submission> latest = new HashMap<>();
        for (Quiz q : QuizService.quizzesOf(courseId, false)) {
            JsonStore.get().db().submissions.stream()
                    .filter(s -> s.getStudentId().equals(studentId) && s.getQuizId().equals(q.getQuizId()))
                    .reduce((a, b) -> b)
                    .ifPresent(s -> latest.put(q.getQuizId(), s));
        }
        return latest;
    }

    /** Overall course grade: mean of latest quiz scores + practical score. */
    public static Double courseAverage(String studentId, String courseId) {
        List<Double> parts = new java.util.ArrayList<>();
        latestPerQuiz(studentId, courseId).values().forEach(s -> parts.add(s.getScorePct()));
        JsonStore.get().db().practicalScores.stream()
                .filter(p -> p.getStudentId().equals(studentId) && p.getCourseId().equals(courseId))
                .findFirst()
                .ifPresent(p -> parts.add(p.getScore() / p.getMaxScore() * 100.0));
        if (parts.isEmpty()) return null;
        return parts.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public static Double avgQuizScore(String studentId) {
        List<Double> scores = JsonStore.get().db().submissions.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .map(Submission::getScorePct).toList();
        if (scores.isEmpty()) return null;
        return scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /** % of answered items correct within one skill domain, across all attempts. */
    public static Double domainMastery(String studentId, String domainId) {
        DB db = JsonStore.get().db();
        List<String> domainQuestions = db.questions.stream()
                .filter(q -> q.getDomainId().equals(domainId))
                .map(Question::getQuestionId).toList();
        if (domainQuestions.isEmpty()) return null;
        List<SubmissionAnswer> mine = db.submissionAnswers.stream()
                .filter(a -> domainQuestions.contains(a.getQuestionId()))
                .filter(a -> db.submissions.stream().anyMatch(s ->
                        s.getSubId().equals(a.getSubId()) && s.getStudentId().equals(studentId)))
                .toList();
        if (mine.isEmpty()) return null;
        long correct = mine.stream().filter(SubmissionAnswer::isCorrect).count();
        return Math.round(correct * 10000.0 / mine.size()) / 100.0;
    }

    public static List<SkillDomain> earnedBadges(String studentId) {
        return JsonStore.get().db().domains.stream()
                .filter(d -> {
                    Double m = domainMastery(studentId, d.getDomainId());
                    return m != null && m >= MASTERY_PCT;
                })
                .toList();
    }

    public static String status(String studentId, String courseId) {
        Double avg = courseAverage(studentId, courseId);
        if (avg == null) return ST_NEW;
        if (avg < PASSING_PCT) return ST_NEEDS_REVIEW;
        if (avg >= CERT_READY_PCT && completionPct(studentId, courseId) >= 100) return ST_CERT_READY;
        return ST_PASSING;
    }

    public static String statusLabel(String status) {
        switch (status) {
            case ST_NEEDS_REVIEW: return "Needs review";
            case ST_PASSING: return "Passing";
            case ST_CERT_READY: return "Certificate ready";
            default: return "Not started";
        }
    }

    public static void markLessonComplete(String studentId, String lessonId) {
        DB db = JsonStore.get().db();
        Lesson lesson = db.lessons.stream().filter(l -> l.getLessonId().equals(lessonId))
                .findFirst().orElse(null);
        if (lesson == null) return;
        enrollment(studentId, lesson.getCourseId()).ifPresent(e -> {
            if (!e.getCompletedLessons().contains(lessonId)) {
                e.getCompletedLessons().add(lessonId);
                JsonStore.get().save();
            }
        });
    }
}
