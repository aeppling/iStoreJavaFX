package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

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

    @FXML
    private AnchorPane _CreateStorePane;

    @FXML
    private Button _closeDisplayCreateStoreButton;
    @FXML
    private Button _CreateStoreDisplayButton;

    @FXML
    private TextField storeNameField;
    @FXML
    private Label storeNameError;

    public void initialize() {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._admin = holder.getUser();
        this.pseudo.setText(this._admin.getPseudo());

    }

    public void DisplayCreateStore() {
        this._CreateStorePane.setVisible(true);

    }

    public void CloseDisplayCreateStore() {
        this._CreateStorePane.setVisible(false);

    }

    public void CreateStore() {
        String storeName = storeNameField.getText();
        Connection connection = null;
        boolean is_good = false;
        try {
            // Check if store name already exist in Database
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            is_good = CheckStoreExists(connection, storeName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!is_good){
            return ;
        }else{
            try {
                // Add store to DataBase
                String sqlStoreInsert = "INSERT INTO iStoreStores(name) VALUES(?)";
                PreparedStatement preparedStoreInsertStatement = connection.prepareStatement(sqlStoreInsert);
                preparedStoreInsertStatement.setString(1, storeName);
                preparedStoreInsertStatement.execute();
                preparedStoreInsertStatement.close();
                CloseDisplayCreateStore();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public boolean CheckStoreExists(Connection connection, String storeName) throws SQLException {
        boolean check = false;
        if (storeName.isEmpty()) {
            this.storeNameError.setText("Please enter a store name.");
        }
        String sqlEmailRequest = "SELECT * FROM iStoreStores WHERE name LIKE ?";
        PreparedStatement preparedMailStatement = connection.prepareStatement(sqlEmailRequest);
        preparedMailStatement.setString(1, storeName);
        ResultSet resultStore = preparedMailStatement.executeQuery();

        if (resultStore.next()) {
            this.storeNameError.setText("A store with this name is already used.");

        } else {
            this.storeNameError.setText("");
            check = true;
        }
        preparedMailStatement.close();
        return (check);

    }

}
