package edureach.service;

import edureach.data.JsonStore;
import edureach.model.*;
import edureach.util.IdGen;
import edureach.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quiz engine (proposal P2): randomized item order, timed attempts,
 * automatic scoring through the Gradable interface, instant feedback.
 */
public final class QuizService {

    private QuizService() {}

    /** One in-flight attempt (kept in memory until submitted). */
    public static final class Attempt {
        public final Quiz quiz;
        public final StudentUser student;
        public final List<Question> questions;
        public final Map<String, List<QuestionOption>> optionsByQuestion;
        public final Map<String, QuestionOption> correctByQuestion;
        public final long limitSeconds;
        public final long startedAt = System.currentTimeMillis();

        Attempt(Quiz quiz, StudentUser student, List<Question> questions,
                Map<String, List<QuestionOption>> options, Map<String, QuestionOption> correct,
                long limitSeconds) {
            this.quiz = quiz; this.student = student; this.questions = questions;
            this.optionsByQuestion = options; this.correctByQuestion = correct;
            this.limitSeconds = limitSeconds;
        }

        public boolean extended() { return student.isExtendedTime(); }
    }

    /** Feedback row shown on the results screen. */
    public record FeedbackRow(Question question, String yourAnswer, String correctAnswer, boolean correct) {}

    public static Attempt start(StudentUser student, Quiz quiz) {
        DB db = JsonStore.get().db();
        List<Question> questions = new ArrayList<>(db.questions.stream()
                .filter(q -> q.getQuizId().equals(quiz.getQuizId()))
                .toList());
        Collections.shuffle(questions);   // randomized order per attempt

        Map<String, List<QuestionOption>> options = new HashMap<>();
        Map<String, QuestionOption> correct = new HashMap<>();
        for (Question q : questions) {
            List<QuestionOption> opts = new ArrayList<>(db.options.stream()
                    .filter(o -> o.getQuestionId().equals(q.getQuestionId()))
                    .toList());
            options.put(q.getQuestionId(), opts);
            opts.stream().filter(QuestionOption::isCorrect).findFirst()
                    .ifPresent(o -> correct.put(q.getQuestionId(), o));
        }

        long limit = quiz.getTimeLimitMin() * 60L;
        if (student.isExtendedTime()) limit = (long) (limit * 1.5);  // PWD accommodation
        return new Attempt(quiz, student, questions, options, correct, limit);
    }

    public static Submission submit(Attempt attempt, Map<String, String> answers, long secondsUsed) {
        DB db = JsonStore.get().db();

        QuizModule module = new QuizModule(attempt.quiz, attempt.questions, answers, attempt.correctByQuestion);
        double obtained = module.calculateScore();
        double pct = module.scorePercent();

        String subId = IdGen.next("sub");
        Submission sub = new Submission(subId, attempt.student.getUserId(), attempt.quiz.getQuizId(),
                pct, (int) obtained, attempt.questions.size(),
                TimeUtil.mmss(secondsUsed), TimeUtil.stamp());
        db.submissions.add(sub);

        for (Question q : attempt.questions) {
            String selected = answers.getOrDefault(q.getQuestionId(), "");
            db.submissionAnswers.add(new SubmissionAnswer(subId, q.getQuestionId(), selected,
                    module.isCorrect(q.getQuestionId())));
        }
        JsonStore.get().save();
        auditSubmission(sub);

        // Remedial pass closes the intervention plan.
        if (attempt.quiz.isRemedial()) {
            RemedialService.onRemedialSubmitted(attempt.student.getUserId(), attempt.quiz, pct);
        }
        // Auto-issue certificate when a learner becomes certificate-ready.
        CertificateService.issueIfReady(attempt.student);
        return sub;
    }

    private static void auditSubmission(Submission sub) {
        edureach.data.DataAccess.audit().log(sub.getStudentId(),
                "QUIZ_SUBMITTED quiz=" + sub.getQuizId() + " score=" + sub.getScorePct() + "%");
    }

    public static List<FeedbackRow> feedback(Submission sub) {
        DB db = JsonStore.get().db();
        List<FeedbackRow> rows = new ArrayList<>();
        for (SubmissionAnswer sa : db.submissionAnswers.stream()
                .filter(a -> a.getSubId().equals(sub.getSubId())).toList()) {
            Question q = db.questions.stream()
                    .filter(x -> x.getQuestionId().equals(sa.getQuestionId())).findFirst().orElse(null);
            if (q == null) continue;
            String yours = db.options.stream()
                    .filter(o -> o.getOptionId().equals(sa.getSelectedOptionId()))
                    .map(QuestionOption::getOptionText).findFirst().orElse("(no answer)");
            String right = db.options.stream()
                    .filter(o -> o.getQuestionId().equals(q.getQuestionId()) && o.isCorrect())
                    .map(QuestionOption::getOptionText).findFirst().orElse("");
            rows.add(new FeedbackRow(q, yours, right, sa.isCorrect()));
        }
        return rows;
    }

    public static List<Quiz> quizzesOf(String courseId, boolean includeRemedial) {
        return JsonStore.get().db().quizzes.stream()
                .filter(q -> q.getCourseId().equals(courseId))
                .filter(q -> includeRemedial || !q.isRemedial())
                .toList();
    }

    public static Quiz byId(String quizId) {
        return JsonStore.get().db().quizzes.stream()
                .filter(q -> q.getQuizId().equals(quizId)).findFirst().orElse(null);
    }
}
