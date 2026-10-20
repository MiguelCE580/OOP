package edureach.model;

/** A quiz attempt by a student. */
public class Submission {
    private String subId;
    private String studentId;
    private String quizId;
    private double scorePct;
    private int correctCount;
    private int totalItems;
    private String timeUsed;      // mm:ss
    private String submittedAt;   // yyyy-MM-dd HH:mm

    public Submission() {}
    public Submission(String subId, String studentId, String quizId, double scorePct,
                      int correctCount, int totalItems, String timeUsed, String submittedAt) {
        this.subId = subId; this.studentId = studentId; this.quizId = quizId;
        this.scorePct = scorePct; this.correctCount = correctCount;
        this.totalItems = totalItems; this.timeUsed = timeUsed; this.submittedAt = submittedAt;
    }

    public String getSubId() { return subId; }
    public String getStudentId() { return studentId; }
    public String getQuizId() { return quizId; }
    public double getScorePct() { return scorePct; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalItems() { return totalItems; }
    public String getTimeUsed() { return timeUsed; }
    public String getSubmittedAt() { return submittedAt; }
}
