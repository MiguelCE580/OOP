package edureach.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Root aggregate persisted as ONE JSON file (data/edureach.json).
 * TODO(Phase 4 / remaining 30%): this aggregate moves to MySQL 8.0 tables
 * (users, courses, lessons, enrollments, attendance, quizzes, questions,
 *  question_options, submissions, submission_answers, student_badges,
 *  certificates, audit_logs) behind the same DAO interfaces.
 */
public class DB {
    public Config config = new Config();

    public List<User> users = new ArrayList<>();
    public List<SkillDomain> domains = new ArrayList<>();
    public List<Course> courses = new ArrayList<>();
    public List<Lesson> lessons = new ArrayList<>();
    public List<Enrollment> enrollments = new ArrayList<>();
    public List<AttendanceRecord> attendance = new ArrayList<>();
    public List<Quiz> quizzes = new ArrayList<>();
    public List<Question> questions = new ArrayList<>();
    public List<QuestionOption> options = new ArrayList<>();
    public List<Submission> submissions = new ArrayList<>();
    public List<SubmissionAnswer> submissionAnswers = new ArrayList<>();
    public List<PracticalScore> practicalScores = new ArrayList<>();
    public List<Certificate> certificates = new ArrayList<>();
    public List<AuditEntry> auditLogs = new ArrayList<>();
    public List<CurriculumTopic> curriculumTopics = new ArrayList<>();
    public List<CurriculumFollow> curriculumFollows = new ArrayList<>();
    public List<RemedialPlan> remedialPlans = new ArrayList<>();
}
