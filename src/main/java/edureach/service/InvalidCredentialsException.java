package edureach.service;

/** Wrong username/password or wrong role for the account. */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) { super(message); }
}
