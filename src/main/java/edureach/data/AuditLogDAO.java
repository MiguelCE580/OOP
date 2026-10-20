package edureach.data;

import edureach.model.AuditEntry;

import java.util.List;

public interface AuditLogDAO {
    void log(String userId, String action);
    List<AuditEntry> recent(int limit);
}
