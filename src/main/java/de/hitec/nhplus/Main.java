package de.hitec.nhplus;

import de.hitec.nhplus.datastorage.ConnectionBuilder;

import de.hitec.nhplus.utils.SetUpDB;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.*;
import java.util.Properties;

import java.io.IOException;

import de.hitec.nhplus.controller.LoginController;
import de.hitec.nhplus.utils.SetUpDB;
import javafx.scene.layout.VBox;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        showLoginWindow();
    }

    public void showLoginWindow(){
        try {
            // login fenster laden
            FXMLLoader loader = new FXMLLoader(
            Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            VBox loginPane = loader.load();

            // Stage wird an LoginController übergeben damit er später zum hauptfenster wechseln kann
            LoginController loginController = loader.getController();
            loginController.setStage(primaryStage);

            Scene scene = new Scene(loginPane);
            this.primaryStage.setTitle("NHPlus Anmeldung");
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            //beim schließen muss datenbankverbindung getrennt werden
            this.primaryStage.setOnCloseRequest(event -> {
                ConnectionBuilder.closeConnection();
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public void mainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            BorderPane pane = loader.load();

            Scene scene = new Scene(pane);
            this.primaryStage.setTitle("NHPlus");
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            this.primaryStage.setOnCloseRequest(event -> {
                ConnectionBuilder.closeConnection();
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}