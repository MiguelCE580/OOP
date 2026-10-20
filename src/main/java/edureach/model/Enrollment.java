package edureach.model;

import java.util.ArrayList;
import java.util.List;

/** Student-course link with status and completed-lesson tracking. */
public class Enrollment {
    private String enrollmentId;
    private String studentId;
    private String courseId;
    private String status = "ENROLLED";   // ENROLLED | COMPLETED
    private String enrolledOn;
    private List<String> completedLessons = new ArrayList<>();

    public Enrollment() {}
    public Enrollment(String enrollmentId, String studentId, String courseId, String enrolledOn) {
        this.enrollmentId = enrollmentId; this.studentId = studentId;
        this.courseId = courseId; this.enrolledOn = enrolledOn;
    }

    public String getEnrollmentId() { return enrollmentId; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEnrolledOn() { return enrolledOn; }
    public List<String> getCompletedLessons() { return completedLessons; }
}
