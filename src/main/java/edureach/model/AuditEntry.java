package edureach.model;

/** Security / activity trail (proposal P6). Appended on logins and privileged actions. */
public class AuditEntry {
    private String logId;
    private String userId;
    private String action;
    private String loggedAt;

    public AuditEntry() {}
    public AuditEntry(String logId, String userId, String action, String loggedAt) {
        this.logId = logId; this.userId = userId; this.action = action; this.loggedAt = loggedAt;
    }

    public String getLogId() { return logId; }
    public String getUserId() { return userId; }
    public String getAction() { return action; }
    public String getLoggedAt() { return loggedAt; }
}
