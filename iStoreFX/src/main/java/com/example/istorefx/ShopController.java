package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.control.Button;

public class ShopController {
    private StoreRecord _store;
    private User        _user;
    @FXML
    private Label _storeName;
    public void initialize() {
        SingletonStoreHolder holder = SingletonStoreHolder.getInstance();
        SingletonUserHolder holder2 = SingletonUserHolder.getInstance();
        this._store = holder.getStore();
        this._storeName.setText(this._store.getName());
        this._user = holder2.getUser();
    }
    public void Home() {
        Stage currentStage2 = (Stage) this._storeName.getScene().getWindow();
        currentStage2.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("store.fxml"));
        try {
            SingletonUserHolder userHolder = SingletonUserHolder.getInstance();
            userHolder.setUser(this._user);
            Scene scene = new Scene(fxmlLoader.load(), 910, 616);
            primaryStage.setTitle("iStore");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
