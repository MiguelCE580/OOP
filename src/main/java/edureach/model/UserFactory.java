package edureach.model;

/**
 * Factory pattern (proposal 5.2): creates the correct User subtype.
 * Used by seeding and (TODO Phase 4) by registration / login hydration.
 */
public final class UserFactory {

    private UserFactory() {}

    public static User createUser(Role role, String userId, String username,
                                  String passwordHash, String fullName, String email) {
        switch (role) {
            case STUDENT:    return new StudentUser(userId, username, passwordHash, fullName, email);
            case INSTRUCTOR: return new InstructorUser(userId, username, passwordHash, fullName, email);
            case ADMIN:      return new AdminUser(userId, username, passwordHash, fullName, email);
            default: throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
