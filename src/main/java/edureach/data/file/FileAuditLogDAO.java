package edureach.data.file;

import edureach.data.AuditLogDAO;
import edureach.data.JsonStore;
import edureach.model.AuditEntry;
import edureach.model.DB;
import edureach.util.IdGen;
import edureach.util.TimeUtil;

import java.util.Comparator;
import java.util.List;

/** Single-file implementation of {@link AuditLogDAO}. */
public class FileAuditLogDAO implements AuditLogDAO {

    @Override
    public void log(String userId, String action) {
        DB db = JsonStore.get().db();
        db.auditLogs.add(new AuditEntry(IdGen.next("log"), userId, action, TimeUtil.stamp()));
        JsonStore.get().save();
    }

    @Override
    public List<AuditEntry> recent(int limit) {
        return JsonStore.get().db().auditLogs.stream()
                .sorted(Comparator.comparing(AuditEntry::getLoggedAt).reversed())
                .limit(limit)
                .toList();
    }
}
