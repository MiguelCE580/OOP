package edureach.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal English / Filipino label bundle (proposal P7).
 * TODO(Phase 4): move to ResourceBundle properties files and cover all screens.
 */
public final class I18N {

    private static final Map<String, String> FIL = new HashMap<>();

    static {
        FIL.put("welcome", "Maligayang pagbabalik");
        FIL.put("signin", "Mag-sign in upang magpatuloy");
        FIL.put("login", "Mag-log in");
        FIL.put("username", "Username");
        FIL.put("password", "Password");
        FIL.put("nav.home", "Home");
        FIL.put("nav.courses", "Mga Kurso");
        FIL.put("nav.curriculum", "Kurikulum");
        FIL.put("nav.certs", "Mga Sertipiko");
        FIL.put("nav.help", "Tulong");
        FIL.put("nav.attendance", "Pasok");
        FIL.put("nav.grades", "Mga Grado");
        FIL.put("nav.analytics", "Analitika");
        FIL.put("nav.interventions", "Interbensyon");
        FIL.put("nav.courses.mgr", "Tagapamahala ng Kurso");
        FIL.put("nav.admin", "Administrasyon");
        FIL.put("greet", "Kumusta");
        FIL.put("mycourses", "Aking mga kurso");
        FIL.put("badges", "Mga Badge");
        FIL.put("mastery", "Kahusayan bawat skill domain");
        FIL.put("continue", "Ituloy ang lesson");
        FIL.put("highcontrast", "High contrast");
    }

    private I18N() {}

    public static String t(String key) {
        if ("FIL".equals(ThemeManager.config().getLang())) {
            return FIL.getOrDefault(key, key);
        }
        switch (key) {
            case "welcome": return "Welcome back";
            case "signin": return "Sign in to continue learning";
            case "login": return "Log In";
            case "username": return "Username";
            case "password": return "Password";
            case "nav.home": return "Home";
            case "nav.courses": return "Courses";
            case "nav.curriculum": return "Curriculum";
            case "nav.certs": return "Certificates";
            case "nav.help": return "Help";
            case "nav.attendance": return "Attendance";
            case "nav.grades": return "Grades";
            case "nav.analytics": return "Analytics";
            case "nav.interventions": return "Interventions";
            case "nav.courses.mgr": return "Course Manager";
            case "nav.admin": return "Administration";
            case "greet": return "Kumusta";
            case "mycourses": return "My courses";
            case "badges": return "Badges";
            case "mastery": return "Skill-domain mastery";
            case "continue": return "Continue lesson";
            case "highcontrast": return "High contrast";
            default: return key;
        }
    }
}
