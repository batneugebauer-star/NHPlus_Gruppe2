package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Role;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;

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

    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldTelephone;

    @FXML
    private ComboBox<String> cmbRole;

    private final ObservableList<Caregiver> caregivers =
            FXCollections.observableArrayList();

    private CaregiverDao dao;

    public void initialize() {

        readAllAndShowInTableView();
// Rollen für neue Pflegekräfte
        cmbRole.getItems().addAll(
                "LEITER",
                "REGISTERED_NURSE",
                "NURSING_ASSISTANT",
                "DOCTOR",
                "THERAPIST"
        );

        Role currentRole =
                de.hitec.nhplus.utils.PermissionManager.getRole();
// Nur Admin und Leiter dürfen Pflegekräfte verwalten
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