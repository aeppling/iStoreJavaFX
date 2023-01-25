package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javax.imageio.ImageIO;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;

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

    public void DisplayCreateStorePane() {
        // Display Create Store overlay
        this._CreateStorePane.setVisible(true);

    }

    public void CloseDisplayCreateStorePane() {
        // Close Create Store overlay
        this._CreateStorePane.setVisible(false);
    }

    public void CreateStore() throws SQLException {
        // Called when user clicks on Create Store button
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
                connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");

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


                CloseDisplayCreateStorePane();
            }catch (SQLException e) {
                e.printStackTrace();
            }
            connection.close();
        }



    }

    public boolean CheckCreateStoreError(Connection connection, String storeName, String imgUrl) throws SQLException {
        // Check Create Store overlay error after the User send form
        this.storeNameError.setText("");
        this.storeImgUrlError.setText("");
        boolean check = true;
        if (storeName.isEmpty()) {
            this.storeNameError.setText("Please enter a store name.");
            check = false;
        }
        if (!imgUrl.isEmpty()) {
            // Check if the url is an existing img
            try {
                URL url = new URL(imgUrl);
                BufferedImage image = ImageIO.read(url);
                if (image == null) {
                    check = false;
                    this.storeImgUrlError.setText("Please enter a valid img url.");
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
        }
        preparedStoreNameStatement.close();
        connection.close();
        return (check);

    }
    // WhiteList
    public void DisplayWhitelistPane() {
        // Display whitelist overlay
        this._whitelistPane.setVisible(true);

    }
    public void CloseDisplayWhitelistPane() {
        // Close Whitelist overlay
        this._whitelistPane.setVisible(false);

    }
    public void AddToWhiteList() throws SQLException {
        // Called when user click on Add to Whitelist button
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
                connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
                String sqlMailWhitelistInsert = "INSERT INTO iStoreWhitelist(email) VALUES(?)";
                PreparedStatement preparedsqlMailWhitelistInsertStatement = connection.prepareStatement(sqlMailWhitelistInsert);
                preparedsqlMailWhitelistInsertStatement.setString(1, emailWhiteList);
                preparedsqlMailWhitelistInsertStatement.execute();
                preparedsqlMailWhitelistInsertStatement.close();
                CloseDisplayWhitelistPane();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connection.close();

    }

    private boolean CheckMailWhiteListError(Connection connection, String emailWhiteList) throws SQLException{
        boolean check = true;
        this.emailWhitelistError.setText("");
        if (emailWhiteList.isEmpty()) {
            this.emailWhitelistError.setText("Please enter a mail.");
            check = false;
        }
        String sqlEmailWhiteListRequest = "SELECT * FROM iStoreWhitelist WHERE email = ?";
        PreparedStatement preparedMailWhiteListStatement = connection.prepareStatement(sqlEmailWhiteListRequest);
        preparedMailWhiteListStatement.setString(1, emailWhiteList);
        ResultSet resultStore = preparedMailWhiteListStatement.executeQuery();

        if (resultStore.next()) {
            this.emailWhitelistError.setText("The email is already whitelisted");
            check = false;
        }
        preparedMailWhiteListStatement.close();
        connection.close();
        return (check);
    }

    // Manage Employee
    public void DisplayManageEmployeePane() {
        // Display manage employee overlay
        this._manageEmployeePane.setVisible(true);

    }

    public void CloseDisplayManageEmployeePane() {
        // Close manage employee overlay
        this._manageEmployeePane.setVisible(false);

    }

    public void UpdateUser(){
        // Called when user click on Update Employee button
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
        if(!is_good){
            return;
        }else{
            RedirectToUpdatePage(emailManageEmployee);
        }
    }

    private boolean CheckMailManageEmployeeError(Connection connection, String emailManageEmployee) throws SQLException {
        // Check error from Manage employee form
        boolean check = true;
        this.emailManageEmployeeError.setText("");
        if (emailManageEmployee.isEmpty()) {
            this.emailManageEmployeeError.setText("Please enter a mail.");
            check = false;
        }
        String sqlEmailManageEmployeeRequest = "SELECT role FROM iStoreUsers WHERE email = ?";
        PreparedStatement preparedEmailManageEmployeeStatement = connection.prepareStatement(sqlEmailManageEmployeeRequest);
        preparedEmailManageEmployeeStatement.setString(1, emailManageEmployee);
        ResultSet resultEmployee = preparedEmailManageEmployeeStatement.executeQuery();
        if (resultEmployee.next()) {
            if(resultEmployee.getString("role").equals("admin")) {
                check = false;
                this.emailManageEmployeeError.setText("You cannot update an admin account.");
            }
        }else{
            check = false;
            this.emailManageEmployeeError.setText("No account using this email exists.");
        }
        connection.close();
        return check;
    }
    public void RedirectToUpdatePage(String emailManageEmployee){
        User user = this._admin;
        Stage currentStage2 = (Stage) _manageEmployeeButton.getScene().getWindow();
        currentStage2.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("UpdateEmployee.fxml"));
        try {
            SingletonUserHolder userHolder = SingletonUserHolder.getInstance();
            userHolder.setUser(user);
            SingletonEmailHolder emailHolder = SingletonEmailHolder.getInstance();
            emailHolder.setEmail(emailManageEmployee);

            Scene scene = new Scene(fxmlLoader.load(), 600, 616);
            primaryStage.setTitle("iStore");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Manage Inventory
    public void Store() {
        User user = this._admin;
        Stage currentStage2 = (Stage) _manageEmployeeButton.getScene().getWindow();
        currentStage2.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("store.fxml"));
        try {
            SingletonUserHolder userHolder = SingletonUserHolder.getInstance();
            userHolder.setUser(user);
            Scene scene = new Scene(fxmlLoader.load(), 910, 616);
            primaryStage.setTitle("iStore");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button _disconnectButton;
    public void Disconnect() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Disconnection");
        alert.setHeaderText("Do you want to disconnect ?");
        alert.setContentText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Stage currentStage = (Stage) _disconnectButton.getScene().getWindow();
            currentStage.close();
            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            try {
                Scene scene = new Scene(fxmlLoader.load(), 356, 400);
                primaryStage.setTitle("iStore - Login Page");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
