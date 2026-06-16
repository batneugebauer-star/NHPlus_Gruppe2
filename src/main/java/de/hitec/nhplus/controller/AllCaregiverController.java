package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Role;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import de.hitec.nhplus.model.Caregiver;

import java.sql.SQLException;

/**
 * The <code>AllCaregiverController</code> contains the entire logic of the caregiver view. It determines which data is displayed and how to react to events.
 */
public class AllCaregiverController {

    @FXML
    private TableView<Caregiver> tableView;

    @FXML
    private TableColumn<Caregiver, Long> columnId;

    @FXML
    private TableColumn<Caregiver, String> columnFirstName;

    @FXML
    private TableColumn<Caregiver, String> columnSurname;

    @FXML
    private TableColumn<Caregiver, String> columnTelephone;

    @FXML
    private TableColumn<Caregiver, String> colRole;

    @FXML
    private Button buttonDelete;
    private TableView<Caregiver> tableView;

    @FXML
    private TableColumn<Caregiver, Integer> columnId;

    @FXML
    private TableColumn<Caregiver, String> columnFirstName;

    @FXML
    private TableColumn<Caregiver, String> columnSurname;

    @FXML
    private TableColumn<Caregiver, String> columnTelephone;

    @FXML
    private TableColumn<Caregiver, String> columnQualification;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldTelephone;

    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldTelephone;

    @FXML
    private TextField textFieldQualification;

    private final ObservableList<Caregiver> caregivers = FXCollections.observableArrayList();
    private CaregiverDao dao;

    /**
     * When <code>initialize()</code> gets called, all fields are already initialized. For example from the FXMLLoader
     * after loading an FXML-File. At this point of the lifecycle of the Controller, the fields can be accessed and
     * configured.
     */
    private ComboBox<String> cmbRole;

    private final ObservableList<Caregiver> caregivers =
            FXCollections.observableArrayList();

    private CaregiverDao dao;

    public void initialize() {
        this.readAllAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("cid"));

        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnTelephone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        this.columnTelephone.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnQualification.setCellValueFactory(new PropertyValueFactory<>("qualification"));
        this.columnQualification.setCellFactory(TextFieldTableCell.forTableColumn());

