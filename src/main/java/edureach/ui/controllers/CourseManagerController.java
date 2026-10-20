package edureach.ui.controllers;

import edureach.data.DataAccess;
import edureach.data.JsonStore;
import edureach.model.Course;
import edureach.model.Enrollment;
import edureach.model.Lesson;
import edureach.model.StudentUser;
import edureach.model.User;
import edureach.service.AppSession;
import edureach.service.DuplicateEnrollmentException;
import edureach.service.ProgressService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

/** Screen 3: course manager (instructor). */
public class CourseManagerController implements Screen {

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
        Course course = ProgressService.course(courseId);

        ComboBox<String> picker = new ComboBox<>();
        for (Course c : courses) picker.getItems().add(c.getTitle());
        picker.getSelectionModel().select(course.getTitle());
        picker.setOnAction(e -> {
            courseId = courses.get(picker.getSelectionModel().getSelectedIndex()).getCourseId();
            build();
        });

        Button clone = Ui.button("Clone course", "btn-outline");
        clone.setOnAction(e -> cloneCourse(course));
        Button neu = Ui.button("+ New course", "btn-dark");
        neu.setOnAction(e -> newCourse());
        root.getChildren().add(Ui.hero("Course Manager",
                "Create, organize and reuse courses and lessons", picker, clone, neu));

        // lessons table
        VBox lessons = Ui.card(Ui.label("Lessons", "card-title"));
        lessons.getChildren().add(Ui.hbox(10,
                col("#", 26), col("Lesson", 340), col("Media", 90), col("Status", 110)));
        for (Lesson l : JsonStore.get().db().lessons.stream()
                .filter(x -> x.getCourseId().equals(courseId))
                .sorted(java.util.Comparator.comparingInt(Lesson::getSequenceNo)).toList()) {
            lessons.getChildren().add(Ui.hbox(10,
                    col(String.valueOf(l.getSequenceNo()), 26),
                    col(l.getTitle(), 340),
                    Ui.chip(l.getMedia(), "chip-mute"),
                    Ui.chip("Published", "chip-ok")));
        }
        root.getChildren().add(lessons);

        // details form
        TextField title = new TextField(course.getTitle());
        TextField category = new TextField(course.getCategory());
        long enrolled = JsonStore.get().db().enrollments.stream()
                .filter(e2 -> e2.getCourseId().equals(courseId)).count();
        Button save = Ui.button("Save changes", "btn-outline");
        save.setOnAction(e -> {
            course.setTitle(title.getText());
            course.setCategory(category.getText());
            DataAccess.courses().save(course);
            DataAccess.audit().log(AppSession.current().getUserId(),
                    "COURSE_UPDATED course=" + courseId);
            build();
        });
        Button enroll = Ui.button("Enroll learners", "btn-grad");
        enroll.setOnAction(e -> enrollLearner(course));
        root.getChildren().add(Ui.card(
                Ui.label("Course details", "card-title"),
                Ui.label("Title", "tiny", "muted"), title,
                Ui.label("Category", "tiny", "muted"), category,
                Ui.label("Instructor", "tiny", "muted"),
                Ui.label(JsonStore.get().db().users.stream()
                        .filter(u -> u.getUserId().equals(course.getInstructorId()))
                        .map(User::getFullName).findFirst().orElse("-")),
                Ui.label("Enrolled learners", "tiny", "muted"),
                Ui.label(enrolled + " students"),
                Ui.hbox(8, save, enroll)));
    }

    private void enrollLearner(Course course) {
        List<StudentUser> candidates = JsonStore.get().db().users.stream()
                .filter(u -> u instanceof StudentUser)
                .map(u -> (StudentUser) u)
                .filter(s -> ProgressService.enrollment(s.getUserId(), course.getCourseId()).isEmpty())
                .toList();
        if (candidates.isEmpty()) { Ui.info("Enroll learners", "All students are already enrolled."); return; }
        ChoiceDialog<StudentUser> dlg = new ChoiceDialog<>(candidates.get(0), candidates);
        dlg.setTitle("Enroll learner");
        dlg.setHeaderText("Choose a learner to enroll in " + course.getTitle());
        Optional<StudentUser> pick = dlg.showAndWait();
        if (pick.isEmpty()) return;
        boolean dup = ProgressService.enrollment(pick.get().getUserId(), course.getCourseId()).isPresent();
        if (dup) throw new DuplicateEnrollmentException(pick.get().getFullName());
        JsonStore.get().db().enrollments.add(new Enrollment(
                edureach.util.IdGen.next("e"), pick.get().getUserId(),
                course.getCourseId(), edureach.util.TimeUtil.today()));
        DataAccess.audit().log(AppSession.current().getUserId(),
                "ENROLLMENT_ADDED student=" + pick.get().getUsername());
        JsonStore.get().save();
        build();
    }

    private void cloneCourse(Course course) {
        var db = JsonStore.get().db();
        String newId = edureach.util.IdGen.next("c");
        Course copy = new Course(newId, course.getTitle() + " (copy)",
                course.getCategory(), course.getInstructorId(), course.getDescription());
        db.courses.add(copy);
        int seq = 1;
        for (Lesson l : db.lessons.stream().filter(x -> x.getCourseId().equals(course.getCourseId()))
                .sorted(java.util.Comparator.comparingInt(Lesson::getSequenceNo)).toList()) {
            db.lessons.add(new Lesson(edureach.util.IdGen.next("l"), newId, l.getTitle(),
                    l.getMedia(), seq++, l.getContent(), l.getDomainId()));
        }
        DataAccess.audit().log(AppSession.current().getUserId(), "COURSE_CLONED from=" + course.getCourseId());
        JsonStore.get().save();
        courseId = newId;
        build();
    }

    private void newCourse() {
        javafx.scene.control.TextInputDialog dlg = new javafx.scene.control.TextInputDialog();
        dlg.setTitle("New course");
        dlg.setHeaderText("Title of the new course");
        dlg.setContentText("Title:");
        dlg.showAndWait().ifPresent(title -> {
            var db = JsonStore.get().db();
            String id = edureach.util.IdGen.next("c");
            db.courses.add(new Course(id, title, "IT",
                    AppSession.current().getUserId(), ""));
            DataAccess.audit().log(AppSession.current().getUserId(), "COURSE_CREATED course=" + id);
            JsonStore.get().save();
            courseId = id;
            build();
        });
    }

    private javafx.scene.control.Label col(String text, double width) {
        javafx.scene.control.Label l = Ui.label(text);
        l.setPrefWidth(width);
        return l;
    }
}
