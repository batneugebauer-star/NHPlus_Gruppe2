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
    private void handleShowPasswordReset() {
        try {
            // passwort reset fenster laden und anzeigen
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/de/hitec/nhplus/PasswordResetView.fxml"));
            VBox pane = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Passwort zurücksetzten");
            stage.setScene(new Scene(pane));
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private Button buttonPasswordReset;

@FXML
    private void initialize() {
        // passwort reset button NUR für admins anzeigen!
        if (!SessionManager.getInstance().isLoggedIn() || !SessionManager.getInstance().getLoggedInUser().isSuperUser()) {
            buttonPasswordReset.setVisible(false);
        }
    }
}
