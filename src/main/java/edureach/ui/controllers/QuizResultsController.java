package edureach.ui.controllers;

import edureach.data.JsonStore;
import edureach.model.Quiz;
import edureach.model.Submission;
import edureach.service.ProgressService;
import edureach.service.QuizService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Screen 6: instant results + per-answer feedback. */
public class QuizResultsController implements Screen {

    @FXML private VBox root;

    @Override
    public void init(String param) {
        root.getChildren().clear();
        Submission sub = JsonStore.get().db().submissions.stream()
                .filter(s -> s.getSubId().equals(param)).findFirst().orElse(null);
        if (sub == null) { root.getChildren().add(Ui.label("Submission not found.")); return; }

        Quiz quiz = QuizService.byId(sub.getQuizId());
        boolean passed = sub.getScorePct() >= quiz.getPassingPct();
        String first = JsonStore.get().db().users.stream()
                .filter(u -> u.getUserId().equals(sub.getStudentId())).findFirst()
                .map(u -> u.getFullName().split("\\s+")[0]).orElse("learner");

        Button back = Ui.button("Back to course", "btn-dark");
        back.setOnAction(e -> ShellController.open("course", quiz.getCourseId()));
        root.getChildren().add(Ui.hero(
                passed ? "Great job, " + first + "!" : "Keep going, " + first + "!",
                "Your result and feedback were saved instantly", back));

        root.getChildren().add(Ui.hbox(12,
                Ui.statCard("✓", sub.getCorrectCount() + " / " + sub.getTotalItems(),
                        "Score (" + sub.getScorePct() + "%)"),
                Ui.statCard(passed ? "★" : "!", passed ? "Passed" : "Not yet passing",
                        "Passing mark " + quiz.getPassingPct() + "%"),
                Ui.statCard("◷", sub.getTimeUsed(), "Time used")));

        if (quiz.isRemedial()) {
            root.getChildren().add(Ui.card(Ui.hbox(8,
                    Ui.chip("Remedial", "chip-warn"),
                    Ui.label(passed ? "Remedial quiz passed - your learning plan was closed."
                                    : "Below the remedial passing mark - your plan stays open.",
                            "muted", "tiny"))));
        }

        VBox fb = Ui.card(Ui.label("Answer feedback", "card-title"));
        HBox head = Ui.hbox(10,
                col("#", 30), col("Question", 420), col("Your answer", 220), col("Result", 160));
        head.getStyleClass().add("tbl-head");
        fb.getChildren().add(head);
        int i = 1;
        for (QuizService.FeedbackRow row : QuizService.feedback(sub)) {
            Label result = row.correct()
                    ? Ui.chip("Correct", "chip-ok")
                    : Ui.chip("Correct: " + row.correctAnswer(), "chip-warn");
            fb.getChildren().add(Ui.hbox(10,
                    col(String.valueOf(i++), 30),
                    col(row.question().getQuestionText(), 420),
                    col(row.yourAnswer(), 220),
                    result));
        }
        root.getChildren().add(fb);

        // where to next
        Button analytics = Ui.button("Instructor: see hard-question analytics", "btn-outline");
        analytics.setOnAction(e -> ShellController.open("analytics", quiz.getCourseId()));
        if (edureach.service.AppSession.isInstructor()) root.getChildren().add(analytics);
    }

    private Label col(String text, double width) {
        Label l = Ui.label(text);
        l.setPrefWidth(width);
        l.setWrapText(true);
        return l;
    }
}
