package edureach.service;

/** Raised when a learner is enrolled twice in the same course. */
public class DuplicateEnrollmentException extends RuntimeException {
    public DuplicateEnrollmentException(String message) { super(message); }
}
