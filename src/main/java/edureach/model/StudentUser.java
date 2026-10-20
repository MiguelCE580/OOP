package edureach.model;

/** Learner account. Adds assessment + progress operations (proposal 5.2). */
public class StudentUser extends User {

    /** PWD accommodation: +50% time on timed quizzes (proposal P7). */
    private boolean extendedTime = false;

    /** Suggested K-12 level, used by the optional Curriculum module. */
    private String gradeLevel = "";

    public StudentUser() { super(); setRole(Role.STUDENT); }

    public StudentUser(String userId, String username, String passwordHash,
                       String fullName, String email) {
        super(userId, username, passwordHash, fullName, email, Role.STUDENT);
    }

    public boolean isExtendedTime() { return extendedTime; }
    public void setExtendedTime(boolean extendedTime) { this.extendedTime = extendedTime; }

    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }

    @Override
    public String homeScreenTitle() { return "Dashboard"; }
}
