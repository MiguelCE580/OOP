package edureach.data;

import edureach.model.AttendanceRecord;

import java.util.List;

public interface AttendanceDAO {
    List<AttendanceRecord> forSession(String courseId, String sessionDate);
    void saveSession(List<AttendanceRecord> records);
    int sessionsAttended(String enrollmentId);
}
