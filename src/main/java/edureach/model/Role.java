package edureach.model;

/** System roles (role-based access control, proposal P6). */
public enum Role {
    STUDENT, INSTRUCTOR, ADMIN;

    public String label() {
        switch (this) {
            case STUDENT: return "Student";
            case INSTRUCTOR: return "Instructor";
            case ADMIN: return "Administrator";
            default: return name();
        }
    }
}
