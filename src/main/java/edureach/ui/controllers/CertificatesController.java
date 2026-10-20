package edureach.ui.controllers;

import edureach.data.JsonStore;
import edureach.model.Certificate;
import edureach.model.Course;
import edureach.model.User;
import edureach.service.AppSession;
import edureach.service.CertificateService;
import edureach.service.ProgressService;
import edureach.ui.Screen;
import edureach.ui.Ui;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

/** Screen 8: certificate preview + verification code checker. */
public class CertificatesController implements Screen {

    @FXML private VBox root;

    @Override
    public void init(String param) { build(); }

    private void build() {
        root.getChildren().clear();
        Button download = Ui.button("Download PDF", "btn-dark");
        download.setOnAction(e -> Ui.phase4("PDF certificate rendering (OpenPDF)"));
        root.getChildren().add(Ui.hero("Certificates",
                "Verifiable proof of your skills", download));

        // whose certificate to preview
        String studentId = AppSession.current().getUserId();
        if (AppSession.isInstructor() || AppSession.isAdmin()) {
            ComboBox<String> picker = new ComboBox<>();
            List<User> students = JsonStore.get().db().users.stream()
                    .filter(u -> u.getRole() == edureach.model.Role.STUDENT).toList();
            for (User s : students) picker.getItems().add(s.getFullName());
            picker.getSelectionModel().selectFirst();
            picker.setOnAction(e -> studentPreview(picker.getValue()));
            root.getChildren().add(picker);
            if (!students.isEmpty()) studentId = students.get(0).getUserId();
        }

        VBox previewCol = Ui.vbox(14);
        previewCol.getStyleClass().add("preview-col");
        renderPreview(previewCol, studentId);

        // verify panel
        TextField code = new TextField();
        code.setPromptText("ER-2026-XXXX-0000");
        VBox result = Ui.vbox(6);
        Button verify = Ui.button("Verify", "btn-grad");
        verify.setMaxWidth(Double.MAX_VALUE);
        verify.setOnAction(e -> {
            result.getChildren().clear();
            CertificateService.verify(code.getText()).ifPresentOrElse(c -> {
                User owner = JsonStore.get().db().users.stream()
                        .filter(u -> u.getUserId().equals(c.getStudentId())).findFirst().orElse(null);
                result.getChildren().add(Ui.vbox(3,
                        Ui.chip("Valid certificate", "chip-ok"),
                        Ui.label((owner == null ? "?" : owner.getFullName())
                                + "  ·  issued " + c.getIssuedOn(), "tiny")));
            }, () -> result.getChildren().add(Ui.vbox(3,
                    Ui.chip("Not found", "chip-warn"),
                    Ui.label("No certificate matches that code.", "tiny", "muted"))));
        });
        VBox verifyCard = Ui.card(
                Ui.label("Verify a certificate", "card-title"),
                Ui.label("Verification code", "tiny", "muted"),
                code, verify, result);
        verifyCard.setPrefWidth(320);

        var cols = Ui.hbox(14, previewCol, verifyCard);
        javafx.scene.layout.HBox.setHgrow(previewCol, javafx.scene.layout.Priority.ALWAYS);
        root.getChildren().add(cols);
    }

    private void studentPreview(String fullName) {
        // rebuild whole screen for the picked student (simple for Phase 2-3)
        JsonStore.get().db().users.stream()
                .filter(u -> u.getFullName().equals(fullName)).findFirst()
                .ifPresent(u -> {
                    root.getChildren().removeIf(n -> n.getStyleClass().contains("preview-holder"));
                });
        build();
    }

    private void renderPreview(VBox col, String studentId) {
        col.getStyleClass().add("preview-holder");
        List<Certificate> certs = CertificateService.ofStudent(studentId);
        if (certs.isEmpty()) {
            col.getChildren().add(Ui.card(Ui.label(
                    "No certificate yet. Reach 85% overall and finish all lessons of a course "
                  + "to have one issued automatically.", "muted")));
            return;
        }
        for (Certificate c : certs) {
            Course course = ProgressService.course(c.getCourseId());
            User owner = JsonStore.get().db().users.stream()
                    .filter(u -> u.getUserId().equals(c.getStudentId())).findFirst().orElse(null);
            VBox frame = new VBox(8,
                    Ui.label("ER", "logo-badge"),
                    Ui.label("CERTIFICATE OF COMPLETION", "tiny", "muted"),
                    Ui.label(course == null ? "?" : course.getTitle(), "heading"),
                    Ui.label("This certifies that", "muted", "tiny"),
                    Ui.label(owner == null ? "?" : owner.getFullName(), "cert-name"),
                    Ui.label("completed the course with a total score of "
                            + c.getFinalScorePct() + "%", "tiny", "muted"),
                    Ui.label("Verification code:  " + c.getVerificationCode(), "code-chip"));
            frame.getStyleClass().add("cert-frame");
            frame.setAlignment(Pos.CENTER);
            col.getChildren().add(frame);
        }
    }
}
