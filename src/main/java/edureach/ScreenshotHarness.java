package edureach;

import edureach.data.JsonStore;
import edureach.data.SeedData;
import edureach.model.Role;
import edureach.model.User;
import edureach.service.AppSession;
import edureach.service.AuthService;
import edureach.ui.ThemeManager;
import edureach.ui.ViewLoader;
import edureach.ui.controllers.ShellController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Dev-only harness: walks the main screens and saves PNG snapshots so the
 * team can review the UI without clicking through everything.
 * Run headless with: xvfb-run -a mvn javafx:run -Djavafx.mainClass=edureach.ScreenshotHarness
 */
public class ScreenshotHarness extends Application {

    private Scene scene;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        if (JsonStore.get().db().users.isEmpty()) {
            JsonStore.get().install(SeedData.build());
            for (var c : JsonStore.get().db().courses) {
                edureach.service.RemedialService.scanCourse(c.getCourseId());
            }
        }
        ViewLoader.LoadResult<?> login = ViewLoader.load("login");
        scene = new Scene(login.root(), 1240, 820);
        ThemeManager.attach(scene);
        stage.setScene(scene);
        stage.show();

        new Thread(this::shoot).start();
    }

    private void shoot() {
        try {
            File dir = new File("shots");
            dir.mkdirs();
            sleep(900);
            snap("01-login-dark");

            // light mode login for the design comparison
            fx(() -> ThemeManager.toggleDark());
            sleep(400);
            snap("02-login-light");
            fx(() -> ThemeManager.toggleDark());
            sleep(300);

            // student flow
            signIn("maria.santos", "learn123", Role.STUDENT);
            sleep(700);
            snap("03-student-dashboard");
            fx(() -> ShellController.open("course", "c1"));
            sleep(500);
            snap("04-student-course");
            fx(() -> ShellController.open("quiz_take", "q2"));
            sleep(600);
            snap("05-quiz-taking");
            fx(() -> ShellController.open("quiz_results", "sub-6"));
            sleep(500);
            snap("06-quiz-results");
            fx(() -> ShellController.open("curriculum", null));
            sleep(500);
            snap("07-curriculum");
            fx(() -> ShellController.open("certificates", null));
            sleep(500);
            snap("08-certificates-student");

            // at-risk student: remedial banner + learning plan
            signIn("mark.villanueva", "learn123", Role.STUDENT);
            sleep(700);
            snap("08b-mark-dashboard");
            fx(() -> ShellController.open("course", "c1"));
            sleep(500);
            snap("08c-mark-plan");

            // instructor flow
            signIn("ms.reyes", "teach123", Role.INSTRUCTOR);
            sleep(700);
            snap("09-course-manager");
            fx(() -> ShellController.open("attendance", null));
            sleep(500);
            snap("10-attendance");
            fx(() -> ShellController.open("gradebook", null));
            sleep(500);
            snap("11-gradebook");
            fx(() -> ShellController.open("analytics", null));
            sleep(500);
            snap("12-analytics");
            fx(() -> ShellController.open("interventions", null));
            sleep(500);
            snap("13-interventions");

            // light-mode sample
            fx(() -> ShellController.open("gradebook", null));
            sleep(400);
            fx(() -> ThemeManager.toggleDark());
            sleep(400);
            snap("14-gradebook-light");
            fx(() -> ThemeManager.toggleDark());

            // admin
            signIn("admin", "admin123", Role.ADMIN);
            sleep(700);
            snap("15-admin");

            System.out.println("SHOTS DONE");
            Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void signIn(String username, String password, Role role) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                User u = AuthService.login(username, password, role);
                AppSession.signIn(u);
                ShellController.show(scene, u);
            } catch (Throwable t) { t.printStackTrace(); }
            latch.countDown();
        });
        latch.await();
    }

    private void fx(Runnable r) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try { r.run(); } catch (Throwable t) { t.printStackTrace(); }
            finally { latch.countDown(); }
        });
        latch.await();
    }

    private void snap(String name) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            final WritableImage[] img = new WritableImage[1];
            Platform.runLater(() -> {
                img[0] = scene.getRoot().snapshot(new javafx.scene.SnapshotParameters(), null);
                latch.countDown();
            });
            latch.await();
            ImageIO.write(SwingFXUtils.fromFXImage(img[0], null), "png",
                    new File("shots/" + name + ".png"));
            System.out.println("shot " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        launch(args);
    }
}
