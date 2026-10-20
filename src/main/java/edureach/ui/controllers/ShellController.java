package edureach.ui.controllers;

import edureach.model.Role;
import edureach.model.User;
import edureach.service.AppSession;
import edureach.ui.I18N;
import edureach.ui.Screen;
import edureach.ui.ThemeManager;
import edureach.ui.Ui;
import edureach.ui.ViewLoader;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * App frame: gradient sidebar, breadcrumb + accessibility bar, role tab bar
 * and the content area that swaps FXML screens.
 */
public class ShellController {

    @FXML private VBox sidebar;
    @FXML private HBox tabBar, a11yBox;
    @FXML private Label crumbPrev, crumbHere;
    @FXML private StackPane contentPane;

    private static ShellController current;
    private String lastView = "";
    private String lastParam = "";

    private record NavItem(String glyph, String labelKey, String view) {}

    // ---------------- bootstrap ----------------

    public static void show(Scene scene, User user) {
        ViewLoader.LoadResult<ShellController> shell = ViewLoader.load("shell");
        scene.setRoot(shell.root());
        ThemeManager.refresh();   // new root must inherit the persisted theme
        current = shell.controller();
        current.buildChrome(user);
        current.open(homeView(user.getRole()), null);
    }

    public static void open(String view, String param) {
        if (current != null) current.doOpen(view, param);
    }

    public static void refreshChrome() {
        if (current != null && AppSession.current() != null) {
            current.buildChrome(AppSession.current());
            current.doOpen(current.lastView, current.lastParam);
        }
    }

    private static String homeView(Role role) {
        switch (role) {
            case INSTRUCTOR: return "course_manager";
            case ADMIN: return "admin";
            default: return "dashboard";
        }
    }

    // ---------------- chrome ----------------

    private void buildChrome(User user) {
        sidebar.getChildren().clear();
        Label logo = Ui.label("ER", "logo-badge");
        VBox logoWrap = new VBox(logo);
        logoWrap.setAlignment(Pos.CENTER);
        sidebar.getChildren().add(logoWrap);

        for (NavItem item : navOf(user.getRole())) {
            sidebar.getChildren().add(navButton(item));
        }
        sidebar.getChildren().add(Ui.vbox(10));
        javafx.scene.layout.VBox.setVgrow(sidebar.getChildren().get(sidebar.getChildren().size() - 1),
                javafx.scene.layout.Priority.ALWAYS);
        sidebar.getChildren().add(navButton(new NavItem("?", "nav.help", "help")));

        buildA11y();
        buildTabs(user.getRole());
    }

    private List<NavItem> navOf(Role role) {
        List<NavItem> items = new ArrayList<>();
        switch (role) {
            case STUDENT -> {
                items.add(new NavItem("⌂", "nav.home", "dashboard"));
                items.add(new NavItem("▤", "nav.courses", "mycourses"));
                items.add(new NavItem("◆", "nav.curriculum", "curriculum"));
                items.add(new NavItem("★", "nav.certs", "certificates"));
            }
            case INSTRUCTOR -> {
                items.add(new NavItem("⌂", "nav.courses.mgr", "course_manager"));
                items.add(new NavItem("✓", "nav.attendance", "attendance"));
                items.add(new NavItem("▦", "nav.grades", "gradebook"));
                items.add(new NavItem("◔", "nav.analytics", "analytics"));
                items.add(new NavItem("!", "nav.interventions", "interventions"));
                items.add(new NavItem("★", "nav.certs", "certificates"));
            }
            case ADMIN -> {
                items.add(new NavItem("⚙", "nav.admin", "admin"));
                items.add(new NavItem("★", "nav.certs", "certificates"));
            }
        }
        return items;
    }

    private Button navButton(NavItem item) {
        Label glyph = Ui.label(item.glyph(), "nav-glyph");
        Label text = Ui.label(I18N.t(item.labelKey()));
        text.setWrapText(true);
        text.setMaxWidth(74);
        text.setStyle("-fx-text-alignment: center;");
        VBox stack = new VBox(2, glyph, text);
        stack.setAlignment(Pos.CENTER);
        Button b = new Button();
        b.setGraphic(stack);
        b.getStyleClass().add("nav-btn");
        b.setUserData(item.view());
        b.setOnAction(e -> {
            if ("help".equals(item.view())) {
                Ui.info("EduReach help",
                        "EduReach v0.7 (Phase 2-3 build).\n\n"
                      + "Keyboard: Tab / Shift+Tab move focus, Enter activates.\n"
                      + "Accessibility bar (top right): font size, high contrast,\n"
                      + "English/Filipino labels and dark mode.\n\n"
                      + "Demo accounts:\n  maria.santos / learn123 (student)\n"
                      + "  ms.reyes / teach123 (instructor)\n  admin / admin123 (admin)");
            } else {
                open(item.view(), null);
            }
        });
        return b;
    }

