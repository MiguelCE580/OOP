package edureach.data.file;

import edureach.data.AttendanceDAO;
import edureach.data.JsonStore;
import edureach.model.AttendanceRecord;
import edureach.model.DB;
import edureach.model.Enrollment;

import java.util.List;

/** Single-file implementation of {@link AttendanceDAO}. */
public class FileAttendanceDAO implements AttendanceDAO {

    @Override
    public List<AttendanceRecord> forSession(String courseId, String sessionDate) {
        DB db = JsonStore.get().db();
        List<String> enrollmentIds = db.enrollments.stream()
                .filter(e -> e.getCourseId().equals(courseId))
                .map(Enrollment::getEnrollmentId)
                .toList();
        return db.attendance.stream()
                .filter(a -> enrollmentIds.contains(a.getEnrollmentId())
                        && a.getSessionDate().equals(sessionDate))
                .toList();
    }

    @Override
    public void saveSession(List<AttendanceRecord> records) {
        DB db = JsonStore.get().db();
        for (AttendanceRecord r : records) {
            db.attendance.removeIf(a -> a.getEnrollmentId().equals(r.getEnrollmentId())
                    && a.getSessionDate().equals(r.getSessionDate()));
            db.attendance.add(r);
        }
        JsonStore.get().save();
    }

    @Override
    public int sessionsAttended(String enrollmentId) {
        return (int) JsonStore.get().db().attendance.stream()
                .filter(a -> a.getEnrollmentId().equals(enrollmentId))
                .filter(a -> !AttendanceRecord.ABSENT.equals(a.getStatus()))
                .count();
    }
}
