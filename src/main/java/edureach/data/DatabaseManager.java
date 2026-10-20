package edureach.data;

/**
 * Phase 4 placeholder (remaining 30%): pooled MySQL 8.0 connections.
 * Nothing in the app depends on MySQL yet - the File DAOs below serve the
 * single JSON document. When the database lands, flip the factory methods
 * in {@link DataAccess} to the MySql* implementations.
 */
public final class DatabaseManager {

    // TODO(Phase 4): HikariCP pool, e.g.
    //   jdbc:mysql://localhost:3306/edureach?useSSL=false
    //   pool size 5, PreparedStatement everywhere (zero string-built SQL),
    //   schema from docs/schema.sql (3NF, FKs, indexes on username,
    //   verification_code and all FK columns).

    private DatabaseManager() {}

    public static boolean isRelationalEnabled() {
        return false;   // flip to true once the MySQL DAOs are wired
    }
}