        this.tableView.setItems(this.caregivers);

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Caregiver>() {
            @Override
            public void changed(ObservableValue<? extends Caregiver> observableValue, Caregiver oldCaregiver, Caregiver newCaregiver) {
                AllCaregiverController.this.buttonDelete.setDisable(newCaregiver == null);
            }
        });

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewCaregiverListener = (observableValue, oldText, newText) ->
                AllCaregiverController.this.buttonAdd.setDisable(!AllCaregiverController.this.areInputDataValid());
        this.textFieldSurname.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldFirstName.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldTelephone.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldQualification.textProperty().addListener(inputNewCaregiverListener);
    }

    @FXML
    public void handleOnEditFirstname(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    public void handleOnEditTelephone(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setPhoneNumber(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    public void handleOnEditQualification(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setQualification(event.getNewValue());
        this.doUpdate(event);
    }

    private void doUpdate(TableColumn.CellEditEvent<Caregiver, String> event) {
        try {
            this.dao.update(event.getRowValue());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void readAllAndShowInTableView() {
        this.caregivers.clear();
        this.dao = DaoFactory.getDaoFactory().createCaregiverDao();
        try {
            this.caregivers.addAll(this.dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleDelete() {
        Caregiver selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                DaoFactory.getDaoFactory().createCaregiverDao().deleteById(selectedItem.getCid());
                this.tableView.getItems().remove(selectedItem);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @FXML
    public void handleAdd() {
        String surname = this.textFieldSurname.getText();
        String firstName = this.textFieldFirstName.getText();
        String phoneNumber = this.textFieldTelephone.getText();
        String qualification = this.textFieldQualification.getText();
        try {
            this.dao.create(new Caregiver(firstName, surname, phoneNumber, qualification));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        readAllAndShowInTableView();
        clearTextfields();
    }

    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldTelephone.clear();
        this.textFieldQualification.clear();
    }

    private boolean areInputDataValid() {
        return !this.textFieldFirstName.getText().isBlank() && !this.textFieldSurname.getText().isBlank() &&
                !this.textFieldTelephone.getText().isBlank() && !this.textFieldQualification.getText().isBlank();

        readAllAndShowInTableView();

        cmbRole.getItems().addAll(
                "LEITER",
                "REGISTERED_NURSE",
                "NURSING_ASSISTANT",
                "DOCTOR",
                "THERAPIST"
        );

        Role currentRole =
                de.hitec.nhplus.utils.PermissionManager.getRole();

        boolean hasAccess =
                currentRole == Role.ADMIN ||
                        currentRole == Role.LEITER;

        buttonAdd.setDisable(!hasAccess);
        buttonDelete.setDisable(true);
        tableView.setEditable(hasAccess);

        columnId.setCellValueFactory(
                new PropertyValueFactory<>("cid"));

        columnFirstName.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));
        columnFirstName.setCellFactory(
                TextFieldTableCell.forTableColumn());

        columnSurname.setCellValueFactory(
                new PropertyValueFactory<>("surname"));
        columnSurname.setCellFactory(
                TextFieldTableCell.forTableColumn());

        columnTelephone.setCellValueFactory(
                new PropertyValueFactory<>("telephoneNumber"));
        columnTelephone.setCellFactory(
                TextFieldTableCell.forTableColumn());

        colRole.setCellValueFactory(
                new PropertyValueFactory<>("role"));

        tableView.setItems(caregivers);

        tableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (hasAccess) {
                        buttonDelete.setDisable(newSelection == null);
                    }
                });

        ChangeListener<String> listener = (obs, oldText, newText) ->
                buttonAdd.setDisable(
                        !areInputDataValid() || !hasAccess);

        textFieldFirstName.textProperty().addListener(listener);
        textFieldSurname.textProperty().addListener(listener);
        textFieldTelephone.textProperty().addListener(listener);
    }

    @FXML
    public void handleOnEditFirstname(
            TableColumn.CellEditEvent<Caregiver, String> event) {

        event.getRowValue().setFirstName(event.getNewValue());
        doUpdate(event.getRowValue());
    }

    @FXML
    public void handleOnEditSurname(
            TableColumn.CellEditEvent<Caregiver, String> event) {

        event.getRowValue().setSurname(event.getNewValue());
        doUpdate(event.getRowValue());
    }

    @FXML
    public void handleOnEditTelephone(
            TableColumn.CellEditEvent<Caregiver, String> event) {

        event.getRowValue().setTelephoneNumber(event.getNewValue());
        doUpdate(event.getRowValue());
    }

    private void doUpdate(Caregiver caregiver) {
        try {
            dao.update(caregiver);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void readAllAndShowInTableView() {

        caregivers.clear();

        dao = DaoFactory.getDaoFactory()
                .createCaregiverDao();

        try {
            caregivers.addAll(dao.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDelete() {

        Caregiver selected =
                tableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                dao.deleteById(selected.getCid());
                caregivers.remove(selected);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleAdd() {

        String firstName =
                textFieldFirstName.getText().trim();

        String surname =
                textFieldSurname.getText().trim();

        String telephone =
                textFieldTelephone.getText().trim();

        String role =
                cmbRole.getValue();

        try {

            Caregiver caregiver =
                    new Caregiver(
                            0,
                            firstName,
                            surname,
                            telephone,
                            role
                    );

            dao.create(caregiver);

            readAllAndShowInTableView();
            clearTextfields();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearTextfields() {

        textFieldFirstName.clear();
        textFieldSurname.clear();
        textFieldTelephone.clear();
        cmbRole.getSelectionModel().clearSelection();
    }

    private boolean areInputDataValid() {

        return !textFieldFirstName.getText().isBlank()
                && !textFieldSurname.getText().isBlank()
                && !textFieldTelephone.getText().isBlank()
                && cmbRole.getValue() != null;
    }
}