package edureach.data;

/** Custom exception for storage errors (proposal 5.2 - graceful error handling). */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) { super(message); }
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
