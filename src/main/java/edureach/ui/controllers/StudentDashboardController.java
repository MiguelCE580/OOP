package edureach.ui.controllers;

import edureach.data.JsonStore;
import edureach.model.*;
import edureach.service.*;
import edureach.ui.I18N;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

/** Screen 2: student dashboard. */
public class StudentDashboardController implements Screen {

    @FXML private VBox root;

    @FXML
    private void initialize() { build(); }

    @Override
    public void init(String param) { build(); }

    private void build() {
        root.getChildren().clear();
        StudentUser me = AppSession.student();
        String first = me.getFullName().split("\\s+")[0];

        // hero + continue lesson
        List<Enrollment> enrollments = ProgressService.enrollmentsOf(me.getUserId());
        String topCourse = enrollments.isEmpty() ? null : enrollments.get(0).getCourseId();
        String sub = "";
        if (topCourse != null) {
            Course c = ProgressService.course(topCourse);
            sub = c.getTitle() + " is " + ProgressService.completionPct(me.getUserId(), topCourse)
                    + "% complete - keep going!";
        }
        Button continueBtn = Ui.button(I18N.t("continue"), "btn-dark");
        continueBtn.setOnAction(e -> ShellController.open("course", topCourse));
        root.getChildren().add(Ui.hero(I18N.t("greet") + ", " + first + "!", sub, continueBtn));

        // remedial banner (new module)
        List<RemedialPlan> openPlans = RemedialService.plansOf(me.getUserId()).stream()
                .filter(p -> !RemedialPlan.COMPLETED.equals(p.getStatus())).toList();
        if (!openPlans.isEmpty()) {
            RemedialPlan p = openPlans.get(0);
            Course c = ProgressService.course(p.getCourseId());
            Button go = Ui.button("Open my learning plan", "btn-dark");
            go.setOnAction(e -> ShellController.open("course", p.getCourseId()));
            var bannerText = Ui.label("Your score in " + c.getTitle()
                    + " is below the passing mark. A short remedial path was prepared for you.", "muted");
            bannerText.setWrapText(true);
            var bannerRow = Ui.hbox(8, Ui.chip("! Needs review", "chip-warn"), bannerText);
            javafx.scene.layout.HBox.setHgrow(bannerText, javafx.scene.layout.Priority.ALWAYS);
            VBox banner = Ui.card(bannerRow, go);
            banner.setStyle("-fx-border-color: -er-warn-fg;");
            root.getChildren().add(banner);
        }

        // stat cards
        Double avg = ProgressService.avgQuizScore(me.getUserId());
        root.getChildren().add(Ui.hbox(12,
                Ui.statCard("★", String.valueOf(ProgressService.earnedBadges(me.getUserId()).size()),
                        "Skill badges earned"),
                Ui.statCard("▦", avg == null ? "-" : Math.round(avg) + "%", "Average quiz score"),
                Ui.statCard("▤", String.valueOf(enrollments.size()), "Courses enrolled")));

        // courses + right column
        VBox courses = Ui.card(Ui.label(I18N.t("mycourses"), "card-title"));
        for (Enrollment e : enrollments) {
            Course c = ProgressService.course(e.getCourseId());
            int pct = ProgressService.completionPct(me.getUserId(), c.getCourseId());
            Label tile = Ui.label(c.getTitle(), "course-tile");
            tile.setMaxWidth(Double.MAX_VALUE);
            Button open = Ui.button("Open", "btn-outline");
            open.setOnAction(ev -> ShellController.open("course", c.getCourseId()));
            tile.setOnMouseClicked(ev -> ShellController.open("course", c.getCourseId()));
            VBox tileBox = new VBox(0, tile,
                    Ui.vbox(4, Ui.hbox(8, Ui.label(pct + "% complete", "tiny", "muted"),
                            Ui.spacer(), open), Ui.bar(pct)));
            tileBox.getStyleClass().add("card");
            tileBox.setPadding(javafx.geometry.Insets.EMPTY);
            VBox.setMargin(tileBox, javafx.geometry.Insets.EMPTY);
            courses.getChildren().add(tileBox);
        }

        VBox right = Ui.vbox(14);
        VBox mastery = Ui.card(Ui.label(I18N.t("mastery"), "card-title"));
        for (SkillDomain d : JsonStore.get().db().domains) {
            Double m = ProgressService.domainMastery(me.getUserId(), d.getDomainId());
            mastery.getChildren().add(Ui.vbox(3,
                    Ui.hbox(8, Ui.label(d.getName(), "tiny"), Ui.spacer(),
                            Ui.label(m == null ? "-" : Math.round(m) + "%", "tiny", "muted")),
                    Ui.bar(m == null ? 0 : m)));
        }
        VBox badges = Ui.card(Ui.label(I18N.t("badges"), "card-title"));
        var earned = ProgressService.earnedBadges(me.getUserId());
        var badgeRow = Ui.hbox(14);
        badgeRow.setAlignment(Pos.CENTER);
        for (SkillDomain d : JsonStore.get().db().domains) {
            boolean got = earned.stream().anyMatch(x -> x.getDomainId().equals(d.getDomainId()));
            Label dot = Ui.label(got ? "★" : "☆", "badge-dot");
            if (!got) dot.getStyleClass().add("badge-off");
            VBox one = Ui.vbox(4, dot, Ui.label(d.getBadgeName(), "tiny", "muted"));
            one.setAlignment(Pos.CENTER);
            badgeRow.getChildren().add(one);
        }
        badges.getChildren().add(badgeRow);
        right.getChildren().addAll(mastery, badges);

        var cols = Ui.hbox(14, courses, right);
        javafx.scene.layout.HBox.setHgrow(courses, javafx.scene.layout.Priority.ALWAYS);
        right.setPrefWidth(360);
        root.getChildren().add(cols);

        // optional curriculum shortcut
        var follow = CurriculumService.follow(me.getUserId());
        String curInfo = follow.map(f -> f.getTrack().label() + " - "
                        + CurriculumService.progressPct(me.getUserId(), f.getTrack()) + "% studied")
                .orElse("Not following yet - completely optional");
        Button browse = Ui.button("Browse curriculum", "btn-outline");
        browse.setOnAction(e -> ShellController.open("curriculum", null));
        root.getChildren().add(Ui.card(
                Ui.hbox(8, Ui.label("◆", "nav-glyph"),
                        Ui.label("Optional K-12 curriculum (Math, Elem-HS)", "card-title"),
                        Ui.spacer(), Ui.label(curInfo, "muted", "tiny"), browse)));
    }
}
