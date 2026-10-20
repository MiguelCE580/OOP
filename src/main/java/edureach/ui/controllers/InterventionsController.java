package edureach.ui.controllers;

import edureach.data.JsonStore;
import edureach.model.*;
import edureach.service.AnalyticsService;
import edureach.service.ProgressService;
import edureach.service.QuizService;
import edureach.service.RemedialService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

/**
 * New module: interventions for failing students. The instructor scans a
 * course, the system auto-builds remedial plans (weak domains + review
 * lessons + remedial quiz) and tracks them to completion.
 */
public class InterventionsController implements Screen {

    @FXML private VBox root;
    private String courseId;

    @Override
    public void init(String param) {
        if (param != null && !param.isBlank()) courseId = param;
        build();
    }

    private void build() {
        root.getChildren().clear();
        List<Course> courses = JsonStore.get().db().courses;
        if (courseId == null) courseId = courses.get(0).getCourseId();

        ComboBox<String> picker = new ComboBox<>();
        for (Course c : courses) picker.getItems().add(c.getTitle());
        picker.getSelectionModel().select(ProgressService.course(courseId).getTitle());
        picker.setOnAction(e -> {
            courseId = courses.get(picker.getSelectionModel().getSelectedIndex()).getCourseId();
            build();
        });
        Button scan = Ui.button("Scan for failing students", "btn-dark");
        scan.setOnAction(e -> {
            int created = RemedialService.scanCourse(courseId);
            Ui.info("Scan complete", created == 0
                    ? "No new remedial plans needed."
                    : created + " remedial plan(s) created.");
            build();
        });
        root.getChildren().add(Ui.hero("Interventions",
                "Support learners who fall below the passing mark", picker, scan));

        List<RemedialPlan> plans = RemedialService.allPlans().stream()
                .filter(p -> p.getCourseId().equals(courseId)).toList();
        long open = plans.stream().filter(p -> !RemedialPlan.COMPLETED.equals(p.getStatus())).count();
        long done = plans.size() - open;
        int struggling = AnalyticsService.strugglingStudents(courseId).size();
        root.getChildren().add(Ui.hbox(12,
                Ui.statCard("!", String.valueOf(open), "Open plans"),
                Ui.statCard("✓", String.valueOf(done), "Completed plans"),
                Ui.statCard("▦", String.valueOf(struggling), "Learners below passing")));

        VBox list = Ui.card(Ui.label("Remedial plans", "card-title"));
        if (plans.isEmpty()) {
            list.getChildren().add(Ui.label(
                    "No plans yet. Press \"Scan for failing students\" after a quiz.", "muted"));
        }
        for (RemedialPlan p : plans) {
            User student = JsonStore.get().db().users.stream()
                    .filter(u -> u.getUserId().equals(p.getStudentId())).findFirst().orElse(null);
            if (student == null) continue;
            String weak = JsonStore.get().db().domains.stream()
                    .filter(d -> p.getWeakDomainIds().contains(d.getDomainId()))
                    .map(SkillDomain::getName).collect(Collectors.joining(", "));
            String lessons = p.getReviewLessonIds().stream()
                    .map(id -> JsonStore.get().db().lessons.stream()
                            .filter(l -> l.getLessonId().equals(id))
                            .map(Lesson::getTitle).findFirst().orElse(""))
                    .collect(Collectors.joining(", "));
            Quiz rq = p.getRemedialQuizId().isBlank() ? null : QuizService.byId(p.getRemedialQuizId());

            list.getChildren().add(Ui.vbox(6,
                    Ui.hbox(10, Ui.avatar(student.initials()),
                            Ui.label(student.getFullName(), "card-title"),
                            Ui.statusChip(p.getStatus()),
                            Ui.spacer(),
                            Ui.label("created " + p.getCreatedOn(), "muted", "tiny")),
                    Ui.label("Weak skills: " + (weak.isBlank() ? "-" : weak), "tiny"),
                    Ui.label("Review lessons: " + (lessons.isBlank() ? "-" : lessons), "tiny"),
                    rq == null ? Ui.label("No remedial quiz attached", "muted", "tiny")
                            : Ui.label("Remedial quiz: " + rq.getTitle()
                                    + " (pass " + rq.getPassingPct() + "%)", "tiny"),
                    Ui.label(p.getNote(), "muted", "tiny")));
        }
        root.getChildren().add(list);
    }
}
