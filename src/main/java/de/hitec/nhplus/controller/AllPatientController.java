package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.PermissionManager;

import java.sql.SQLException;
import java.time.LocalDate;


/**
 * The <code>AllPatientController</code> contains the entire logic of the patient view. It determines which data is displayed and how to react to events.
 */
public class AllPatientController {

    @FXML
    private TableView<Patient> tableView;

    @FXML
    private TableColumn<Patient, Integer> columnId;

    @FXML
    private TableColumn<Patient, String> columnFirstName;

    @FXML
    private TableColumn<Patient, String> columnSurname;

    @FXML
    private TableColumn<Patient, String> columnDateOfBirth;

    @FXML
    private TableColumn<Patient, String> columnCareLevel;

    @FXML
    private TableColumn<Patient, String> columnRoomNumber;

    @FXML
    private TableColumn<Patient, String> columnAssets;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldDateOfBirth;

    @FXML
    private TextField textFieldCareLevel;

    @FXML
    private TextField textFieldRoomNumber;

    @FXML
    private TextField textFieldAssets;

    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    private PatientDao dao;

    /**
     * When <code>initialize()</code> gets called, all fields are already initialized. For example from the FXMLLoader
     * after loading an FXML-File. At this point of the lifecycle of the Controller, the fields can be accessed and
     * configured.
     */
    public void initialize() {
        this.readAllAndShowInTableView();

        // Berechtigungsprüfung für Buttons und Tabellen-Editierbarkeit
        de.hitec.nhplus.model.Role role = de.hitec.nhplus.utils.PermissionManager.getRole();
        boolean hasAccess = (role == de.hitec.nhplus.model.Role.LEITER || role == de.hitec.nhplus.model.Role.ADMIN);
        this.buttonAdd.setDisable(!hasAccess);
        this.buttonDelete.setDisable(!hasAccess);
        this.tableView.setEditable(hasAccess);

        // Tabellenspalten initialisieren
        this.columnId.setCellValueFactory(new PropertyValueFactory<>("pid"));
        this.setupEditableColumn(this.columnFirstName, "firstName");
        this.setupEditableColumn(this.columnSurname, "surname");
        this.setupEditableColumn(this.columnDateOfBirth, "dateOfBirth");
        this.setupEditableColumn(this.columnCareLevel, "careLevel");
        this.setupEditableColumn(this.columnRoomNumber, "roomNumber");
        this.setupEditableColumn(this.columnAssets, "assets");

        this.tableView.setItems(this.patients);

        // Listener für Auswahl- und Eingabevalidierung
        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                this.buttonDelete.setDisable(newSelection == null));

        ChangeListener<String> inputNewPatientListener = (obs, oldText, newText) ->
                this.buttonAdd.setDisable(!this.areInputDataValid());

