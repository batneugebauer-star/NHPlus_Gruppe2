package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.KeyWrapUtil;
import de.hitec.nhplus.utils.PasswordUtil;
import de.hitec.nhplus.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import javax.crypto.SecretKey;

// Controller für das Passwort-Reset-Fenster
// NUR ADMINS dürfen Passwörter zurücksetzten!
public class PasswordResetController {

    @FXML private TextField textFieldUsername;
    @FXML private PasswordField passwordFieldNew;
    @FXML private PasswordField passwordFieldConfirm;
    @FXML private Label labelResult;

    @FXML
    private void handleReset() {
        labelResult.setVisible(false);

        // prüfen ob der eingeloggte user ein admin ist
        if (!SessionManager.getInstance().isLoggedIn() ||
                !SessionManager.getInstance().getLoggedInUser().isSuperUser()) {
            showMessage("Keine Berechtigung.", true);
            return;
        }

        String username  = textFieldUsername.getText().trim();
        String newPw     = passwordFieldNew.getText();
        String confirmPw = passwordFieldConfirm.getText();

        if (username.isEmpty()) {
            showMessage("Bitte Benutzernamen eingeben.", true);
            return;
        }
        if (newPw.isEmpty()) {
            showMessage("Bitte neues Passwort eingeben.", true);
            return;
        }
        if (!newPw.equals(confirmPw)) {
            showMessage("Passwörter stimmen nicht überein.", true);
            return;
        }

        try {
            UserDao userDao = DaoFactory.getDaoFactory().createUserDao();
            User user = userDao.findByUsername(username);

            if (user == null) {
                showMessage("Benutzer nicht gefunden.", true);
                return;
            }

            // neuen Salt und Hash berechnen und speichern
            String newSalt = PasswordUtil.generateSalt();
            String newHash = PasswordUtil.hash(newPw, newSalt);
            SecretKey encryptionKey = SessionManager.getInstance().getDataEncryptionKey();
            if (encryptionKey == null) {
                showMessage("Verschlüsselungsschlüssel nicht geladen.", true);
                return;
            }
            KeyWrapUtil.WrappedKey wrappedKey = KeyWrapUtil.wrap(encryptionKey, newPw);
            user.setSalt(newSalt);
            user.setPasswordHash(newHash);
            user.setWrappedEncryptionKey(wrappedKey.wrappedKey());
            user.setWrappedEncryptionKeySalt(wrappedKey.salt());
            user.setWrappedEncryptionKeyIv(wrappedKey.iv());
            user.setWrappedEncryptionKeyIterations(wrappedKey.iterations());
            userDao.updatePassword(user);

            showMessage("Passwort erfolgreich zurückgesetzt.", false);

            textFieldUsername.clear();
            passwordFieldNew.clear();
            passwordFieldConfirm.clear();

        } catch (SQLException e) {
            showMessage("Datenbankfehler: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) textFieldUsername.getScene().getWindow();
        stage.close();
    }

    private void showMessage(String message, boolean isError) {
        labelResult.setText(message);
        labelResult.setStyle(isError ? "-fx-text-fill: #c0392b;" : "-fx-text-fill: #27ae60;");
        labelResult.setVisible(true);
    }
}
