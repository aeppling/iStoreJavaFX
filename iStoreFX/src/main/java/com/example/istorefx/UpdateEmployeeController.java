package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.*;

public class UpdateEmployeeController {

    private User _admin;
    private String _email;
    private String _nickname;
    private int _id;
    private String password;

    @FXML
    private Label _nicknameLabel;
    @FXML
    private Label _emailLabel;
    @FXML
    private AnchorPane _updateEmailPane;
    @FXML
    private TextField _newEmailTextField;
    @FXML
    private Label _newEmailError;

    public void initialize() {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._admin = holder.getUser();
        this._id = GetAccountIdFromMail();
        this._nicknameLabel.setText(this._nickname);
        this._emailLabel.setText(this._email);

    }
    public void DeleteAccount() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            // Delete from StoreUserLink
            String sqlDeleteLink = "DELETE * FROM StoreUserLink WHERE UserID = ?";
            PreparedStatement preparedDeleteLinkStatement = connection.prepareStatement(sqlDeleteLink);
            preparedDeleteLinkStatement.setInt(1, this._id);
            preparedDeleteLinkStatement.execute();
            preparedDeleteLinkStatement.close();

            // Delete account from iStoreUsers
            String sqlDeleteAccount = "DELETE * FROM iStoreUsers WHERE email = ?";
            PreparedStatement preparedDeleteAccountStatement = connection.prepareStatement(sqlDeleteAccount);
            preparedDeleteAccountStatement.setString(1, this._email);
            preparedDeleteAccountStatement.execute();
            preparedDeleteAccountStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void DisplayUpdateNicknamePane(){

    }
    public void CloseUpdateNicknamePane(){

    }
    public void DisplayUpdatePasswordPane(){

    }
    public void CloseUpdatePasswordPane(){

    }
    public void DisplayUpdateEmailPane(){
        this._updateEmailPane.setVisible(true);
    }
    public void CloseUpdateEmailPane(){
        this._updateEmailPane.setVisible(false);
    }
    public void UpdateNickname(){

    }

    public void UpdateMail(){
        String newEmail = this._newEmailTextField.getText();
        Connection connection = null;
        boolean is_good = false;
        try {

            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            is_good = CheckErrorFromUpdateMail(connection, newEmail);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!is_good){
            return;
        }else{
            // Update DB
            try {
                String sqlUpdateEmail = "UPDATE `iStoreUsers` SET `email`=?, WHERE id = ?";
                PreparedStatement preparedMailUpdateStatement = connection.prepareStatement(sqlUpdateEmail);
                preparedMailUpdateStatement.setString(1, newEmail);
                preparedMailUpdateStatement.setInt(2, this._id);
                preparedMailUpdateStatement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


    }
    public boolean CheckErrorFromUpdateMail(Connection connection, String newEmail)throws SQLException{
        boolean check = true;
        if (newEmail.isEmpty()) {
            this._newEmailError.setText("Please enter a mail.");
            check = false;
        }else{
            this._newEmailError.setText("");
        }
        // Check if mail is already used
        String sqlEmailRequest = "SELECT * FROM iStoreUser WHERE email = ? AND id != ?";
        PreparedStatement preparedMailStatement = connection.prepareStatement(sqlEmailRequest);
        preparedMailStatement.setString(1, newEmail);
        preparedMailStatement.setInt(2, this._id);
        ResultSet resultMail = preparedMailStatement.executeQuery();

        if (resultMail.next()) {
            this._newEmailError.setText("An account is already registered with this email.");
            check = false;
        } else {
            this._newEmailError.setText("");

        }
        if(this._email.equals(newEmail)){

            this._newEmailError.setText("You have to enter a new email.");
            check = false;
        } else {
            this._newEmailError.setText("");

        }
        // Check if mail is whitelisted
        String sqlEmailWhiteListRequest = "SELECT * FROM iStoreWhitelist WHERE email = ?";
        PreparedStatement preparedMailWhitelistStatement = connection.prepareStatement(sqlEmailWhiteListRequest);
        preparedMailWhitelistStatement.setString(1, newEmail);
        ResultSet resultMailWhitelist = preparedMailWhitelistStatement.executeQuery();

        if(!resultMailWhitelist.next()){
            // Add mail to whitelist
            String sqlEmailAddWhitelist = "INSERT INTO iStoreWhitelist(email) VALUES(?)";
            PreparedStatement preparedMailAddWhiteListStatement = connection.prepareStatement(sqlEmailAddWhitelist);
            preparedMailAddWhiteListStatement.setString(1, newEmail);
            preparedMailAddWhiteListStatement.execute();
        }

        return check;
    }

    public void UpdatePassword(){


    }

    public int GetAccountIdFromMail(){
        int id = 0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");

            String sqlGetId = "SELECT id FROM iStoreUsers WHERE email = ?";
            PreparedStatement preparedGetIdStatement = connection.prepareStatement(sqlGetId);
            preparedGetIdStatement.setString(1, this._email);
            ResultSet resultId = preparedGetIdStatement.executeQuery();
            preparedGetIdStatement.close();
            id = resultId.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;

    }
}
