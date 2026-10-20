package edureach.data;

import edureach.model.User;

import java.util.List;
import java.util.Optional;

/**
 * DAO abstraction (proposal 5.2 - Abstraction pillar).
 * The JavaFX controllers never touch SQL/JSON directly; they call these
 * interfaces. Phase 2-3 ships the File* implementations; Phase 4 adds
 * MySql* implementations on top of the very same contracts.
 */
public interface UserDAO {
    List<User> findAll();
    Optional<User> findByUsername(String username);
    Optional<User> findById(String userId);
    void save(User user);
}
