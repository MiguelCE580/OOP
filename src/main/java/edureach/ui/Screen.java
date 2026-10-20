package edureach.ui;

/** Screens that receive a navigation parameter implement this. */
public interface Screen {
    default void init(String param) {}
}
