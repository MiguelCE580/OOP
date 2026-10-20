package edureach.ui.controllers;

import edureach.data.JsonStore;
import edureach.model.*;
import edureach.service.*;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

/** Learner view of one course: lessons, quizzes and remedial path. */
public class StudentCourseController implements Screen {

    @FXML private VBox root;
    private String courseId;

    @Override
    public void init(String param) {
        courseId = (param == null || param.isBlank()) ? null : param;
        build();
    }

    private void build() {
        root.getChildren().clear();
        StudentUser me = AppSession.student();
        if (courseId == null) { buildPicker(me); return; }

        Course course = ProgressService.course(courseId);
        if (course == null) { buildPicker(me); return; }

        String status = ProgressService.status(me.getUserId(), courseId);
        int pct = ProgressService.completionPct(me.getUserId(), courseId);
        root.getChildren().add(Ui.hero(course.getTitle(),
                course.getCategory() + "  ·  " + pct + "% complete",
                Ui.statusChip(status)));

        // ---- remedial plan panel (new module) ----
        RemedialService.plansOf(me.getUserId()).stream()
                .filter(p -> p.getCourseId().equals(courseId))
                .filter(p -> !RemedialPlan.COMPLETED.equals(p.getStatus()))
                .findFirst()
                .ifPresent(plan -> root.getChildren().add(planCard(plan)));

        // ---- lessons ----
        VBox lessons = Ui.card(Ui.label("Lessons", "card-title"));
        for (Lesson l : JsonStore.get().db().lessons.stream()
                .filter(x -> x.getCourseId().equals(courseId))
                .sorted(java.util.Comparator.comparingInt(Lesson::getSequenceNo)).toList()) {
            boolean done = ProgressService.enrollment(me.getUserId(), courseId)
                    .map(e -> e.getCompletedLessons().contains(l.getLessonId())).orElse(false);
            Button toggle = Ui.button(done ? "✓ Completed" : "Mark complete",
                    done ? "btn-chip btn-chip-on" : "btn-chip");
            toggle.setOnAction(e -> {
                ProgressService.markLessonComplete(me.getUserId(), l.getLessonId());
                CertificateService.issueIfReady(me);
                build();
            });
            lessons.getChildren().add(Ui.vbox(4,
                    Ui.hbox(10, Ui.label(l.getSequenceNo() + ".", "muted"),
                            Ui.label(l.getTitle()), Ui.chip(l.getMedia(), "chip-mute"),
                            Ui.spacer(), toggle),
                    Ui.label(l.getContent(), "muted", "tiny")));
        }
        root.getChildren().add(lessons);

        // ---- quizzes ----
        VBox quizzes = Ui.card(Ui.label("Quizzes", "card-title"));
        for (Quiz q : QuizService.quizzesOf(courseId, false)) {
            Submission latest = ProgressService.latestPerQuiz(me.getUserId(), courseId)
                    .get(q.getQuizId());
            Button take = Ui.button(latest == null ? "Take quiz" : "Retake quiz", "btn-grad");
            take.setOnAction(e -> ShellController.open("quiz_take", q.getQuizId()));
            quizzes.getChildren().add(Ui.hbox(10,
                    Ui.label(q.getTitle()),
                    Ui.chip(q.getTimeLimitMin() + " min", "chip-mute"),
                    Ui.chip("Passing " + q.getPassingPct() + "%", "chip-mute"),
                    Ui.spacer(),
                    latest == null ? Ui.label("No attempt yet", "muted", "tiny")
                            : Ui.label("Latest: " + latest.getScorePct() + "%", "tiny", "muted"),
                    take));
        }
        root.getChildren().add(quizzes);
    }

    private VBox planCard(RemedialPlan plan) {
        VBox card = Ui.card(
                Ui.hbox(8, Ui.chip("My learning plan", "chip-warn"),
                        Ui.label("Status: " + plan.getStatus(), "muted", "tiny")));
        card.setStyle("-fx-border-color: -er-warn-fg;");

        List<String> weakNames = JsonStore.get().db().domains.stream()
                .filter(d -> plan.getWeakDomainIds().contains(d.getDomainId()))
                .map(SkillDomain::getName).toList();
        card.getChildren().add(Ui.label("Skills to recover: " + String.join(", ", weakNames), "tiny"));

        for (String lessonId : plan.getReviewLessonIds()) {
            Lesson l = JsonStore.get().db().lessons.stream()
                    .filter(x -> x.getLessonId().equals(lessonId)).findFirst().orElse(null);
            if (l == null) continue;
            Button reviewed = Ui.button("Mark reviewed", "btn-chip");
            reviewed.setOnAction(e -> { RemedialService.markReviewDone(plan.getPlanId(), lessonId); build(); });
            card.getChildren().add(Ui.hbox(8, Ui.label("↻", "nav-glyph"),
                    Ui.label("Re-study: " + l.getTitle()), Ui.spacer(), reviewed));
        }
        if (!plan.getRemedialQuizId().isBlank()) {
            Quiz rq = QuizService.byId(plan.getRemedialQuizId());
            if (rq != null) {
                Button take = Ui.button("Take " + rq.getTitle(), "btn-grad");
                take.setOnAction(e -> ShellController.open("quiz_take", rq.getQuizId()));
                card.getChildren().add(Ui.hbox(8,
                        Ui.label("Pass with " + rq.getPassingPct() + "% to close this plan.", "muted", "tiny"),
                        Ui.spacer(), take));
            }
        }
        return card;
    }

    private void buildPicker(StudentUser me) {
        root.getChildren().add(Ui.hero("My courses", "Pick a course to continue learning"));
        for (Enrollment e : ProgressService.enrollmentsOf(me.getUserId())) {
            Course c = ProgressService.course(e.getCourseId());
            Button open = Ui.button("Open", "btn-outline");
            open.setOnAction(ev -> ShellController.open("course", c.getCourseId()));
            root.getChildren().add(Ui.card(Ui.hbox(10,
                    Ui.label(c.getTitle(), "heading"),
                    Ui.chip(c.getCategory(), "chip-mute"),
                    Ui.spacer(),
                    Ui.statusChip(ProgressService.status(me.getUserId(), c.getCourseId())),
                    open)));
        }
    }
}
