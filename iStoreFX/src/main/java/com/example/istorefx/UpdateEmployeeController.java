package com.example.istorefx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UpdateEmployeeController {

    private User _admin;
    private String _email;
    private String _nickname;
    private int _id;
    private String _role;
    private String password;
    @FXML
    private ChoiceBox<String> _roleChoice;
    @FXML
    private Label _nicknameLabel;
    @FXML
    private Button _backButton;
    @FXML
    private Label _emailLabel;
    @FXML
    private AnchorPane _updateEmailPane;
    @FXML
    private AnchorPane _updatePasswordPane;
    @FXML
    private AnchorPane _updateNicknamePane;
    @FXML
    private AnchorPane _updatePane;
    @FXML
    private TextField _newEmailTextField;
    @FXML
    private TextField _newNicknameTextField;
    @FXML
    private TextField _newPasswordTextField;
    @FXML
    private TextField _newPasswordConfirmTextField;
    @FXML
    private Label _newPasswordError;
    @FXML
    private Label _newPasswordConfirmError;
    @FXML
    private Label _newEmailError;
    @FXML
    private Label _newNicknameError;

    public void initialize() {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._admin = holder.getUser();
        SingletonEmailHolder emailHolder = SingletonEmailHolder.getInstance();
        this._email = emailHolder.getEmail();
        this._id = GetAccountIdFromMail();
        this._role = GetAccountRoleFromID();
        this._nickname = GetAccountUsernameFromID();
        this._nicknameLabel.setText(this._nickname);
        this._emailLabel.setText(this._email);
        this._roleChoice = new ChoiceBox<String>();
    }

    public void DisplayRoleChoiceBox(){
        String role1, role2;
        if(this._role.equals("employee")){
            role1 = "standart";

        }else{
            role1 = "employee";
        }
        role2 = "admin";
        this._roleChoice.setItems(FXCollections.observableArrayList(this._role, role1, role2));

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
            connection.close();
            Back();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void DisplayUpdateNicknamePane(){
        this._updateNicknamePane.setVisible(true);
    }
    public void CloseUpdateNicknamePane(){
        this._updatePasswordPane.setVisible(false);
    }
    public void DisplayUpdatePasswordPane(){
        this._updatePasswordPane.setVisible(true);
    }
    public void CloseUpdatePasswordPane(){
        this._updatePasswordPane.setVisible(false);
    }
    public void DisplayUpdateEmailPane(){
        this._updateEmailPane.setVisible(true);
    }
    public void CloseUpdateEmailPane(){
        this._updateEmailPane.setVisible(false);
    }
    public void UpdateNickname() throws SQLException {
        String newNickname = this._newNicknameTextField.getText();
        Connection connection = null;
        boolean is_good = false;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            is_good = CheckErrorFromUpdateNickname(connection, newNickname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!is_good){
            return;
        }else{
            // Update DB
            try {
                String sqlUpdateNickname = "UPDATE `iStoreUsers` SET `pseudo`= ?, WHERE id = ?";
                PreparedStatement preparedNicknameUpdateStatement = connection.prepareStatement(sqlUpdateNickname);
                preparedNicknameUpdateStatement.setString(1, newNickname);
                preparedNicknameUpdateStatement.setInt(2, this._id);
                preparedNicknameUpdateStatement.execute();
                this._nickname = newNickname;
                this._nicknameLabel.setText(this._nickname);
                CloseUpdateNicknamePane();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connection.close();
    }
    private boolean CheckErrorFromUpdateNickname(Connection connection, String newNickname) {
        boolean check = true;
        this._newNicknameError.setText("");
        if (newNickname.isEmpty()) {
            this._newNicknameError.setText("Please enter a username.");
            check = false;
        }else{
            if(newNickname.length() <= 3) {
                this._newNicknameError.setText("Username need to be at least 3 character length");
                check = false;
            }else{
                if(this._nickname.equals(newNickname)){
                    this._newNicknameError.setText("You have to enter a new nickname.");
                    check = false;
                }
            }
        }
        return check;
    }
    public void UpdateMail() throws SQLException {
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
                this._email = newEmail;
                this._emailLabel.setText(this._email);
                CloseUpdateEmailPane();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connection.close();
    }
    public boolean CheckErrorFromUpdateMail(Connection connection, String newEmail)throws SQLException{
        this._newEmailError.setText("");
        boolean check = true;
        if (newEmail.isEmpty()) {
            this._newEmailError.setText("Please enter a mail.");
            check = false;
        }else{
            // Check if mail is already used
            String sqlEmailRequest = "SELECT * FROM iStoreUser WHERE email = ? AND id != ?";
            PreparedStatement preparedMailStatement = connection.prepareStatement(sqlEmailRequest);
            preparedMailStatement.setString(1, newEmail);
            preparedMailStatement.setInt(2, this._id);
            ResultSet resultMail = preparedMailStatement.executeQuery();
            if (resultMail.next()) {
                this._newEmailError.setText("An account is already registered with this email.");
                check = false;
            }
            if(this._email.equals(newEmail)){
                this._newEmailError.setText("You have to enter a new email.");
                check = false;
            }
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
        connection.close();
        return check;
    }

    public void UpdatePassword() throws NoSuchAlgorithmException {
        String newPassword = this._newPasswordTextField.getText();
        String newPasswordHash = HashPassword(newPassword);
        Connection connection = null;
        boolean is_good = false;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            is_good = CheckErrorFromUpdatePassword(connection, newPassword, newPasswordHash);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!is_good){
            return;
        }else{
            // Update DB
            try {
                String sqlUpdateEmail = "UPDATE `iStoreUsers` SET `psswd`=?, WHERE id = ?";
                PreparedStatement preparedMailUpdateStatement = connection.prepareStatement(sqlUpdateEmail);
                preparedMailUpdateStatement.setString(1, newPasswordHash);
                preparedMailUpdateStatement.setInt(2, this._id);
                preparedMailUpdateStatement.execute();
                CloseUpdateNicknamePane();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    private boolean CheckErrorFromUpdatePassword(Connection connection, String newPassword, String newPasswordHash) throws SQLException{
        this._newPasswordError.setText("");
        this._newPasswordConfirmError.setText("");
        String newPasswordConfirm = this._newPasswordConfirmTextField.getText();
        boolean check = true;
        if (newPassword.isEmpty()) {
            this._newPasswordError.setText("Please enter a password.");
            check = false;
        }else {
            if (newPasswordConfirm.isEmpty()) {
                this._newPasswordConfirmError.setText("You need to confirm your password.");
                check = false;
            } else {
                if (!newPassword.equals(newPasswordConfirm)) {
                    this._newPasswordConfirmError.setText("Passwords are not matching !");
                }else{
                    if (newPassword.length() < 8) {
                        this._newPasswordError.setText("Password need to be at least 3 character length");
                        check = false;
                    }
                    if (!(newPassword.matches("(.*[A-Z].*)" )))
                    {
                        this._newPasswordError.setText("Password need to contain at least 1 uppercase character");
                        check = false;
                    }
                    if (!(newPassword.matches("(.*[a-z].*)" )))
                    {
                        this._newPasswordError.setText("Password need to contain at least 1 lowercase character");
                        check = false;
                    }
                    if (!(newPassword.matches("(.*[0-9].*)" )))
                    {
                        this._newPasswordError.setText("Password need to contain at least 1 digit");
                        check = false;
                    }
                    if (!(newPassword.matches("(.*[@,#,!,.,/,;,?,$,%].*$)" )))
                    {
                        this._newPasswordError.setText("Password need to contain at least 1 special character");
                        check = false;
                    }
                }
            }
        }
        String sqlPasswordRequest = "SELECT psswd FROM iStoreUser WHERE email = ?";
        PreparedStatement preparedPasswordStatement = connection.prepareStatement(sqlPasswordRequest);
        preparedPasswordStatement.setString(1, newPassword);
        ResultSet resultPassword = preparedPasswordStatement.executeQuery();
        if(newPasswordHash.equals(resultPassword.getString("psswd"))) {
            this._newPasswordError.setText("You have to enter a new password.");
            check = false;
        }
        connection.close();
        return check;

    }

    public int GetAccountIdFromMail(){
        int id = 0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlGetId = "SELECT id FROM iStoreUsers WHERE email = ?";
            PreparedStatement preparedGetIdStatement = connection.prepareStatement(sqlGetId);
            preparedGetIdStatement.setString(1, this._email);
            ResultSet resultId = preparedGetIdStatement.executeQuery();

            id = resultId.getInt("id");
            preparedGetIdStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }
    private String GetAccountUsernameFromID() {
        String username = null;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlGetUsername = "SELECT pseudo FROM iStoreUsers WHERE id = ?";
            PreparedStatement preparedGetUsernameStatement = connection.prepareStatement(sqlGetUsername);
            preparedGetUsernameStatement.setInt(1, this._id);
            ResultSet resultUsername = preparedGetUsernameStatement.executeQuery();
            username = resultUsername.getString("pseudo");
            preparedGetUsernameStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    private String GetAccountRoleFromID() {
        String role = null;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlGetRole = "SELECT role FROM iStoreUsers WHERE id = ?";
            PreparedStatement preparedGetRoleStatement = connection.prepareStatement(sqlGetRole);
            preparedGetRoleStatement.setInt(1, this._id);
            ResultSet resultUsername = preparedGetRoleStatement.executeQuery();
            role = resultUsername.getString("pseudo");
            preparedGetRoleStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }
    public String HashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        StringBuffer buffer = new StringBuffer();
        for (byte b : digest) {
            buffer.append(String.format("%02x", b & 0xff));
        }
        return buffer.toString();
    }
    public void Back() {
        Stage currentStage = (Stage) _backButton.getScene().getWindow();
        currentStage.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AdminView.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600.0, 620);
            primaryStage.setTitle("iStore - Admin Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
