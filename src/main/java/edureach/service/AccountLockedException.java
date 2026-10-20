package edureach.service;

/** Temporary lockout after repeated failed attempts (proposal P6). */
public class AccountLockedException extends RuntimeException {
    private final long secondsLeft;

    public AccountLockedException(long secondsLeft) {
        super("Account temporarily locked. Try again in " + secondsLeft + "s.");
        this.secondsLeft = secondsLeft;
    }

    public long getSecondsLeft() { return secondsLeft; }
}
