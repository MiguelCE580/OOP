package edureach.ui.controllers;

import edureach.data.AttendanceDAO;
import edureach.data.DataAccess;
import edureach.data.JsonStore;
import edureach.model.AttendanceRecord;
import edureach.model.Course;
import edureach.model.Enrollment;
import edureach.model.User;
import edureach.service.AppSession;
import edureach.service.ProgressService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Screen 4: attendance recording (instructor). */
public class AttendanceController implements Screen {

    @FXML private VBox root;
    private String courseId;
    private final Map<String, String> marks = new HashMap<>();   // enrollmentId -> P/A/L
    private TextField dateField;

    @Override
    public void init(String param) {
        if (param != null && !param.isBlank()) courseId = param;
        build();
    }

    private void build() {
        root.getChildren().clear();
        marks.clear();
        List<Course> courses = JsonStore.get().db().courses;
        if (courseId == null) courseId = courses.get(0).getCourseId();

        ComboBox<String> picker = new ComboBox<>();
        for (Course c : courses) picker.getItems().add(c.getTitle());
        picker.getSelectionModel().select(ProgressService.course(courseId).getTitle());
        picker.setOnAction(e -> {
            courseId = courses.get(picker.getSelectionModel().getSelectedIndex()).getCourseId();
            build();
        });
        dateField = new TextField(edureach.util.TimeUtil.today());
        dateField.setPrefWidth(130);
        dateField.textProperty().addListener((o, a, b) -> build());

        Button save = Ui.button("Save attendance", "btn-dark");
        save.setOnAction(e -> saveAttendance());
        root.getChildren().add(Ui.hero("Attendance",
                "Session: " + dateField.getText(), picker, dateField, save));

        VBox table = Ui.card(Ui.label("Mark attendance", "card-title"));
        table.getChildren().add(Ui.hbox(10,
                colw("Student", 260), colw("Status", 160), colw("Sessions attended", 160)));

        AttendanceDAO dao = DataAccess.attendance();
        for (Enrollment e : JsonStore.get().db().enrollments.stream()
                .filter(x -> x.getCourseId().equals(courseId)).toList()) {
            User student = JsonStore.get().db().users.stream()
                    .filter(u -> u.getUserId().equals(e.getStudentId())).findFirst().orElse(null);
            if (student == null) continue;

            String existing = dao.forSession(courseId, dateField.getText()).stream()
                    .filter(a -> a.getEnrollmentId().equals(e.getEnrollmentId()))
                    .map(AttendanceRecord::getStatus).findFirst()
                    .orElse(AttendanceRecord.PRESENT);
            marks.put(e.getEnrollmentId(), existing);

            var statusBox = Ui.hbox(6);
            for (String code : new String[]{AttendanceRecord.PRESENT,
                    AttendanceRecord.ABSENT, AttendanceRecord.LATE}) {
                Button b = Ui.button(code, "btn-chip");
                if (marks.get(e.getEnrollmentId()).equals(code)) b.getStyleClass().add("btn-chip-on");
                b.setOnAction(ev -> {
                    marks.put(e.getEnrollmentId(), code);
                    build();
                });
                statusBox.getChildren().add(b);
            }
            table.getChildren().add(Ui.hbox(10,
                    Ui.hbox(8, Ui.avatar(student.initials()), Ui.label(student.getFullName())),
                    Ui.hbox(0, statusBox),
                    Ui.label(dao.sessionsAttended(e.getEnrollmentId()) + " / "
                            + sessionsHeld() , "muted")));
        }
        table.getChildren().add(Ui.label(
                "P = Present   A = Absent   L = Late   ·   Status is shown with letters as well as color",
                "muted", "tiny"));
        root.getChildren().add(table);
    }

    private long sessionsHeld() {
        return JsonStore.get().db().attendance.stream()
                .map(AttendanceRecord::getSessionDate).distinct().count();
    }

    private void saveAttendance() {
        List<AttendanceRecord> records = marks.entrySet().stream()
                .map(en -> new AttendanceRecord(edureach.util.IdGen.next("att"),
                        en.getKey(), dateField.getText(), en.getValue()))
                .toList();
        DataAccess.attendance().saveSession(records);
        DataAccess.audit().log(AppSession.current().getUserId(),
                "ATTENDANCE_SAVED course=" + courseId + " date=" + dateField.getText());
        Ui.info("Attendance saved", records.size() + " record(s) stored for " + dateField.getText() + ".");
        build();
    }

    private javafx.scene.control.Label colw(String text, double width) {
        javafx.scene.control.Label l = Ui.label(text.toUpperCase(), "tbl-head");
        l.setPrefWidth(width);
        return l;
    }
}
