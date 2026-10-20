package edureach.model;

/** Volunteer / staff teacher account. Adds course + grading operations. */
public class InstructorUser extends User {

    private String specialization = "";

    public InstructorUser() { super(); setRole(Role.INSTRUCTOR); }

    public InstructorUser(String userId, String username, String passwordHash,
                          String fullName, String email) {
        super(userId, username, passwordHash, fullName, email, Role.INSTRUCTOR);
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    @Override
    public String homeScreenTitle() { return "Course Manager"; }
}
