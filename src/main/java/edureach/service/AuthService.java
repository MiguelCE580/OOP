package edureach.service;

import edureach.data.DataAccess;
import edureach.model.Role;
import edureach.model.User;
import edureach.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Login with role-based menus + temporary lockout (proposal P6).
 * Failure counters are in-memory for Phase 2-3; TODO(Phase 4): persist
 * lockout state in the users table so it survives restarts.
 */
public final class AuthService {

    public static final int MAX_ATTEMPTS = 3;
    public static final long LOCK_SECONDS = 60;

    private static final Map<String, Integer> FAILS = new HashMap<>();
    private static final Map<String, Long> LOCKED_UNTIL = new HashMap<>();

    private AuthService() {}

    public static User login(String username, String password, Role role) {
        String key = username.trim().toLowerCase();

        Long until = LOCKED_UNTIL.get(key);
        if (until != null && until > System.currentTimeMillis() / 1000) {
            throw new AccountLockedException(until - System.currentTimeMillis() / 1000);
        }

        User user = DataAccess.users().findByUsername(username)
                .orElseThrow(() -> {
                    registerFailure(key);
                    return new InvalidCredentialsException("No account with that username.");
                });

        if (user.getRole() != role) {
            registerFailure(key);
            throw new InvalidCredentialsException(
                    "That account is not registered as " + role.label() + ".");
        }
        if (!user.isActive()) {
            throw new InvalidCredentialsException("This account is inactive. See the administrator.");
        }
        if (!user.getPasswordHash().equals(TimeUtil.hashPassword(password))) {
            registerFailure(key);
            throw new InvalidCredentialsException("Incorrect password.");
        }

        FAILS.remove(key);
        LOCKED_UNTIL.remove(key);
        DataAccess.audit().log(user.getUserId(), "LOGIN_SUCCESS (" + role.label() + ")");
        return user;
    }

    private static void registerFailure(String key) {
        int fails = FAILS.merge(key, 1, Integer::sum);
        DataAccess.audit().log("-", "LOGIN_FAILED username=" + key + " attempt=" + fails);
        if (fails >= MAX_ATTEMPTS) {
            LOCKED_UNTIL.put(key, System.currentTimeMillis() / 1000 + LOCK_SECONDS);
            FAILS.put(key, 0);
            DataAccess.audit().log("-", "ACCOUNT_LOCKED username=" + key);
        }
    }

    public static void logout(User user) {
        if (user != null) DataAccess.audit().log(user.getUserId(), "LOGOUT");
    }
}
