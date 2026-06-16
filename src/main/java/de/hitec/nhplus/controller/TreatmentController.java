package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.SQLException;
import java.time.LocalDate;

public class TreatmentController {

    @FXML
    private TextArea txtDetailDocumentation;

    @FXML
    private TextArea txtDetailDiagnosis;

    @FXML
    private TextArea txtDetailTherapy;

    @FXML
    private Label labelPatientName;

    @FXML
    private Label labelCareLevel;

    @FXML
    private TextField textFieldBegin;

    @FXML
    private TextField textFieldEnd;

    @FXML
    private TextField textFieldDescription;

    @FXML
    private TextArea textAreaRemarks;

    @FXML
    private DatePicker datePicker;

    private AllTreatmentController controller;
    private Stage stage;
    private Patient patient;
    private Treatment treatment;

    public void initializeController(AllTreatmentController controller, Stage stage, Treatment treatment) {
        this.stage = stage;
        this.controller= controller;
        PatientDao pDao = DaoFactory.getDaoFactory().createPatientDao();
        try {
            this.patient = pDao.read(treatment.getPid());
            this.treatment = treatment;
            showData();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        // Rollen-Sperre im Detail-Fenster
        de.hitec.nhplus.model.Role currentRole = de.hitec.nhplus.utils.PermissionManager.getRole();

        // Alle Detailfelder zunächst deaktivieren
        if (txtDetailDocumentation != null) txtDetailDocumentation.setDisable(true);
        if (txtDetailDiagnosis != null) txtDetailDiagnosis.setDisable(true);
        if (txtDetailTherapy != null) txtDetailTherapy.setDisable(true);

        // Rollenabhängige Freigabe der Bearbeitungsfelder
        if (currentRole == de.hitec.nhplus.model.Role.ADMIN) {

            if (txtDetailDocumentation != null)
                txtDetailDocumentation.setDisable(false);

            if (txtDetailDiagnosis != null)
                txtDetailDiagnosis.setDisable(false);

            if (txtDetailTherapy != null)
                txtDetailTherapy.setDisable(false);

        }
        else if (currentRole == de.hitec.nhplus.model.Role.REGISTERED_NURSE) {

            if (txtDetailDocumentation != null)
                txtDetailDocumentation.setDisable(false);

        }
        else if (currentRole == de.hitec.nhplus.model.Role.DOCTOR) {

            if (txtDetailDiagnosis != null)
                txtDetailDiagnosis.setDisable(false);

        }
        else if (currentRole == de.hitec.nhplus.model.Role.THERAPIST) {

            if (txtDetailTherapy != null)
                txtDetailTherapy.setDisable(false);

        }
    }

    private void showData(){
        this.labelPatientName.setText(patient.getSurname()+", "+patient.getFirstName());
        this.labelCareLevel.setText(patient.getCareLevel());
        LocalDate date = DateConverter.convertStringToLocalDate(treatment.getDate());
        this.datePicker.setValue(date);
        this.textFieldBegin.setText(this.treatment.getBegin());
        this.textFieldEnd.setText(this.treatment.getEnd());
        this.textFieldDescription.setText(this.treatment.getDescription());

        if (this.txtDetailDocumentation != null) this.txtDetailDocumentation.clear();
        if (this.txtDetailDiagnosis != null) this.txtDetailDiagnosis.clear();
        if (this.txtDetailTherapy != null) this.txtDetailTherapy.clear();

        String gesamtText = this.treatment.getRemarks() != null ? this.treatment.getRemarks() : "";

        if (gesamtText.contains("Diagnose:") || gesamtText.contains("Therapie:") || gesamtText.contains("Pflege:")) {
            String[] parts = gesamtText.split(" \\| ");
            for (String part : parts) {
                if (part.startsWith("Pflege:") && this.txtDetailDocumentation != null) {
                    this.txtDetailDocumentation.setText(part.replace("Pflege: ", ""));
                } else if (part.startsWith("Diagnose:") && this.txtDetailDiagnosis != null) {
                    this.txtDetailDiagnosis.setText(part.replace("Diagnose: ", ""));
                } else if (part.startsWith("Therapie:") && this.txtDetailTherapy != null) {
                    this.txtDetailTherapy.setText(part.replace("Therapie: ", ""));
                }
            }
        } else {
            if (this.txtDetailDocumentation != null) {
                this.txtDetailDocumentation.setText(gesamtText);
            }
        }
    }

    @FXML
    public void handleChange() {

        this.treatment.setDate(this.datePicker.getValue().toString());
        this.treatment.setBegin(textFieldBegin.getText());
        this.treatment.setEnd(textFieldEnd.getText());
        this.treatment.setDescription(textFieldDescription.getText());

        String remarks = "";

        if (txtDetailDocumentation != null &&
                !txtDetailDocumentation.getText().isBlank()) {

            remarks += "Pflege: "
                    + txtDetailDocumentation.getText();
        }

        if (txtDetailDiagnosis != null &&
                !txtDetailDiagnosis.getText().isBlank()) {

            if (!remarks.isEmpty()) remarks += " | ";

            remarks += "Diagnose: "
                    + txtDetailDiagnosis.getText();
        }

        if (txtDetailTherapy != null &&
                !txtDetailTherapy.getText().isBlank()) {

            if (!remarks.isEmpty()) remarks += " | ";

            remarks += "Therapie: "
                    + txtDetailTherapy.getText();
        }

        this.treatment.setRemarks(remarks);

        doUpdate();
        controller.readAllAndShowInTableView();
        stage.close();
    }

    private void doUpdate(){
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            dao.update(treatment);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(){
        stage.close();
    }
}