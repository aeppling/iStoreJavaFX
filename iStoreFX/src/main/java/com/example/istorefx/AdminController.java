package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    private User _admin; // TODO -> change to Admin Use = error
    @FXML
    private Button _manageEmployeeButton;

    @FXML
    private Button _createStoreButton;

    @FXML
    private Button _manageInventoryButton;

    @FXML
    private Button _viewWhitelistButton;

    @FXML
    private Button _backButton;

    @FXML
    private Label pseudo;

    public void initialize() {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._admin = holder.getUser();
        pseudo.setText(_admin.getPseudo());

    }



}
