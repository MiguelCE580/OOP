package edureach.model;

/** Persisted rubric score of a practical task (gradebook "Practical /10" column). */
public class PracticalScore {
    private String scoreId;
    private String studentId;
    private String courseId;
    private double score;
    private double maxScore = 10;
    private String gradedBy;

    public PracticalScore() {}
    public PracticalScore(String scoreId, String studentId, String courseId,
                          double score, String gradedBy) {
        this.scoreId = scoreId; this.studentId = studentId;
        this.courseId = courseId; this.score = score; this.gradedBy = gradedBy;
    }

    public String getScoreId() { return scoreId; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public double getScore() { return score; }
    public double getMaxScore() { return maxScore; }
    public String getGradedBy() { return gradedBy; }
}
