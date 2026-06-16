package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.DataEncryptionException;
import de.hitec.nhplus.utils.EncryptionUtil;
import de.hitec.nhplus.utils.KeyWrapUtil;
import de.hitec.nhplus.utils.PasswordUtil;
import de.hitec.nhplus.utils.SessionManager;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

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
import javax.crypto.SecretKey;

/**
 * Diese Klasse ist der Controller für das Login-Fenster.
 * Sie prüft die Eingaben des Benutzers und vergleicht das Passwort
 * mit dem gespeicherten Hash aus der Datenbank.
 * Bei erfolgreichem Login wird zum Hauptfenster gewechselt.
 */
public class LoginController {

    @FXML private TextField textFieldUsername;
    @FXML private PasswordField passwordField;
    @FXML private Label labelError;

    private Stage stage;

    /**
     * Uebergibt die Stage an den Controller, damit er spaeter
     * die Szene wechseln kann (vom Login-Fenster zum Hauptfenster).
     *
     * @param stage die Stage der Anwendung
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    /**
     * Wird aufgerufen wenn der Benutzer auf den Login-Button klickt.
     * Prueft ob die Felder leer sind, laedt den Benutzer aus der Datenbank
     * und vergleicht das eingegebene Passwort mit dem gespeicherten Hash.
     *
     * Bei Erfolg wird zusaetzlich der Verschluesselungs-Schluessel des
     * Benutzers geladen (resolveDataKey) und, falls der Benutzer kein
     * Admin ist, ein Dialog zur Auswahl einer Arbeits-Rolle mit eigenem
     * Rollen-Passwort angezeigt. Erst danach wird das Hauptfenster geoeffnet.
     */
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
/**
 * Beschafft den Verschluesselungs-Schluessel fuer die Patientendaten
 * des angemeldeten Benutzers. Falls der Benutzer bereits einen
 * gewrappten Schluessel in der Datenbank hat, wird dieser mit dem
 * eingegebenen Passwort entschluesselt (unwrap). Falls noch keiner
 * existiert oder das Entschluesseln fehlschlaegt, wird ein neuer
 * Schluessel erzeugt, mit dem Passwort gewrappt und in der Datenbank
 * gespeichert.
 *
 * @param userDao das UserDao um den neuen Schluessel ggf. zu speichern
 * @param user der angemeldete Benutzer
 * @param password das eingegebene Klartext-Passwort, mit dem der
 *                  Schluessel ent- bzw. verschluesselt wird
 * @return der entschluesselte bzw. neu erzeugte Verschluesselungs-Schluessel
 * @throws SQLException wenn ein Datenbankfehler beim Speichern auftritt
 */
            SecretKey dataKey = resolveDataKey(userDao, user, password);
            // Wenn es KEIN globaler Admin ist, muss der Pfleger seine Rolle wählen
            if (!user.isSuperUser()) {
                // 1. Rollen-Auswahl-Dialog anzeigen
                java.util.List<String> choices = java.util.List.of(
                        "Leiter", "Registered Nurse", "Nursing Assistant", "Doctor", "Therapist"
                );
                ChoiceDialog<String> roleDialog = new ChoiceDialog<>("Registered Nurse", choices);
                roleDialog.setTitle("Rollen-Auswahl");
                roleDialog.setHeaderText("In welcher Rolle möchten Sie sich heute anmelden?");
                roleDialog.setContentText("Wählen Sie Ihre Rolle:");

                java.util.Optional<String> roleResult = roleDialog.showAndWait();
                if (!roleResult.isPresent()) {
                    showError("Anmeldung abgebrochen: Keine Rolle gewählt.");
                    return; // Login abbrechen, wenn das Fenster geschlossen wird
                }
                String selectedRoleName = roleResult.get();

                // 2. Passwort-Abfrage für die gewählte Rolle anzeigen
                TextInputDialog pinDialog = new TextInputDialog();
                pinDialog.setTitle("Rollen-Freischaltung");
                pinDialog.setHeaderText("Geben Sie das Passwort für die Rolle '" + selectedRoleName + "' ein:");
                pinDialog.setContentText("Passwort:");

                java.util.Optional<String> pinResult = pinDialog.showAndWait();
                if (!pinResult.isPresent()) {
                    showError("Anmeldung abgebrochen: Kein Passwort eingegeben.");
                    return;
                }
                String enteredPin = pinResult.get();

                // 3. Überprüfen, ob das Passwort zur gewählten Rolle passt
                boolean passwordValid = false;
                de.hitec.nhplus.model.Role resolvedEnum = de.hitec.nhplus.model.Role.REGISTERED_NURSE;

                if (selectedRoleName.equals("Leiter") && enteredPin.equals("leiter123")) {
                    passwordValid = true;
                    resolvedEnum = de.hitec.nhplus.model.Role.LEITER;
                } else if (selectedRoleName.equals("Registered Nurse") && enteredPin.equals("nurse123")) {
                    passwordValid = true;
                    resolvedEnum = de.hitec.nhplus.model.Role.REGISTERED_NURSE;
                } else if (selectedRoleName.equals("Nursing Assistant") && enteredPin.equals("assistant123")) {
                    passwordValid = true;
                    resolvedEnum = de.hitec.nhplus.model.Role.NURSING_ASSISTANT;
                } else if (selectedRoleName.equals("Doctor") && enteredPin.equals("doctor123")) {
                    passwordValid = true;
                    resolvedEnum = de.hitec.nhplus.model.Role.DOCTOR;
                } else if (selectedRoleName.equals("Therapist") && enteredPin.equals("therapist123")) {
                    passwordValid = true;
                    resolvedEnum = de.hitec.nhplus.model.Role.THERAPIST;
                }

                // 4. Auswertung
                if (!passwordValid) {
                    showError("Falsches Passwort für die Rolle " + selectedRoleName + "!");
                    passwordField.clear();
                    return; // Login abbrechen bei falschem Rollen-Passwort
                }

                // Rolle erfolgreich im User-Objekt für die aktuelle Session speichern
                user.setRole(resolvedEnum);
                System.out.println("Benutzer erfolgreich angemeldet als Rolle: " + resolvedEnum);
            }


