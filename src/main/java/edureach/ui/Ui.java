package edureach.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Small widget factory so every screen shares the design language. */
public final class Ui {

    private Ui() {}

    public static Label label(String text, String... classes) {
        Label l = new Label(text);
        l.getStyleClass().addAll(classes);
        return l;
    }

    public static Button button(String text, String... classes) {
        Button b = new Button(text);
        b.getStyleClass().addAll(classes);
        return b;
    }

    public static HBox hbox(double spacing, Node... children) {
        HBox h = new HBox(spacing, children);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    public static VBox vbox(double spacing, Node... children) {
        VBox v = new VBox(spacing, children);
        return v;
    }

    public static Node spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    public static VBox card(Node... children) {
        VBox c = new VBox(10, children);
        c.getStyleClass().add("card");
        return c;
    }

    /** Gradient header card: title, subtitle, right-aligned actions. */
    public static HBox hero(String title, String subtitle, Node... actions) {
        Label t = label(title, "hero-title");
        Label s = label(subtitle == null ? "" : subtitle, "hero-sub");
        VBox left = vbox(2, t, s);
        HBox hero = new HBox(12, left);
        hero.getStyleClass().add("hero");
        HBox act = hbox(8, actions);
        act.setAlignment(Pos.CENTER_RIGHT);
        hero.getChildren().addAll(spacer(), act);
        HBox.setMargin(act, new Insets(0, 0, 0, 0));
        return hero;
    }

    public static Label chip(String text, String kind) {
        return label(text, "chip", kind);
    }

    /** Status chip mapping used across gradebook / tables. */
    public static Label statusChip(String status) {
        switch (status) {
            case "PASSING":     return chip("✓ Passing", "chip-ok");
            case "CERT_READY":  return chip("★ Certificate ready", "chip-dark");
            case "NEEDS_REVIEW":return chip("! Needs review", "chip-warn");
            case "COMPLETED":   return chip("✓ Completed", "chip-ok");
            case "IN_PROGRESS": return chip("… In progress", "chip-warn");
            case "OPEN":        return chip("! Open", "chip-warn");
            case "ACTIVE":      return chip("Active", "chip-ok");
            case "LOCKED":      return chip("Locked", "chip-warn");
            default:            return chip("Not started", "chip-mute");
        }
    }

    public static ProgressBar bar(double pct) {
        ProgressBar p = new ProgressBar(Math.max(0, Math.min(1, pct / 100.0)));
        p.setPrefHeight(8);
        p.setMinWidth(60);
        return p;
    }

    public static Label avatar(String initials) {
        return label(initials, "avatar");
    }

    public static VBox statCard(String glyph, String value, String caption) {
        Label g = label(glyph, "badge-dot");
        g.getStyleClass().add("badge-off");
        g.setStyle("-fx-background-color: transparent; -fx-border-color: -er-border;"
                 + "-fx-border-radius: 999; -fx-font-size: 15px; -fx-text-fill: -er-accent;");
        VBox v = vbox(4, hbox(8, g, vbox(0, label(value, "heading"), label(caption, "muted", "tiny"))));
        v.getStyleClass().add("card");
        v.setPadding(new Insets(12, 14, 12, 14));
        return v;
    }

    public static HBox row(Node... children) {
        HBox r = hbox(10, children);
        r.getStyleClass().add("tbl-row");
        return r;
    }

    public static HBox tableHead(String... cols) {
        HBox h = new HBox(10);
        h.getStyleClass().add("tbl-head");
        for (String c : cols) h.getChildren().add(label(c.toUpperCase(), "tbl-head"));
        return h;
    }

    public static void info(String title, String message) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION, message);
        a.setTitle(title);
        a.setHeaderText(title);
        a.showAndWait();
    }

    /** Stub notice for features scheduled in the remaining 30%. */
    public static void phase4(String feature) {
        info("Planned for Phase 4",
                feature + " is scheduled for the remaining 30% of the project "
              + "(MySQL + PDF phase). The button is wired but intentionally inactive.");
    }
}
