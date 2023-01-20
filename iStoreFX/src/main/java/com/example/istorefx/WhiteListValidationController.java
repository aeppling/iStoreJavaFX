package com.example.istorefx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class WhiteListValidationController {
    @FXML
    private Button exitButton;
    @FXML
    private Button backButton;
    public void exit() {
        Platform.exit();
    }
    public void back() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        currentStage.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 356, 400);
            primaryStage.setTitle("iStore - Login page");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
