package edureach.model;

/**
 * Abstract base of the user hierarchy (proposal 5.2 - Inheritance).
 * Shared attributes and behavior live here; role-specific operations
 * are added only in the subclasses.
 */
public abstract class User {
    private String userId;
    private String username;
    private String passwordHash;   // encapsulated; TODO(Phase 4): switch SHA-256 -> salted BCrypt
    private String fullName;
    private String email;
    private Role role;
    private boolean active = true;

    protected User() {}

    protected User(String userId, String username, String passwordHash,
                   String fullName, String email, Role role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username must not be empty");
        this.username = username.trim();
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Full name must not be empty");
        this.fullName = fullName.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /** Initials used by the avatar circles in the UI. */
    public String initials() {
        String[] parts = fullName.trim().split("\\s+");
        String a = parts.length > 0 ? parts[0] : "";
        String b = parts.length > 1 ? parts[parts.length - 1] : "";
        return ((a.isEmpty() ? "" : a.substring(0, 1)) + (b.isEmpty() ? "" : b.substring(0, 1))).toUpperCase();
    }

    public abstract String homeScreenTitle();
}
