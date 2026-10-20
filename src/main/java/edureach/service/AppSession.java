package edureach.service;

import edureach.model.StudentUser;
import edureach.model.User;

/** Logged-in user + session-scoped state. */
public final class AppSession {

    private static User current;

    private AppSession() {}

    public static User current() { return current; }

    public static void signIn(User user) { current = user; }

    public static void signOut() { current = null; }

    public static StudentUser student() { return (StudentUser) current; }

    public static boolean isStudent()    { return current != null && current.getRole() == edureach.model.Role.STUDENT; }
    public static boolean isInstructor() { return current != null && current.getRole() == edureach.model.Role.INSTRUCTOR; }
    public static boolean isAdmin()      { return current != null && current.getRole() == edureach.model.Role.ADMIN; }
}
