package edureach.ui;

import edureach.data.JsonStore;
import edureach.model.Config;
import javafx.scene.Scene;

/**
 * Applies theme (light/dark), font scale and high-contrast mode from the
 * persisted {@link Config} (accessibility, proposal P7).
 */
public final class ThemeManager {

    private static Scene scene;

    private ThemeManager() {}

    public static void attach(Scene scene) {
        ThemeManager.scene = scene;
        scene.getStylesheets().add(
                ThemeManager.class.getResource("app.css").toExternalForm());
        refresh();
    }

    public static Config config() { return JsonStore.get().db().config; }

    public static void refresh() {
        if (scene == null) return;
        Config c = config();
        var roots = scene.getRoot().getStyleClass();
        roots.removeIf(s -> s.equals("dark") || s.equals("hc")
                || s.startsWith("fs"));
        if ("dark".equals(c.getTheme())) roots.add("dark");
        if (c.isHighContrast()) roots.add("hc");
        roots.add("fs" + c.getFontScale());
    }

    public static void toggleDark() {
        config().setTheme("dark".equals(config().getTheme()) ? "light" : "dark");
        JsonStore.get().save();
        refresh();
    }

    public static void bumpFont(int delta) {
        config().setFontScale(config().getFontScale() + delta);
        JsonStore.get().save();
        refresh();
    }

    public static void toggleContrast() {
        config().setHighContrast(!config().isHighContrast());
        JsonStore.get().save();
        refresh();
    }

    public static void toggleLang() {
        config().setLang("EN".equals(config().getLang()) ? "FIL" : "EN");
        JsonStore.get().save();
    }

    public static boolean isDark() { return "dark".equals(config().getTheme()); }
}
