package edureach.model;

/** Persisted UI preferences (kept inside the single data file). */
public class Config {
    private String theme = "light";          // light | dark
    private int fontScale = 1;               // 0..3
    private boolean highContrast = false;
    private String lang = "EN";              // EN | FIL

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public int getFontScale() { return fontScale; }
    public void setFontScale(int fontScale) { this.fontScale = Math.max(0, Math.min(3, fontScale)); }
    public boolean isHighContrast() { return highContrast; }
    public void setHighContrast(boolean highContrast) { this.highContrast = highContrast; }
    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }
}
