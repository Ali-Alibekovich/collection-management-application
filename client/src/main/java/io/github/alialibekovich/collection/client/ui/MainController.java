package io.github.alialibekovich.collection.client.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import io.github.alialibekovich.collection.client.core.ConsoleManager;
import io.github.alialibekovich.collection.client.core.Receiver;
import io.github.alialibekovich.collection.client.i18n.Bundle;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text loginText;

    @FXML
    private Button executeScriptButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button historyButton;

    @FXML
    private Button infoButton;

    @FXML
    private Button showButton;

    @FXML
    private Button visualizeButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button logoutButton;

    @FXML
    void initialize() {
        ResourceBundle resourceBundle = Bundle.getResourceBundle();
        helpButton.setText(resourceBundle.getString("helpButton"));
        logoutButton.setText(resourceBundle.getString("logoutButton"));
        visualizeButton.setText(resourceBundle.getString("visualizeButton"));
        exitButton.setText(resourceBundle.getString("exitButton"));
        showButton.setText(resourceBundle.getString("showButton"));
        infoButton.setText(resourceBundle.getString("infoButton"));
        historyButton.setText(resourceBundle.getString("historyButton"));
        loginText.setText(Receiver.myLogin);
        loginText.setFill(Paint.valueOf(Receiver.myColor));
        exitButton.setOnAction(actionEvent -> {
            try {
                ConsoleManager.invoke("exit");
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        logoutButton.setOnAction(actionEvent -> {
            try {
                ConsoleManager.invoke("logout");
                Stage stage = new Stage();
                try {
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/welcome.fxml"));
                    Scene scene = new Scene(root, 700, 400);
                    stage.setTitle(resourceBundle.getString("authorization"));
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                    stage = (Stage) logoutButton.getScene().getWindow();
                    stage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        helpButton.setOnAction(actionEvent -> {
            try {
                ConsoleManager.invoke("help");
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        infoButton.setOnAction(actionEvent -> {
            try {
                ConsoleManager.invoke("info");
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        historyButton.setOnAction(actionEvent -> {
            try {
                ConsoleManager.invoke("history");
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        showButton.setOnAction(actionEvent -> {
            try {
                ConsoleManager.invoke("show");
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        visualizeButton.setOnAction(actionEvent -> {
            try {
                ConsoleManager.invoke("visualize");
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
