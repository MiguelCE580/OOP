package edureach.data;

import edureach.data.file.FileAttendanceDAO;
import edureach.data.file.FileAuditLogDAO;
import edureach.data.file.FileCourseDAO;
import edureach.data.file.FileUserDAO;

/**
 * Factory for DAO implementations. Today every method returns the
 * single-file JSON implementation; in Phase 4 the same call sites will
 * receive MySQL implementations when {@link DatabaseManager#isRelationalEnabled()}.
 */
public final class DataAccess {

    private DataAccess() {}

    public static UserDAO users() {
        return DatabaseManager.isRelationalEnabled() ? null /* new MySqlUserDAO() */ : new FileUserDAO();
    }

    public static CourseDAO courses() {
        return DatabaseManager.isRelationalEnabled() ? null /* new MySqlCourseDAO() */ : new FileCourseDAO();
    }

    public static AttendanceDAO attendance() {
        return DatabaseManager.isRelationalEnabled() ? null /* new MySqlAttendanceDAO() */ : new FileAttendanceDAO();
    }

    public static AuditLogDAO audit() {
        return DatabaseManager.isRelationalEnabled() ? null /* new MySqlAuditLogDAO() */ : new FileAuditLogDAO();
    }
}
