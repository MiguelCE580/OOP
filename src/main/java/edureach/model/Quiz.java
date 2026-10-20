package edureach.model;

/** Timed multiple-choice assessment. */
public class Quiz {
    private String quizId;
    private String courseId;
    private String title;
    private int timeLimitMin;
    private int passingPct;
    /** Remedial quizzes are simplified retakes assigned to failing students. */
    private boolean remedial = false;
    /** For remedial quizzes: the main quiz it retargets. */
    private String forQuizId = "";

    public Quiz() {}
    public Quiz(String quizId, String courseId, String title, int timeLimitMin, int passingPct) {
        this.quizId = quizId; this.courseId = courseId; this.title = title;
        this.timeLimitMin = timeLimitMin; this.passingPct = passingPct;
    }

    public String getQuizId() { return quizId; }
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public int getTimeLimitMin() { return timeLimitMin; }
    public int getPassingPct() { return passingPct; }
    public boolean isRemedial() { return remedial; }
    public void setRemedial(boolean remedial) { this.remedial = remedial; }
    public String getForQuizId() { return forQuizId; }
    public void setForQuizId(String forQuizId) { this.forQuizId = forQuizId; }
}
