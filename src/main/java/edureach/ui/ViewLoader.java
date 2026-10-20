package edureach.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/** Loads FXML views from /edureach/ui/views. */
public final class ViewLoader {

    private ViewLoader() {}

    public static <C> LoadResult<C> load(String name) {
        URL url = ViewLoader.class.getResource("views/" + name + ".fxml");
        if (url == null) throw new IllegalStateException("Missing view: " + name);
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            return new LoadResult<>(root, loader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Could not load view " + name, e);
        }
    }

    public record LoadResult<C>(Parent root, C controller) {}
}
