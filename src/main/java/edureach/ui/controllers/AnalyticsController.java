package edureach.ui.controllers;

import edureach.data.JsonStore;
import edureach.model.Course;
import edureach.model.Quiz;
import edureach.model.SkillDomain;
import edureach.service.AnalyticsService;
import edureach.service.ProgressService;
import edureach.service.QuizService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * New module: detailed quiz checking. Per-item correctness across all
 * submissions, hard-item flagging and auto improvement suggestions.
 */
public class AnalyticsController implements Screen {

    @FXML private VBox root;
    private String courseId;
    private String quizId;

    @Override
    public void init(String param) {
        if (param != null && !param.isBlank()) courseId = param;
        build();
    }

    private void build() {
        root.getChildren().clear();
        List<Course> courses = JsonStore.get().db().courses;
        if (courseId == null) courseId = courses.get(0).getCourseId();
        List<Quiz> quizzes = QuizService.quizzesOf(courseId, true);
        if (quizId == null || quizzes.stream().noneMatch(q -> q.getQuizId().equals(quizId))) {
            quizId = quizzes.isEmpty() ? null : quizzes.get(0).getQuizId();
        }

        ComboBox<String> coursePick = new ComboBox<>();
        for (Course c : courses) coursePick.getItems().add(c.getTitle());
        coursePick.getSelectionModel().select(ProgressService.course(courseId).getTitle());
        coursePick.setOnAction(e -> {
            courseId = courses.get(coursePick.getSelectionModel().getSelectedIndex()).getCourseId();
            quizId = null;
            build();
        });
        ComboBox<String> quizPick = new ComboBox<>();
        for (Quiz q : quizzes) quizPick.getItems().add(q.getTitle());
        if (quizId != null) {
            quizPick.getSelectionModel().select(
                    quizzes.stream().filter(q -> q.getQuizId().equals(quizId)).findFirst()
                            .map(Quiz::getTitle).orElse(null));
        }
        quizPick.setOnAction(e -> {
            quizId = quizzes.get(quizPick.getSelectionModel().getSelectedIndex()).getQuizId();
            build();
        });

        root.getChildren().add(Ui.hero("Quiz Analytics",
                "Detailed quiz checking - find the questions learners struggle with",
                coursePick, quizPick));
        if (quizId == null) {
            root.getChildren().add(Ui.label("No quizzes in this course yet."));
            return;
        }

        List<AnalyticsService.QuestionStat> stats = AnalyticsService.statsForQuiz(quizId);
        List<AnalyticsService.QuestionStat> hard = AnalyticsService.hardQuestions(quizId);
        double classAvg = stats.stream().mapToDouble(s -> s.pct).average().orElse(0);

        root.getChildren().add(Ui.hbox(12,
                Ui.statCard("▦", Math.round(classAvg) + "%", "Class average on this quiz"),
                Ui.statCard("!", String.valueOf(hard.size()), "Flagged hard items (<50%)"),
                Ui.statCard("◔", String.valueOf(stats.size()), "Items analyzed")));

        VBox table = Ui.card(Ui.label("Per-question breakdown", "card-title"));
        table.getChildren().add(Ui.hbox(10,
                head("#", 26), head("Question", 380), head("Domain", 130),
                head("Attempts", 70), head("Correct", 60), head("Rate", 130), head("Flag", 90)));

        int i = 1;
        for (AnalyticsService.QuestionStat s : stats) {
            String domain = JsonStore.get().db().domains.stream()
                    .filter(d -> d.getDomainId().equals(s.question.getDomainId()))
                    .map(SkillDomain::getName).findFirst().orElse("");
            table.getChildren().add(Ui.hbox(10,
                    head(String.valueOf(i++), 26),
                    wrap(s.question.getQuestionText(), 380),
                    Ui.chip(domain, "chip-mute"),
                    head(String.valueOf(s.attempts), 70),
                    head(String.valueOf(s.correct), 60),
                    Ui.hbox(6, Ui.bar(s.pct), head(Math.round(s.pct) + "%", 40)),
                    s.isHard() ? Ui.chip("Hard item", "chip-warn") : Ui.chip("OK", "chip-ok")));
            if (s.isHard()) {
                table.getChildren().add(Ui.label("   ↳ " + AnalyticsService.suggestion(s),
                        "muted", "tiny"));
            }
        }
        root.getChildren().add(table);

        if (!hard.isEmpty()) {
            var longText = Ui.label("Hardest item: \"" + hard.get(0).question.getQuestionText()
                    + "\" (" + Math.round(hard.get(0).pct) + "% correct). "
                    + AnalyticsService.suggestion(hard.get(0)), "tiny");
            longText.setWrapText(true);
            var planRow = Ui.hbox(8, Ui.chip("Improvement plan", "chip-dark"), longText);
            javafx.scene.layout.HBox.setHgrow(longText, javafx.scene.layout.Priority.ALWAYS);
            root.getChildren().add(Ui.card(planRow));
        }
    }

    private javafx.scene.control.Label head(String text, double w) {
        javafx.scene.control.Label l = Ui.label(text);
        l.setPrefWidth(w);
        return l;
    }

    private javafx.scene.control.Label wrap(String text, double w) {
        javafx.scene.control.Label l = Ui.label(text);
        l.setPrefWidth(w);
        l.setWrapText(true);
        return l;
    }
}
