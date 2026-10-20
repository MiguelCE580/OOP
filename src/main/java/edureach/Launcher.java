package edureach;

/**
 * Classpath-friendly launcher (java -cp ... edureach.Launcher).
 * `mvn javafx:run` uses Main directly; this wrapper exists for plain
 * classpath runs where the JavaFX module check would otherwise fail.
 */
public final class Launcher {
    private Launcher() {}
    public static void main(String[] args) {
        Main.main(args);
    }
}
