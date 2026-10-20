package edureach.model;

/** Center coordinator / system administrator account. */
public class AdminUser extends User {

    public AdminUser() { super(); setRole(Role.ADMIN); }

    public AdminUser(String userId, String username, String passwordHash,
                     String fullName, String email) {
        super(userId, username, passwordHash, fullName, email, Role.ADMIN);
    }

    @Override
    public String homeScreenTitle() { return "Administration"; }
}
