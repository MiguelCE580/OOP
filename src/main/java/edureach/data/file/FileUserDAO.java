package edureach.data.file;

import edureach.data.JsonStore;
import edureach.data.UserDAO;
import edureach.model.User;

import java.util.List;
import java.util.Optional;

/** Single-file implementation of {@link UserDAO}. */
public class FileUserDAO implements UserDAO {

    @Override
    public List<User> findAll() {
        return JsonStore.get().db().users;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(String userId) {
        return findAll().stream().filter(u -> u.getUserId().equals(userId)).findFirst();
    }

    @Override
    public void save(User user) {
        List<User> users = findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                JsonStore.get().save();
                return;
            }
        }
        users.add(user);
        JsonStore.get().save();
    }
}
