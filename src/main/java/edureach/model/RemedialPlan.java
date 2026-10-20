package edureach.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Intervention record for a failing student (new module):
 * captures weak skill domains, review lessons to re-study and the
 * simplified remedial quiz assigned to recover the course.
 */
public class RemedialPlan {
    public static final String OPEN = "OPEN";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String COMPLETED = "COMPLETED";

    private String planId;
    private String studentId;
    private String courseId;
    private List<String> weakDomainIds = new ArrayList<>();
    private List<String> reviewLessonIds = new ArrayList<>();
    private String remedialQuizId = "";
    private String status = OPEN;
    private String createdOn;
    private String note = "";

    public RemedialPlan() {}
    public RemedialPlan(String planId, String studentId, String courseId, String createdOn) {
        this.planId = planId; this.studentId = studentId;
        this.courseId = courseId; this.createdOn = createdOn;
    }

    public String getPlanId() { return planId; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public List<String> getWeakDomainIds() { return weakDomainIds; }
    public List<String> getReviewLessonIds() { return reviewLessonIds; }
    public String getRemedialQuizId() { return remedialQuizId; }
    public void setRemedialQuizId(String remedialQuizId) { this.remedialQuizId = remedialQuizId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedOn() { return createdOn; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
