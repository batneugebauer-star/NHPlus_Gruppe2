package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.PasswordUtil;
import de.hitec.nhplus.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller für das Login-Fenster.
 * Prüft Benutzername + Passwort und wechselt bei Erfolg zum Hauptfenster.
 */
public class LoginController {

    @FXML private TextField textFieldUsername;
    @FXML private PasswordField passwordField;
    @FXML private Label labelError;

    private Stage stage;

    // Wird von Main.java aufgerufen damit wir die Scene wechseln können
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        // Fehlermeldung zuerst ausblenden
        labelError.setVisible(false);

        String username = textFieldUsername.getText().trim();
        String password = passwordField.getText();

        // Leere Felder prüfen
        if (username.isEmpty()) {
            showError("Bitte gib deinen Benutzernamen ein.");
            return;
        }
        if (password.isEmpty()) {
            showError("Bitte gib dein Passwort ein.");
            return;
        }

        try {
            UserDao userDao = DaoFactory.getDaoFactory().createUserDao();
            User user = userDao.findByUsername(username);

            // Kein User mit diesem Namen gefunden
            if (user == null) {
                showError("Benutzername oder Passwort falsch.");
                return;
            }

            // Passwort prüfen
            boolean ok = PasswordUtil.verify(password, user.getSalt(), user.getPasswordHash());

            if (!ok) {
                showError("Benutzername oder Passwort falsch.");
                passwordField.clear();
                return;
            }

            // Login erfolgreich — Session starten + Auto-Logout einrichten
            SessionManager.getInstance().login(user, () ->
                    javafx.application.Platform.runLater(this::showLoginWindow)
            );

            showMainWindow();

        } catch (SQLException e) {
            showError("Datenbankfehler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        labelError.setText(message);
        labelError.setVisible(true);
    }

    // Wechselt zum Hauptfenster auf derselben Stage
    private void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            BorderPane pane = loader.load();

            Scene mainScene = new Scene(pane);
            // Bei jeder Aktivität den Timer zurücksetzen (für Auto-Logout)
            mainScene.setOnMouseMoved(e -> SessionManager.getInstance().resetTimer());
            mainScene.setOnKeyPressed(e -> SessionManager.getInstance().resetTimer());

            stage.setScene(mainScene);
            stage.setTitle("NHPlus");

        } catch (IOException e) {
            showError("Fehler beim Laden des Hauptfensters.");
            e.printStackTrace();
        }
    }

    // Wechselt zurück zum Login (wird beim Auto-Logout aufgerufen)
    private void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            VBox loginPane = loader.load();

            LoginController ctrl = loader.getController();
            ctrl.setStage(stage);

            stage.setScene(new Scene(loginPane));
            stage.setTitle("NHPlus — Anmeldung");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}