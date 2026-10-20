package edureach.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import edureach.model.DB;
import edureach.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Singleton store for the single-file persistence of Phase 2-3.
 * Everything (records + UI config) lives in ONE json document.
 *
 * TODO(Phase 4 / remaining 30%): keep this class as the fallback / offline cache
 * and route the DAO interfaces to MySQL 8 via DatabaseManager + HikariCP.
 */
public final class JsonStore {

    private static JsonStore instance;

    private final Path file;
    private final Gson gson;
    private DB db;

    private JsonStore(Path file) {
        this.file = file;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(User.class, new UserAdapter())
                .create();
    }

    public static synchronized JsonStore get() {
        if (instance == null) {
            String path = System.getProperty("edureach.data", "data/edureach.json");
            instance = new JsonStore(Path.of(path));
        }
        return instance;
    }

    /** Test / harness hook: point the store at another file. */
    public static synchronized void useForTesting(Path path) {
        instance = new JsonStore(path);
    }

    public synchronized DB db() {
        if (db == null) load();
        return db;
    }

    private void load() {
        if (Files.exists(file)) {
            try {
                String json = Files.readString(file, StandardCharsets.UTF_8);
                db = gson.fromJson(json, DB.class);
                if (db != null) return;
            } catch (IOException e) {
                throw new DataAccessException("Could not read " + file, e);
            }
        }
        db = new DB();
    }

    /** Replaces the whole aggregate and writes it to the single file. */
    public synchronized void install(DB seeded) {
        this.db = seeded;
        save();
    }

    public synchronized void save() {
        try {
            if (file.getParent() != null) Files.createDirectories(file.getParent());
            Files.writeString(file, gson.toJson(db), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataAccessException("Could not write " + file, e);
        }
    }

    public Path file() { return file; }

    /** Rebuilds the concrete User subtype from the stored "role" discriminator. */
    private static final class UserAdapter implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type type, JsonDeserializationContext ctx)
                throws JsonParseException {
            JsonObject o = json.getAsJsonObject();
            String role = o.has("role") ? o.get("role").getAsString() : "STUDENT";
            Class<? extends User> cls = switch (role) {
                case "INSTRUCTOR" -> edureach.model.InstructorUser.class;
                case "ADMIN" -> edureach.model.AdminUser.class;
                default -> edureach.model.StudentUser.class;
            };
            return ctx.deserialize(json, cls);
        }
    }
}
