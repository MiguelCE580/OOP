package edureach.ui.controllers;

import edureach.model.CurriculumTopic;
import edureach.model.CurriculumTrack;
import edureach.service.AppSession;
import edureach.service.CurriculumService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * New module: optional K-12 curriculum browser (Mathematics flagship,
 * Elementary -> Senior High). Students may follow a track and tick topics
 * they already studied; instructors get a read-only view.
 */
public class CurriculumController implements Screen {

    @FXML private VBox root;
    private CurriculumTrack track = CurriculumTrack.ELEMENTARY;
    private String subject = "Mathematics";

    @Override
    public void init(String param) { build(); }

    private void build() {
        root.getChildren().clear();
        boolean student = AppSession.isStudent();
        String me = AppSession.current().getUserId();

        root.getChildren().add(Ui.hero("K-12 Curriculum",
                "Optional supplemental learning path - Mathematics first, Elem to HS"));

        // track selector
        var tracks = Ui.hbox(8);
        for (CurriculumTrack t : CurriculumTrack.values()) {
            Button b = Ui.button(t.label() + "  (" + t.grades() + ")", "btn-chip");
            if (t == track) b.getStyleClass().add("btn-chip-on");
            b.setOnAction(e -> { track = t; subject = "Mathematics"; build(); });
            tracks.getChildren().add(b);
        }
        root.getChildren().add(tracks);

        // follow toggle + progress (students only)
        if (student) {
            boolean following = CurriculumService.follow(me)
                    .map(f -> f.getTrack() == track).orElse(false);
            Button follow = Ui.button(following ? "✓ Following this track" : "Follow this track (optional)",
                    following ? "btn-chip btn-chip-on" : "btn-chip");
            follow.setOnAction(e -> { CurriculumService.toggleFollow(me, track); build(); });
            int pct = CurriculumService.progressPct(me, track);
            root.getChildren().add(Ui.hbox(10, follow,
                    Ui.label(pct + "% studied", "muted", "tiny"), Ui.bar(pct)));
        } else {
            root.getChildren().add(Ui.label(
                    "Instructor view - learners follow tracks optionally from their dashboard.",
                    "muted", "tiny"));
        }

        // subject chips
        var subs = Ui.hbox(8);
        for (String s : CurriculumService.subjectsOf(track)) {
            Button b = Ui.button(s, "btn-chip");
            if (s.equals(subject)) b.getStyleClass().add("btn-chip-on");
            b.setOnAction(e -> { subject = s; build(); });
            subs.getChildren().add(b);
        }
        root.getChildren().add(subs);

        // topics
        List<CurriculumTopic> topics = CurriculumService.topics(track, subject);
        var grid = Ui.vbox(10);
        for (CurriculumTopic t : topics) {
            boolean studied = student && CurriculumService.follow(me)
                    .map(f -> f.getStudiedTopics().contains(t.getTopicId())).orElse(false);
            Button tick = null;
            if (student) {
                tick = Ui.button(studied ? "✓ Studied" : "Mark studied",
                        studied ? "btn-chip btn-chip-on" : "btn-chip");
                tick.setOnAction(e -> { CurriculumService.toggleStudied(me, track, t.getTopicId()); build(); });
            }
            grid.getChildren().add(Ui.card(
                    Ui.hbox(10, Ui.label(t.getTitle(), "card-title"),
                            Ui.chip(t.getSubject(), "chip-mute"),
                            Ui.chip("~" + t.getEstimatedMinutes() + " min", "chip-mute"),
                            Ui.spacer(), tick == null ? Ui.label("") : tick),
                    Ui.label(t.getDescription(), "muted", "tiny")));
        }
        root.getChildren().add(grid);
    }
}
