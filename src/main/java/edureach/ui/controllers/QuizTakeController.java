package edureach.ui.controllers;

import edureach.data.JsonStore;
import edureach.model.Question;
import edureach.model.QuestionOption;
import edureach.model.SkillDomain;
import edureach.model.Submission;
import edureach.service.AppSession;
import edureach.service.QuizService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/** Screen 5: timed quiz taking with navigator + extended-time notice. */
public class QuizTakeController implements Screen {

    @FXML private VBox root;

    private QuizService.Attempt attempt;
    private final Map<String, String> answers = new HashMap<>();
    private int current = 0;
    private long remaining;
    private Timeline timer;

    private VBox questionBox;
    private GridPane navGrid;
    private Label timerLabel;
    private HBox heroBox;

    @Override
    public void init(String param) {
        root.getChildren().clear();
        attempt = QuizService.start(AppSession.student(), QuizService.byId(param));
        remaining = attempt.limitSeconds;

        timerLabel = Ui.label("", "timer-pill");
        heroBox = Ui.hero(attempt.quiz.getTitle(), "", timerLabel);
        root.getChildren().add(heroBox);

        questionBox = Ui.vbox(12);
        questionBox.getStyleClass().add("card");

        VBox right = Ui.card(Ui.label("Questions", "card-title"));
        navGrid = new GridPane();
        navGrid.setHgap(6); navGrid.setVgap(6);
        right.getChildren().add(navGrid);
        if (attempt.extended()) {
            right.getChildren().add(Ui.label("*Extended time applies for this student", "muted", "tiny"));
        }
        Button submit = Ui.button("Submit quiz", "btn-grad");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> submit());
        right.getChildren().addAll(submit);

        HBox cols = Ui.hbox(14, questionBox, right);
        HBox.setHgrow(questionBox, javafx.scene.layout.Priority.ALWAYS);
        right.setPrefWidth(300);
        root.getChildren().add(cols);

        renderQuestion();
        startTimer();
    }

    private void startTimer() {
        if (timer != null) timer.stop();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remaining--;
            timerLabel.setText("◷ " + edureach.util.TimeUtil.mmss(Math.max(0, remaining)));
            if (remaining <= 0) {
                timer.stop();
                submit();
            }
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
        timerLabel.setText("◷ " + edureach.util.TimeUtil.mmss(remaining));
    }

    private void renderQuestion() {
        Question q = attempt.questions.get(current);
        String domain = JsonStore.get().db().domains.stream()
                .filter(d -> d.getDomainId().equals(q.getDomainId()))
                .map(SkillDomain::getName).findFirst().orElse("");
        VBox heroLeft = (VBox) heroBox.getChildren().get(0);
        ((Label) heroLeft.getChildren().get(0)).setText(attempt.quiz.getTitle());
        ((Label) heroLeft.getChildren().get(1)).setText(
                "Question " + (current + 1) + " of " + attempt.questions.size()
              + "  ·  Skill domain: " + domain);

        questionBox.getChildren().clear();
        questionBox.getChildren().add(Ui.label(q.getQuestionText(), "heading"));

        VBox opts = Ui.vbox(8);
        char letter = 'A';
        for (QuestionOption o : attempt.optionsByQuestion.get(q.getQuestionId())) {
            char l = letter++;
            Label badge = Ui.label(String.valueOf(l), "avatar");
            HBox row = Ui.hbox(10, badge, Ui.label(o.getOptionText()));
            row.getStyleClass().add("option-row");
            if (o.getOptionId().equals(answers.get(q.getQuestionId()))) {
                row.getStyleClass().add("option-sel");
            }
            row.setOnMouseClicked(e -> {
                answers.put(q.getQuestionId(), o.getOptionId());
                renderQuestion();
            });
            opts.getChildren().add(row);
        }
        questionBox.getChildren().add(opts);

        Button prev = Ui.button("← Previous", "btn-outline");
        prev.setDisable(current == 0);
        prev.setOnAction(e -> { current--; renderQuestion(); });
        Button next = Ui.button("Next →", "btn-dark");
        next.setDisable(current == attempt.questions.size() - 1);
        next.setOnAction(e -> { current++; renderQuestion(); });
        questionBox.getChildren().add(Ui.hbox(8, prev, Ui.spacer(), next));

        renderNav();
    }

    private void renderNav() {
        navGrid.getChildren().clear();
        int cols = 5;
        for (int i = 0; i < attempt.questions.size(); i++) {
            Question q = attempt.questions.get(i);
            Label cell = Ui.label(String.valueOf(i + 1), "qnav");
            if (!answers.containsKey(q.getQuestionId())) cell.getStyleClass().add("qnav-off");
            if (i == current) cell.getStyleClass().add("qnav-cur");
            int idx = i;
            cell.setOnMouseClicked(e -> { current = idx; renderQuestion(); });
            navGrid.add(cell, i % cols, i / cols);
        }
    }

    private void submit() {
        long unanswered = attempt.questions.stream()
                .map(q -> q.getQuestionId()).filter(id -> !answers.containsKey(id)).count();
        if (unanswered > 0 && remaining > 0) {
            javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION,
                    unanswered + " question(s) unanswered. Submit anyway?");
            a.setHeaderText("Submit quiz?");
            if (a.showAndWait().orElse(null) == null) return;
            if (a.getResult() != javafx.scene.control.ButtonType.OK) return;
        }
        timer.stop();
        long used = attempt.limitSeconds - Math.max(0, remaining);
        Submission sub = QuizService.submit(attempt, answers, used);
        ShellController.open("quiz_results", sub.getSubId());
    }
}
