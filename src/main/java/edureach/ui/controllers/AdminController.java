package edureach.ui.controllers;

import edureach.data.DataAccess;
import edureach.data.JsonStore;
import edureach.model.AuditEntry;
import edureach.model.User;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/** Screen 9: administration (users, audit log, backup placeholders). */
public class AdminController implements Screen {

    @FXML private VBox root;

    @Override
    public void init(String param) { build(); }

    private void build() {
        root.getChildren().clear();
        Button restore = Ui.button("Restore", "btn-outline");
        restore.setOnAction(e -> Ui.phase4("Database restore"));
        Button backup = Ui.button("Back up database", "btn-dark");
        backup.setOnAction(e -> Ui.phase4("Database backup (mysqldump)"));
        root.getChildren().add(Ui.hero("Administration",
                "Users, audit logs and database backup", restore, backup));

        var db = JsonStore.get().db();
        long active = db.users.stream().filter(User::isActive).count();
        root.getChildren().add(Ui.hbox(12,
                Ui.statCard("☺", String.valueOf(active), "Active users"),
                Ui.statCard("▤", String.valueOf(db.courses.size()), "Courses"),
                Ui.statCard("★", String.valueOf(db.certificates.size()), "Certificates issued")));

        VBox users = Ui.card(Ui.label("User management", "card-title"));
        users.getChildren().add(Ui.hbox(10,
                head("Name", 260), head("Role", 140), head("Status", 120)));
        for (User u : db.users) {
            users.getChildren().add(Ui.hbox(10,
                    Ui.hbox(8, Ui.avatar(u.initials()), Ui.label(u.getFullName())),
                    head(u.getRole().label(), 140),
                    Ui.statusChip(u.isActive() ? "ACTIVE" : "LOCKED")));
        }
        root.getChildren().add(users);

        VBox audit = Ui.card(Ui.label("Audit log", "card-title"));
        for (AuditEntry a : DataAccess.audit().recent(12)) {
            String who = db.users.stream().filter(u -> u.getUserId().equals(a.getUserId()))
                    .map(User::getFullName).findFirst().orElse(a.getUserId());
            audit.getChildren().add(Ui.vbox(1,
                    Ui.label(a.getAction(), "tiny"),
                    Ui.label(who + "  ·  " + a.getLoggedAt(), "muted", "tiny")));
        }
        root.getChildren().add(audit);
    }

    private javafx.scene.control.Label head(String text, double w) {
        javafx.scene.control.Label l = Ui.label(text.toUpperCase(), "tbl-head");
        l.setPrefWidth(w);
        return l;
    }
}
