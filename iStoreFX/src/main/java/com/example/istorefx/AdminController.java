package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.imageio.ImageIO;
import javafx.scene.layout.AnchorPane;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class AdminController {

    private User _admin; // TODO -> change to Admin Use = error
    @FXML
    private Button _manageEmployeeButton;
    @FXML
    private Button _createStoreButton;
    @FXML
    private Button _whitelistEmailButton;
    @FXML
    private Button _manageInventoryButton;
    @FXML
    private Button _backButton;
    @FXML
    private Label pseudo;
    @FXML
    private AnchorPane _whitelistPane;
    @FXML
    private AnchorPane _CreateStorePane;
    @FXML
    private AnchorPane _manageEmployeePane;

    @FXML
    private Button _closeDisplayCreateStoreButton;
    @FXML
    private Button _CreateStoreDisplayButton;

    @FXML
    private TextField storeNameField;
    @FXML
    private TextField emailWhitelistField;
    @FXML
    private TextField emailManageEmployeeField;
    @FXML
    private Label storeNameError;
    @FXML
    private Label emailWhitelistError;
    @FXML
    private Label emailManageEmployeeError;

    @FXML
    private Label storeImgUrlError;
    @FXML
    private TextField storeImgUrlField;




    public void initialize() {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._admin = holder.getUser();
        this.pseudo.setText(this._admin.getPseudo());

    }

    // Create New Store

    public void DisplayCreateStore() {
        this._CreateStorePane.setVisible(true);

    }

    public void CloseDisplayCreateStore() {
        this._CreateStorePane.setVisible(false);

    }

    public void CreateStore() {
        String storeName = storeNameField.getText();
        String imgUrl = storeImgUrlField.getText();
        Connection connection = null;
        boolean is_good = false;
        try {
            // Check if store name already exist in Database
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            is_good = CheckCreateStoreError(connection, storeName, imgUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!is_good){
            return ;
        }else{
            try {
                if (imgUrl.isEmpty()) {
                    String sqlStoreInsert = "INSERT INTO iStoreStores(name) VALUES(?)";
                    PreparedStatement preparedStoreInsertStatement = connection.prepareStatement(sqlStoreInsert);
                    preparedStoreInsertStatement.setString(1, storeName);
                    preparedStoreInsertStatement.execute();
                    preparedStoreInsertStatement.close();
                }else{
                    String sqlStoreInsert = "INSERT INTO iStoreStores(name, store_img) VALUES(?,?)";
                    PreparedStatement preparedStoreInsertStatement = connection.prepareStatement(sqlStoreInsert);
                    preparedStoreInsertStatement.setString(1, storeName);
                    preparedStoreInsertStatement.setString(2, imgUrl);
                    preparedStoreInsertStatement.execute();
                    preparedStoreInsertStatement.close();
                }
                // Add store to DataBase


                CloseDisplayCreateStore();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public boolean CheckCreateStoreError(Connection connection, String storeName, String imgUrl) throws SQLException {
        boolean check = true;
        if (storeName.isEmpty()) {
            this.storeNameError.setText("Please enter a store name.");
            check = false;
        }else{
            this.storeNameError.setText("");
        }
        if (!imgUrl.isEmpty()) {
            // Check if the url is an existing img
            try {
                URL url = new URL(imgUrl);
                BufferedImage image = ImageIO.read(url);
                if (image == null) {
                    check = false;
                    this.storeImgUrlError.setText("Please enter a valid img url.");
                }else{
                    this.storeNameError.setText("");
                }
            } catch (IOException e) {
                e.printStackTrace();
                check = false;
                this.storeImgUrlError.setText("Please enter a valid img url.");
            }
        }
        String sqlStoreNameRequest = "SELECT * FROM iStoreStores WHERE name = ?";
        PreparedStatement preparedStoreNameStatement = connection.prepareStatement(sqlStoreNameRequest);
        preparedStoreNameStatement.setString(1, storeName);
        ResultSet resultStore = preparedStoreNameStatement.executeQuery();

        if (resultStore.next()) {
            this.storeNameError.setText("A store with this name is already used.");
            check = false;
        } else {
            this.storeNameError.setText("");

        }
        preparedStoreNameStatement.close();
        return (check);

    }
    // WhiteList
    public void DisplayWhitelist() {
        this._whitelistPane.setVisible(true);

    }
    public void CloseDisplayWhitelist() {
        this._whitelistPane.setVisible(false);

    }
    public void AddToWhiteList(){
        String emailWhiteList = emailWhitelistField.getText();
        Connection connection = null;
        boolean is_good = false;
        try {
            // Check if store name already exist in Database
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            is_good = CheckMailWhiteListError(connection, emailWhiteList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!is_good){
            return ;
        }else{
            try {
                // Add store to DataBase
                String sqlMailWhitelistInsert = "INSERT INTO iStoreWhitelist(email) VALUES(?)";
                PreparedStatement preparedsqlMailWhitelistInsertStatement = connection.prepareStatement(sqlMailWhitelistInsert);
                preparedsqlMailWhitelistInsertStatement.setString(1, emailWhiteList);
                preparedsqlMailWhitelistInsertStatement.execute();
                preparedsqlMailWhitelistInsertStatement.close();
                CloseDisplayWhitelist();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean CheckMailWhiteListError(Connection connection, String emailWhiteList) throws SQLException{
        boolean check = true;
        if (emailWhiteList.isEmpty()) {
            this.emailWhitelistError.setText("Please enter a mail.");
            check = false;
        }else{
            this.emailWhitelistError.setText("");
        }
        String sqlEmailWhiteListRequest = "SELECT * FROM iStoreWhitelist WHERE email = ?";
        PreparedStatement preparedMailWhiteListStatement = connection.prepareStatement(sqlEmailWhiteListRequest);
        preparedMailWhiteListStatement.setString(1, emailWhiteList);
        ResultSet resultStore = preparedMailWhiteListStatement.executeQuery();

        if (resultStore.next()) {
            this.storeNameError.setText("The email is already whitelisted");
            check = false;
        } else {
            this.storeNameError.setText("");

        }
        preparedMailWhiteListStatement.close();
        return (check);

    }

    // Manage Employee
    public void DisplayManageEmployee() {
        this._manageEmployeePane.setVisible(true);

    }

    public void CloseDisplayManageEmployee() {
        this._manageEmployeePane.setVisible(false);

    }

    public void UpdateUser(){
        String emailManageEmployee = emailManageEmployeeField.getText();
        Connection connection = null;
        boolean is_good = false;
        try {
            // Check if store name already exist in Database
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            is_good = CheckMailManageEmployeeError(connection, emailManageEmployee);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private boolean CheckMailManageEmployeeError(Connection connection, String emailManageEmployee) {
        boolean check = true;
        if (emailManageEmployee.isEmpty()) {
            this.emailManageEmployeeError.setText("Please enter a mail.");
            check = false;
        }else{
            this.emailManageEmployeeError.setText("");
        }
        return check;
    }


}