        this.textFieldSurname.textProperty().addListener(inputNewPatientListener);
        this.textFieldFirstName.textProperty().addListener(inputNewPatientListener);
        this.textFieldDateOfBirth.textProperty().addListener(inputNewPatientListener);
        this.textFieldCareLevel.textProperty().addListener(inputNewPatientListener);
        this.textFieldRoomNumber.textProperty().addListener(inputNewPatientListener);
        this.textFieldAssets.textProperty().addListener(inputNewPatientListener);
    }

    /**
     * Configures a table column as editable text column bound to the given patient property.
     *
     * @param column table column to configure
     * @param property property name used by {@link PropertyValueFactory}
     */
    private void setupEditableColumn(TableColumn<Patient, String> column, String property) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
    }


    /**
     * When a cell of the column with first names was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditFirstname(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with surnames was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with dates of birth was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditDateOfBirth(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setDateOfBirth(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with care levels was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditCareLevel(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setCareLevel(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with room numbers was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditRoomNumber(TableColumn.CellEditEvent<Patient, String> event){
        event.getRowValue().setRoomNumber(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with assets was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditAssets(TableColumn.CellEditEvent<Patient, String> event){
        event.getRowValue().setAssets(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * Updates a patient by calling the method <code>update()</code> of {@link PatientDao}.
     *
     * @param event Event including the changed object and the change.
     */
    private void doUpdate(TableColumn.CellEditEvent<Patient, String> event) {

        if (!PermissionManager.canManagePatients()) {
            return;
        }

        try {
            this.dao.update(event.getRowValue());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Reloads all patients to the table by clearing the list of all patients and filling it again by all persisted
     * patients, delivered by {@link PatientDao}.
     */
    private void readAllAndShowInTableView() {
        this.patients.clear();
        this.dao = DaoFactory.getDaoFactory().createPatientDao();
        try {
            this.patients.addAll(this.dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method handles events fired by the button to delete patients. It calls {@link PatientDao} to delete the
     * patient from the database and removes the object from the list, which is the data source of the
     * <code>TableView</code>.
     */
    @FXML
    public void handleDelete() {

        if (!PermissionManager.canManagePatients()) {
            return;
        }

        Patient selectedItem = this.tableView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            try {
                DaoFactory.getDaoFactory().createPatientDao().deleteById(selectedItem.getPid());
                this.tableView.getItems().remove(selectedItem);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * This method handles the events fired by the button to add a patient. It collects the data from the
     * <code>TextField</code>s, creates an object of class <code>Patient</code> of it and passes the object to
     * {@link PatientDao} to persist the data.
     */
    @FXML
    private void handleAdd() {
        String surname = this.textFieldSurname.getText().trim();
        String firstname = this.textFieldFirstName.getText().trim();
        String birthday = this.textFieldDateOfBirth.getText().trim();
        String careLevel = this.textFieldCareLevel.getText().trim();
        String room = this.textFieldRoomNumber.getText().trim();
        String assets = this.textFieldAssets.getText().trim();

        if (firstname.isEmpty() || surname.isEmpty()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING, "Bitte Vorname und Nachname ausfüllen!", javafx.scene.control.ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            if (this.dao == null) {
                this.dao = DaoFactory.getDaoFactory().createPatientDao();
            }

            LocalDate date = DateConverter.convertStringToLocalDate(birthday);
            Patient patient = new Patient(firstname, surname, date, careLevel, room, assets);

            this.dao.create(patient);

            // Holt die Daten frisch aus der DB und aktualisiert die Tabelle
            this.readAllAndShowInTableView();
            this.tableView.refresh();

            this.textFieldFirstName.clear();
            this.textFieldSurname.clear();
            this.textFieldDateOfBirth.clear();
            this.textFieldCareLevel.clear();
            this.textFieldRoomNumber.clear();
            this.textFieldAssets.clear();

        } catch (Exception e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Fehler beim Speichern: " + e.getMessage(), javafx.scene.control.ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Clears all contents from all <code>TextField</code>s.
     */
    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldDateOfBirth.clear();
        this.textFieldCareLevel.clear();
        this.textFieldRoomNumber.clear();
        this.textFieldAssets.clear();
    }

    /**
     * Validates the add-patient input fields and checks date formatting.
     *
     * @return true if all required inputs are present and valid, otherwise false
     */
    private boolean areInputDataValid() {
        if (!PermissionManager.canManagePatients()) {
            return !this.textFieldFirstName.getText().isBlank() &&
                    !this.textFieldSurname.getText().isBlank() &&
                    !this.textFieldDateOfBirth.getText().isBlank();
        }

        if (!this.textFieldDateOfBirth.getText().isBlank()) {
            try {
                DateConverter.convertStringToLocalDate(this.textFieldDateOfBirth.getText());
            } catch (Exception exception) {
                return false;
            }
        }

        return !this.textFieldFirstName.getText().isBlank() &&
                !this.textFieldSurname.getText().isBlank() &&
                !this.textFieldDateOfBirth.getText().isBlank() &&
                !this.textFieldCareLevel.getText().isBlank() &&
                !this.textFieldRoomNumber.getText().isBlank() &&
                !this.textFieldAssets.getText().isBlank();
    }
}