    private void buildA11y() {
        a11yBox.getChildren().clear();
        Button minus = Ui.button("A-", "btn-chip");
        minus.setOnAction(e -> ThemeManager.bumpFont(-1));
        Button plus = Ui.button("A+", "btn-chip");
        plus.setOnAction(e -> ThemeManager.bumpFont(1));
        Button contrast = Ui.button(
                (ThemeManager.config().isHighContrast() ? "✓ " : "") + I18N.t("highcontrast"), "btn-chip");
        contrast.setOnAction(e -> { ThemeManager.toggleContrast(); buildA11y(); });
        Button lang = Ui.button("EN | FIL", "btn-chip");
        lang.setOnAction(e -> { ThemeManager.toggleLang(); refreshChrome(); });
        Button theme = Ui.button(ThemeManager.isDark() ? "☀ Light" : "☾ Dark", "btn-chip");
        theme.setOnAction(e -> { ThemeManager.toggleDark(); buildA11y(); });
        Button logout = Ui.button("Logout", "btn-chip");
        logout.setOnAction(e -> {
            edureach.service.AuthService.logout(AppSession.current());
            AppSession.signOut();
            ViewLoader.LoadResult<?> login = ViewLoader.load("login");
            contentPane.getScene().setRoot(login.root());
        });
        a11yBox.getChildren().addAll(minus, plus, contrast, lang, theme, logout);
    }

    private void buildTabs(Role role) {
        tabBar.getChildren().clear();
        if (role == Role.STUDENT || role == Role.ADMIN) {
            tabBar.setVisible(false);
            tabBar.setManaged(false);
            return;
        }
        tabBar.setVisible(true);
        tabBar.setManaged(true);
        Object[][] tabs = {
                {"Home", "course_manager"}, {"Announcements", "phase:Announcements"},
                {"Modules", "course_manager"}, {"Quizzes", "analytics"},
                {"Item Banks", "phase:Item Banks"}, {"Attendance", "attendance"},
                {"Grades", "gradebook"}, {"Certificates", "certificates"},
                {"People", "phase:People"}, {"Interventions", "interventions"},
        };
        for (Object[] t : tabs) {
            String label = (String) t[0];
            String view = (String) t[1];
            Button b = Ui.button(label, "tab");
            b.setUserData(view);
            b.setOnAction(e -> {
                if (view.startsWith("phase:")) {
                    Ui.phase4(label + " screen");
                } else {
                    open(view, null);
                }
            });
            tabBar.getChildren().add(b);
        }
    }

    // ---------------- navigation ----------------

    public void doOpen(String view, String param) {
        lastView = view;
        lastParam = param == null ? "" : param;

        ViewLoader.LoadResult<Object> res = ViewLoader.load(fxmlOf(view));
        Node content = res.root();
        if (res.controller() instanceof Screen s) s.init(lastParam);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentPane.getChildren().setAll(sp);

        crumbPrev.setText(crumbContext(view));
        crumbHere.setText(titleOf(view));
        highlight(view);
    }

    private String fxmlOf(String view) {
        switch (view) {
            case "dashboard": return "student_dashboard";
            case "mycourses": return "student_course";
            case "course": return "student_course";
            case "quiz_take": return "quiz_take";
            case "quiz_results": return "quiz_results";
            default: return view;
        }
    }

    private String crumbContext(String view) {
        User u = AppSession.current();
        if (u == null) return "";
        if (view.startsWith("quiz")) return "Basic Computer Use";
        return u.getRole().label();
    }

    private String titleOf(String view) {
        switch (view) {
            case "dashboard": return I18N.t("nav.home");
            case "mycourses": return I18N.t("nav.courses");
            case "course": return I18N.t("nav.courses");
            case "curriculum": return I18N.t("nav.curriculum");
            case "certificates": return I18N.t("nav.certs");
            case "course_manager": return I18N.t("nav.courses.mgr");
            case "attendance": return I18N.t("nav.attendance");
            case "gradebook": return I18N.t("nav.grades");
            case "analytics": return I18N.t("nav.analytics");
            case "interventions": return I18N.t("nav.interventions");
            case "admin": return I18N.t("nav.admin");
            case "quiz_take": return "Quiz";
            case "quiz_results": return "Quiz results";
            default: return view;
        }
    }

    private void highlight(String view) {
        for (Node n : sidebar.getChildren()) {
            if (n instanceof Button b && b.getUserData() != null) {
                String v = (String) b.getUserData();
                b.getStyleClass().remove("nav-btn-active");
                if (v.equals(view) || (view.equals("course") && v.equals("mycourses"))) {
                    b.getStyleClass().add("nav-btn-active");
                }
            }
        }
        for (Node n : tabBar.getChildren()) {
            if (n instanceof Button b && b.getUserData() != null) {
                String v = (String) b.getUserData();
                b.getStyleClass().remove("tab-active");
                if (v.equals(view)) b.getStyleClass().add("tab-active");
            }
        }
    }
}
