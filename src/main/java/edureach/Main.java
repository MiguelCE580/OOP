package edureach;

import edureach.data.JsonStore;
import edureach.data.SeedData;
import edureach.ui.ThemeManager;
import edureach.ui.ViewLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * EduReach - Community Digital Literacy and Skill Assessment System.
 * CS 002 Advanced OOP · Phase 2-3 build (~70%): JavaFX UI + single-file
 * JSON persistence. MySQL lands in Phase 4.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Seed the single data file on first run.
        if (JsonStore.get().db().users.isEmpty()) {
            JsonStore.get().install(SeedData.build());
            for (var c : JsonStore.get().db().courses) {
                edureach.service.RemedialService.scanCourse(c.getCourseId());
            }
        }

        ViewLoader.LoadResult<?> login = ViewLoader.load("login");
        Scene scene = new Scene(login.root(), 1180, 760);
        ThemeManager.attach(scene);

        stage.setTitle("EduReach - Community Digital Literacy and Skill Assessment");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
