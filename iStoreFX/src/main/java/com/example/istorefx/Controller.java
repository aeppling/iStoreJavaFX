package com.example.istorefx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class Controller {
    @FXML
    private Button          registerButton;
    @FXML
    private Button          loginButton;
    @FXML
    private TextField       pseudoLogin;
    @FXML
    private PasswordField   passwordLogin;

    @FXML
    private Label           emailError;
    @FXML
    private Label           passwordError;
    private String          _email;
    private String          _password;

    public String HashPassword(String hashed_password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(hashed_password.getBytes());
        byte[] digest = md.digest();
        StringBuffer buffer = new StringBuffer();
        for (byte b : digest) {
            buffer.append(String.format("%02x", b & 0xff));
        }
        return buffer.toString();
    }
    public boolean checkPasswordMatch(ResultSet resultPassword, String password) throws SQLException {
        boolean check = false;

        while (resultPassword.next()) {
            System.out.println("LOOP");
            System.out.println(resultPassword.getString("email"));
            System.out.println(this._email);
            if (resultPassword.getString("email").equals(this._email)) {
                System.out.println("HERE");
                System.out.println(resultPassword.getString("psswd"));
                System.out.println(password);
                if (resultPassword.getString("psswd").equals(password)) {
                    // IS VALIDa
                    User user = new User(resultPassword.getString("pseudo"), this._email, resultPassword.getInt("id"));
                    Store(user);
                    System.out.println("USER LOGGED!");
                }
            }
        }
        return (check);
    }
    public boolean checkPasswordValid(Connection connection) throws SQLException {
        boolean check = false;

        if (this._password.isEmpty()) {
            this.passwordError.setText("Please enter your password.");
            return (false);
        }
        String sqlPasswordRequest = "SELECT * FROM iStoreUsers WHERE email LIKE ?";
        PreparedStatement preparedPasswordStatement = connection.prepareStatement(sqlPasswordRequest);
        preparedPasswordStatement.setString(1, this._email);
        System.out.println("After S: " + preparedPasswordStatement.toString());
        ResultSet resultPassword = preparedPasswordStatement.executeQuery();
            try {
                String hashed_password = HashPassword(this._password);
                if (checkPasswordMatch(resultPassword, hashed_password) == true) {
                    this.passwordError.setText("");
                    check = true;
                }
                else {
                    check = false;
                }
                //check password
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            this.passwordError.setText("");
        preparedPasswordStatement.close();
        return (check);
    }
    public boolean checkEmailExist(Connection connection) throws SQLException {
        boolean check = false;

        if (this._email.isEmpty()) {
            this.emailError.setText("Please enter your email.");
            return (false);
        }
        String sqlEmailRequest = "SELECT * FROM iStoreUsers WHERE email LIKE ?";
        PreparedStatement preparedMailStatement = connection.prepareStatement(sqlEmailRequest);
        preparedMailStatement.setString(1, this._email);
        System.out.println("After : " + preparedMailStatement.toString());
        ResultSet resultEmail = preparedMailStatement.executeQuery();
        if (resultEmail.next()) {
            this.emailError.setText("");
            check = true;
        }
        else
            this.emailError.setText("No account has been found.");
        preparedMailStatement.close();
        return (check);
    }
    public void login(ActionEvent event) {
        boolean is_good = false;
        System.out.println("LOGIN");
        //CHECK LOGIN IN DDB AND CONNECT
        this._email = this.pseudoLogin.getText();
        this._password = this.passwordLogin.getText();
        System.out.println(this._email);
        System.out.println(this._password);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            is_good = checkEmailExist(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (is_good != true)
            return ;
        try {
            is_good = checkPasswordValid(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (is_good == true)
            System.out.println("CONNECT USER");
    }

    public void register(ActionEvent event) {
        Stage currentStage = (Stage) registerButton.getScene().getWindow();
        currentStage.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 400, 600);
            primaryStage.setTitle("iStore - Register an account");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Store(User user) {
        Stage currentStage2 = (Stage) loginButton.getScene().getWindow();
        currentStage2.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("store.fxml"));
        try {
            SingletonUserHolder userHolder = SingletonUserHolder.getInstance();
            userHolder.setUser(user);
            Scene scene = new Scene(fxmlLoader.load(), 875, 616);
            primaryStage.setTitle("iStore");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
