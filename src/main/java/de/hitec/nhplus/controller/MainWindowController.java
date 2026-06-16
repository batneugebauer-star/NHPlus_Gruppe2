package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.hitec.nhplus.utils.SessionManager;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private void handleShowAllPatient(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllPatientView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleShowAllCaregiver(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllCaregiverView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleShowAllTreatments(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllTreatmentView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleShowAllCaregivers() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllCaregiverView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Wird aufgerufen wenn der Admin auf den Button "Passwort Reset" klickt.
     * öffnet ein neues Fenster zum Zurücksetzen eines Benutzerpassworts.
     * Gehört zum Login-System aus Workstream C.
     */
    @FXML
    private void handleShowPasswordReset() {
        try {
            // Passwort-Reset-Fenster öffnen
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/de/hitec/nhplus/PasswordResetView.fxml"));
            VBox pane = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Passwort zurücksetzen");
            stage.setScene(new Scene(pane));
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private Button buttonPasswordReset;

    /**
     * Wird beim Laden des Hauptfensters automatisch aufgerufen.
     * Blendet den Passwort-Reset Button aus, wenn der eingeloggte
     * Benutzer kein Admin ist. Prüft zuerst ob der Button im FXML-Layout
     * überhaupt vorhanden ist, um eine NullPointerException zu vermeiden.
     */

    @FXML
    private void initialize() {
        // Erst prüfen, ob der Button überhaupt im FXML-Layout existiert!
        if (buttonPasswordReset != null) {
            // passwort reset button NUR für admins anzeigen!
            if (!SessionManager.getInstance().isLoggedIn() || !SessionManager.getInstance().getLoggedInUser().isSuperUser()) {
                buttonPasswordReset.setVisible(false);
            }
        }
    }
}
