package de.hitec.nhplus.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

public class AllCaregiverController {

    @FXML
    private TableView<?> tableView;

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonDelete;

    @FXML
    public void initialize() {
        // Wird später für Rollenrechte und Pflegekräfte-Verwaltung erweitert.
    }
}