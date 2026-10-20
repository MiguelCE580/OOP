package edureach;

/** Launcher wrapper so the harness can run from a plain classpath. */
public final class HarnessLauncher {
    private HarnessLauncher() {}
    public static void main(String[] args) {
        ScreenshotHarness.main(args);
    }
}
