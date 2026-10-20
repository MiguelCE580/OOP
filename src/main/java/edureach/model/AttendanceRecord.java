package edureach.model;

/** One attendance mark for a session date. */
public class AttendanceRecord {
    public static final String PRESENT = "P";
    public static final String ABSENT  = "A";
    public static final String LATE    = "L";

    private String attendanceId;
    private String enrollmentId;
    private String sessionDate;   // yyyy-MM-dd
    private String status = PRESENT;

    public AttendanceRecord() {}
    public AttendanceRecord(String attendanceId, String enrollmentId, String sessionDate, String status) {
        this.attendanceId = attendanceId; this.enrollmentId = enrollmentId;
        this.sessionDate = sessionDate; this.status = status;
    }

    public String getAttendanceId() { return attendanceId; }
    public String getEnrollmentId() { return enrollmentId; }
    public String getSessionDate() { return sessionDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
