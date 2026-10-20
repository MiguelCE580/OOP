package edureach.ui.controllers;

import edureach.data.JsonStore;
import edureach.model.Course;
import edureach.model.PracticalScore;
import edureach.model.Quiz;
import edureach.model.Submission;
import edureach.model.User;
import edureach.service.ProgressService;
import edureach.service.QuizService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Screen 7: class gradebook with search + CSV export. */
public class GradebookController implements Screen {

    @FXML private VBox root;
    private String courseId;
    private String filter = "";

    @Override
    public void init(String param) {
        if (param != null && !param.isBlank()) courseId = param;
        build();
    }

    private void build() {
        root.getChildren().clear();
        List<Course> courses = JsonStore.get().db().courses;
        if (courseId == null) courseId = courses.get(0).getCourseId();
        Course course = ProgressService.course(courseId);

        ComboBox<String> picker = new ComboBox<>();
        for (Course c : courses) picker.getItems().add(c.getTitle());
        picker.getSelectionModel().select(course.getTitle());
        picker.setOnAction(e -> {
            courseId = courses.get(picker.getSelectionModel().getSelectedIndex()).getCourseId();
            build();
        });

        Button csv = Ui.button("↓ Export CSV", "btn-outline");
        csv.setOnAction(e -> exportCsv(course));
        Button pdf = Ui.button("↓ Export PDF", "btn-dark");
        pdf.setOnAction(e -> Ui.phase4("PDF gradebook export"));
        root.getChildren().add(Ui.hero("Gradebook", course.getTitle(), picker, csv, pdf));

        // stats
        List<User> students = enrolledStudents();
        List<Double> avgs = students.stream()
                .map(s -> ProgressService.courseAverage(s.getUserId(), courseId))
                .filter(java.util.Objects::nonNull).toList();
        double classAvg = avgs.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        long certReady = students.stream().filter(s -> ProgressService.ST_CERT_READY.equals(
                ProgressService.status(s.getUserId(), courseId))).count();
        long needsReview = students.stream().filter(s -> ProgressService.ST_NEEDS_REVIEW.equals(
                ProgressService.status(s.getUserId(), courseId))).count();
        root.getChildren().add(Ui.hbox(12,
                Ui.statCard("▦", Math.round(classAvg) + "%", "Average of learner averages"),
                Ui.statCard("★", String.valueOf(certReady), "Certificate ready"),
                Ui.statCard("!", String.valueOf(needsReview), "Needs review")));

        TextField search = new TextField();
        search.setPromptText("Search learners...");
        search.textProperty().addListener((o, a, b) -> { filter = b.toLowerCase(); build(); });
        root.getChildren().add(search);

        List<Quiz> quizzes = QuizService.quizzesOf(courseId, false);
        VBox table = Ui.card(Ui.hbox(8, Ui.label("Scores", "card-title"),
                Ui.label("showing " + filtered(students).size() + " of " + students.size(),
                        "muted", "tiny")));
        HBoxHead(table, quizzes);

        for (User s : filtered(students)) {
            var who = Ui.hbox(8, Ui.avatar(s.initials()), Ui.label(s.getFullName()));
            who.setPrefWidth(210);
            var row = Ui.hbox(10, who);
            Map<String, Submission> latest = ProgressService.latestPerQuiz(s.getUserId(), courseId);
            for (Quiz q : quizzes) {
                Submission sub = latest.get(q.getQuizId());
                row.getChildren().add(col(sub == null ? "-"
                        : sub.getCorrectCount() + " /" + sub.getTotalItems(), 70));
            }
            PracticalScore p = JsonStore.get().db().practicalScores.stream()
                    .filter(x -> x.getStudentId().equals(s.getUserId())
                            && x.getCourseId().equals(courseId)).findFirst().orElse(null);
            row.getChildren().add(col(p == null ? "-" : p.getScore() + " /10", 70));
            Double avg = ProgressService.courseAverage(s.getUserId(), courseId);
            row.getChildren().add(Ui.hbox(6,
                    col(avg == null ? "-" : Math.round(avg) + "%", 46),
                    Ui.bar(avg == null ? 0 : avg)));
            row.getChildren().add(Ui.statusChip(ProgressService.status(s.getUserId(), courseId)));
            table.getChildren().add(row);
        }
        root.getChildren().add(table);
    }

    private void HBoxHead(VBox table, List<Quiz> quizzes) {
        var head = Ui.hbox(10, col("STUDENT", 210));
        for (int i = 0; i < quizzes.size(); i++) {
            head.getChildren().add(col("QUIZ " + (i + 1), 70));
        }
        head.getChildren().addAll(col("PRACTICAL", 70), col("TOTAL", 110), col("STATUS", 150));
        head.getStyleClass().add("tbl-head");
        table.getChildren().add(head);
    }

    private List<User> enrolledStudents() {
        return JsonStore.get().db().enrollments.stream()
                .filter(e -> e.getCourseId().equals(courseId))
                .map(e -> JsonStore.get().db().users.stream()
                        .filter(u -> u.getUserId().equals(e.getStudentId())).findFirst().orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private List<User> filtered(List<User> all) {
        if (filter.isBlank()) return all;
        return all.stream().filter(u -> u.getFullName().toLowerCase().contains(filter)).toList();
    }

    private void exportCsv(Course course) {
        List<Quiz> quizzes = QuizService.quizzesOf(courseId, false);
        StringBuilder sb = new StringBuilder("Student");
        for (int i = 0; i < quizzes.size(); i++) sb.append(",Quiz").append(i + 1);
        sb.append(",Practical,Total,Status\n");
        for (User s : enrolledStudents()) {
            sb.append('"').append(s.getFullName()).append('"');
            Map<String, Submission> latest = ProgressService.latestPerQuiz(s.getUserId(), courseId);
            for (Quiz q : quizzes) {
                Submission sub = latest.get(q.getQuizId());
                sb.append(',').append(sub == null ? "" : sub.getScorePct() + "%");
            }
            PracticalScore p = JsonStore.get().db().practicalScores.stream()
                    .filter(x -> x.getStudentId().equals(s.getUserId())
                            && x.getCourseId().equals(courseId)).findFirst().orElse(null);
            Double avg = ProgressService.courseAverage(s.getUserId(), courseId);
            sb.append(',').append(p == null ? "" : p.getScore())
              .append(',').append(avg == null ? "" : avg + "%")
              .append(',').append(ProgressService.statusLabel(
                      ProgressService.status(s.getUserId(), courseId)))
              .append('\n');
        }
        try {
            Path out = Path.of("exports", "gradebook_" + courseId + ".csv");
            Files.createDirectories(out.getParent());
            Files.writeString(out, sb.toString());
            Ui.info("Export ready", "CSV written to " + out.toAbsolutePath());
        } catch (IOException e) {
            Ui.info("Export failed", e.getMessage());
        }
    }

    private Label col(String text, double width) {
        Label l = Ui.label(text);
        if (width > 0) l.setPrefWidth(width);
        l.setMinWidth(width == 0 ? USE_COMPUTED : width);
        return l;
    }

    private static final double USE_COMPUTED = -1;
}
