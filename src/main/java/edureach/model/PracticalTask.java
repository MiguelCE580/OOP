package edureach.model;

/**
 * Gradable implementation for instructor-graded practical tasks (rubric).
 * Polymorphism: the Progress Tracker treats this exactly like a QuizModule.
 */
public class PracticalTask implements Gradable {

    private final String courseId;
    private final String title;
    private final double maxScore;
    private double awarded;

    public PracticalTask(String courseId, String title, double maxScore) {
        this.courseId = courseId; this.title = title; this.maxScore = maxScore;
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }

    public double getAwarded() { return awarded; }
    public void setAwarded(double awarded) {
        if (awarded < 0 || awarded > maxScore)
            throw new IllegalArgumentException("Awarded score must be within 0.." + maxScore);
        this.awarded = awarded;
    }

    @Override
    public double calculateScore() { return awarded; }

    @Override
    public double getMaxScore() { return maxScore; }
}
