package edureach.ui.controllers;

import edureach.model.Role;
import edureach.model.User;
import edureach.service.AccountLockedException;
import edureach.service.AppSession;
import edureach.service.AuthService;
import edureach.service.InvalidCredentialsException;
import edureach.ui.I18N;
import edureach.ui.ThemeManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/** Screen 1: login + role selection. */
public class LoginController {

    @FXML private Label welcomeLabel, signinLabel, userLabel, passLabel, errorLabel;
    @FXML private HBox roleBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn, fontMinus, fontPlus, contrastBtn, langBtn, themeBtn;

    private Role role = Role.STUDENT;

    @FXML
    private void initialize() {
        rebuildRoleTabs();
        refreshTexts();

        loginBtn.setOnAction(e -> doLogin());
        passwordField.setOnAction(e -> doLogin());

        fontMinus.setOnAction(e -> ThemeManager.bumpFont(-1));
        fontPlus.setOnAction(e -> ThemeManager.bumpFont(1));
        contrastBtn.setOnAction(e -> { ThemeManager.toggleContrast(); refreshTexts(); });
        themeBtn.setOnAction(e -> { ThemeManager.toggleDark(); refreshTexts(); });
        langBtn.setOnAction(e -> { ThemeManager.toggleLang(); refreshTexts(); rebuildRoleTabs(); });

        usernameField.setText("maria.santos");
        passwordField.setText("learn123");
    }

    private void rebuildRoleTabs() {
        roleBox.getChildren().clear();
        for (Role r : Role.values()) {
            Button b = new Button(r.label());
            b.getStyleClass().add("btn-chip");
            if (r == role) b.getStyleClass().add("btn-chip-on");
            b.setOnAction(e -> { role = r; rebuildRoleTabs(); });
            roleBox.getChildren().add(b);
        }
    }

    private void refreshTexts() {
        welcomeLabel.setText(I18N.t("welcome"));
        signinLabel.setText(I18N.t("signin"));
        userLabel.setText(I18N.t("username"));
        passLabel.setText(I18N.t("password"));
        loginBtn.setText(I18N.t("login"));
        contrastBtn.setText((ThemeManager.config().isHighContrast() ? "✓ " : "") + I18N.t("highcontrast"));
        themeBtn.setText(ThemeManager.isDark() ? "☀ Light mode" : "☾ Dark mode");
    }

    private void doLogin() {
        errorLabel.setText("");
        try {
            User user = AuthService.login(usernameField.getText(), passwordField.getText(), role);
            AppSession.signIn(user);
            ShellController.show(loginBtn.getScene(), user);
        } catch (InvalidCredentialsException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (AccountLockedException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
}
