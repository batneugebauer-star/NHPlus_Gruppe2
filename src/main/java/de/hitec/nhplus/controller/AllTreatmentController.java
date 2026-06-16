package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class AllTreatmentController {

    @FXML
    private TableView<Treatment> tableView;

    @FXML
    private TableColumn<Treatment, Integer> columnId;

    @FXML
    private TableColumn<Treatment, Integer> columnPid;

    @FXML
    private TableColumn<Treatment, String> columnDate;

    @FXML
    private TableColumn<Treatment, String> columnBegin;

    @FXML
    private TableColumn<Treatment, String> columnEnd;

    @FXML
    private TableColumn<Treatment, String> columnDescription;

    @FXML
    private ComboBox<String> comboBoxPatientSelection;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonNewTreament;

    private TreatmentDao dao;
    private final ObservableList<String> patientSelection = FXCollections.observableArrayList();
    private final ObservableList<Treatment> treatments = FXCollections.observableArrayList();
    private ArrayList<Patient> patientList;


    public void initialize() {
        readAllAndShowInTableView();
        comboBoxPatientSelection.setItems(patientSelection);
        comboBoxPatientSelection.getSelectionModel().select(0);

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("tid"));
        this.columnPid.setCellValueFactory(new PropertyValueFactory<>("pid"));
        this.columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.columnBegin.setCellValueFactory(new PropertyValueFactory<>("begin"));
        this.columnEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        this.columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        this.tableView.setItems(this.treatments);

        // Disabling the button to delete treatments as long, as no treatment was selected.
        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldTreatment, newTreatment) ->
                        AllTreatmentController.this.buttonDelete.setDisable(newTreatment == null));

        this.createComboBoxData();
        de.hitec.nhplus.model.Role role =
                de.hitec.nhplus.utils.PermissionManager.getRole();

        if (role == de.hitec.nhplus.model.Role.LEITER
                || role == de.hitec.nhplus.model.Role.NURSING_ASSISTANT) {

            buttonDelete.setDisable(true);
            buttonNewTreament.setDisable(true);
        }
    }

    public void readAllAndShowInTableView() {
        this.treatments.clear();
        comboBoxPatientSelection.getSelectionModel().select(0);
        this.dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            java.util.List<de.hitec.nhplus.model.Treatment> allTreatments = dao.readAll();
            de.hitec.nhplus.model.Role currentRole = de.hitec.nhplus.utils.PermissionManager.getRole();

            for (de.hitec.nhplus.model.Treatment t : allTreatments) {
                if (currentRole == de.hitec.nhplus.model.Role.ADMIN || currentRole == de.hitec.nhplus.model.Role.LEITER) {
                    this.treatments.add(t);
                }
                else if (currentRole == de.hitec.nhplus.model.Role.DOCTOR && t.getDescription().toLowerCase().contains("medizinisch")) {
                    this.treatments.add(t);
                }
                else if (currentRole == de.hitec.nhplus.model.Role.THERAPIST && t.getDescription().toLowerCase().contains("therapie")) {
                    this.treatments.add(t);
                }
                else if ((currentRole == de.hitec.nhplus.model.Role.REGISTERED_NURSE || currentRole == de.hitec.nhplus.model.Role.NURSING_ASSISTANT)
                        && !t.getDescription().toLowerCase().contains("therapie")) {
                    this.treatments.add(t);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void createComboBoxData() {
        patientSelection.clear();
        patientSelection.add("alle");

        PatientDao dao = DaoFactory.getDaoFactory().createPatientDao();
        try {
            patientList = (ArrayList<Patient>) dao.readAll();
            for (Patient patient: patientList) {
                this.patientSelection.add(formatPatientDisplayName(patient));
            }
            comboBoxPatientSelection.setItems(patientSelection);
            comboBoxPatientSelection.getSelectionModel().selectFirst(); // "alle" wird vorausgewählt
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private String formatPatientDisplayName(Patient patient) {
        return String.format("%s, %s", patient.getSurname(), patient.getFirstName());
    }

    @FXML
    public void handleComboBox() {
        String selectedPatient = this.comboBoxPatientSelection.getSelectionModel().getSelectedItem();
        this.treatments.clear();
        this.dao = DaoFactory.getDaoFactory().createTreatmentDao();

        if (selectedPatient == null || selectedPatient.equals("alle")) {
            try {
                this.treatments.addAll(this.dao.readAll());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        else {
            Patient patient = getPatientFromDisplayName(selectedPatient);
            if (patient != null) {
                try {
                    this.treatments.addAll(this.dao.readTreatmentsByPid(patient.getPid()));
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private Patient getPatientFromDisplayName(String displayName) {
        for (Patient patient : patientList) {
            if (displayName.equals(formatPatientDisplayName(patient))) {
                return patient;
            }
        }
        return null;
    }

    @FXML
    public void handleDelete() {

        de.hitec.nhplus.model.Role role =
                de.hitec.nhplus.utils.PermissionManager.getRole();

        if (role == de.hitec.nhplus.model.Role.LEITER
                || role == de.hitec.nhplus.model.Role.NURSING_ASSISTANT) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Keine Berechtigung");
            alert.setHeaderText(null);
            alert.setContentText("Sie dürfen keine Behandlungen löschen.");
            alert.showAndWait();
            return;
        }

        int index = this.tableView.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            return;
        }

        Treatment t = this.treatments.remove(index);

        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();

        try {
            dao.deleteById(t.getTid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleNewTreatment() {
        String selectedPatient = this.comboBoxPatientSelection.getSelectionModel().getSelectedItem();
        if (selectedPatient == null || selectedPatient.equals("alle")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Patient für die Behandlung fehlt!");
            alert.setContentText("Wählen Sie über die Combobox einen Patienten aus!");
            alert.showAndWait();
            return;
        }
        Patient patient = getPatientFromDisplayName(selectedPatient);
        if (patient == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Patient nicht gefunden!");
            alert.setContentText("Der ausgewählte Patient konnte nicht gefunden werden.");
            alert.showAndWait();
            return;
        }
        newTreatmentWindow(patient);
    }

    @FXML
    public void handleMouseClick() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            int index = this.tableView.getSelectionModel().getSelectedIndex();
            Treatment treatment = this.treatments.get(index);
            treatmentWindow(treatment);
        }
    }

    public void newTreatmentWindow(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/NewTreatmentView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // the primary stage should stay in the background
            Stage stage = new Stage();

            NewTreatmentController controller = loader.getController();
            controller.initialize(this, stage, patient);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void treatmentWindow(Treatment treatment){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/TreatmentView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // the primary stage should stay in the background
            Stage stage = new Stage();
            TreatmentController controller = loader.getController();
            controller.initializeController(this, stage, treatment);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