            // Login erfolgreich — Session starten + Auto-Logout einrichten
            SessionManager.getInstance().login(user, dataKey, () ->
                    javafx.application.Platform.runLater(this::showLoginWindow)
            );

            showMainWindow();

        } catch (SQLException e) {
            showError("Datenbankfehler: " + e.getMessage());
            e.printStackTrace();
        } catch (DataEncryptionException e) {
            showError("Der Verschlüsselungsschlüssel konnte nicht geladen werden.");
            e.printStackTrace();
        }
    }

   /** private SecretKey resolveDataKey(UserDao userDao, User user, String password) throws SQLException {
        if (hasWrappedEncryptionKey(user)) {
            try {
                return KeyWrapUtil.unwrap(
                        user.getWrappedEncryptionKey(),
                        user.getWrappedEncryptionKeySalt(),
                        user.getWrappedEncryptionKeyIv(),
                        user.getWrappedEncryptionKeyIterations(),
                        password
                );
            } catch (DataEncryptionException e) {
                // Falls unwrap fehlschlägt, erzwingen wir ein neues Wrapping mit dem aktuellen Passwort
                System.out.println("Unwrap fehlgeschlagen für Benutzer: " + user.getUsername() + ". Generiere Schlüssel neu.");
            }
        }

        SecretKey bootstrapKey = EncryptionUtil.getBootstrapKey();
        KeyWrapUtil.WrappedKey wrappedKey = KeyWrapUtil.wrap(bootstrapKey, password);
        user.setWrappedEncryptionKey(wrappedKey.wrappedKey());
        user.setWrappedEncryptionKeySalt(wrappedKey.salt());
        user.setWrappedEncryptionKeyIv(wrappedKey.iv());
        user.setWrappedEncryptionKeyIterations(wrappedKey.iterations());
        userDao.updatePassword(user);
        return bootstrapKey;
    } **/

   private SecretKey resolveDataKey(UserDao userDao, User user, String password) throws SQLException {
       if (hasWrappedEncryptionKey(user)) {
           try {
               return KeyWrapUtil.unwrap(
                       user.getWrappedEncryptionKey(),
                       user.getWrappedEncryptionKeySalt(),
                       user.getWrappedEncryptionKeyIv(),
                       user.getWrappedEncryptionKeyIterations(),
                       password
               );
           } catch (DataEncryptionException e) {
               // Bei ungültigem Schlüssel wird ein neuer Schlüssel erzeugt
               System.out.println("WARNUNG: Schlüssel defekt. Generiere neu.");
           }
       }

       SecretKey bootstrapKey = EncryptionUtil.getBootstrapKey();
       KeyWrapUtil.WrappedKey wrappedKey = KeyWrapUtil.wrap(bootstrapKey, password);
       user.setWrappedEncryptionKey(wrappedKey.wrappedKey());
       user.setWrappedEncryptionKeySalt(wrappedKey.salt());
       user.setWrappedEncryptionKeyIv(wrappedKey.iv());
       user.setWrappedEncryptionKeyIterations(wrappedKey.iterations());
       userDao.updatePassword(user);
       return bootstrapKey;
   }
    /**
     * Prueft, ob der Benutzer bereits einen vollstaendigen gewrappten
     * Verschluesselungs-Schluessel in der Datenbank gespeichert hat.
     *
     * @param user der zu pruefende Benutzer
     * @return true wenn alle Felder fuer den gewrappten Schluessel vorhanden sind
     */
    private boolean hasWrappedEncryptionKey(User user) {
        return user.getWrappedEncryptionKey() != null
                && user.getWrappedEncryptionKeySalt() != null
                && user.getWrappedEncryptionKeyIv() != null
                && user.getWrappedEncryptionKeyIterations() > 0;
    }

    /**
     * Zeigt eine Fehlermeldung im Login-Fenster an.
     *
     * @param message der Text der Fehlermeldung
     */
    private void showError(String message) {
        labelError.setText(message);
        labelError.setVisible(true);
    }

    /**
     * Wechselt vom Login-Fenster zum Hauptfenster auf derselben Stage.
     * Wird nur nach erfolgreichem Login aufgerufen.
     */

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

    /**
     * Wechselt zurück zum Login-Fenster.
     * Wird aufgerufen wenn der automatische Logout nach 15 Minuten ausgelöst wird.
     */
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
