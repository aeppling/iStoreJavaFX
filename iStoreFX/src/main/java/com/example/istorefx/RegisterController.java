package com.example.istorefx;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class RegisterController {
    private String _email = null;
    private String _pseudo = null;
    private int _id = -1;
    @FXML
    private Button backButton;
    @FXML
    private TextField inputPseudo;
    @FXML
    private PasswordField inputPassword;
    @FXML
    private PasswordField inputPassword2;
    @FXML
    private TextField inputEmail;
    @FXML
    private TextField inputEmail2;
    @FXML
    private Button formButton;
    @FXML
    private Label pseudoError;
    @FXML
    private Label emailError;
    @FXML
    private Label email2Error;
    @FXML
    private Label passwordError;
    @FXML
    private Label password2Error;

    @FXML
    private ImageView _logoHeader;

    public void initImage() {
        Image image = new Image(getClass().getResourceAsStream("logo-no-background.png"));
        this._logoHeader.setImage(image);
        this._logoHeader.setFitWidth(140);
        this._logoHeader.setFitHeight(140);
    }

    public void initialize() {
        initImage();
    }
    public void requestWhitelist(Connection connection) {
        PreparedStatement preparedRegisterStatementWhitelist = null;
        try {
            String sqlRegister = "INSERT INTO iStoreWhitelistRequest(email) VALUES(?)";
            preparedRegisterStatementWhitelist = connection.prepareStatement(sqlRegister);
            preparedRegisterStatementWhitelist.setString(1, this.inputEmail.getText());
            preparedRegisterStatementWhitelist.execute();
            preparedRegisterStatementWhitelist.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isWhitelisted(String email, Connection connection) throws SQLException {
        boolean check = false;

        String sqlMailRequest = "SELECT * FROM iStoreWhitelist WHERE email LIKE ?";
        PreparedStatement preparedMailStatement = connection.prepareStatement(sqlMailRequest);
        preparedMailStatement.setString(1, this.inputEmail.getText());
        ResultSet resultEmail = preparedMailStatement.executeQuery();
        if (resultEmail.next()) {
            check = true;
        }
        preparedMailStatement.close();
        return (check);
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
    public boolean checkValidFields() {
        boolean check = true;

        if(inputPseudo.getText().length() <= 3) {
            this.pseudoError.setText("Nickname need to be at least 3 character length");
            check = false;
        }
        else if (check != false)
            this.pseudoError.setText("");
        if (!(inputEmail.getText().equals(inputEmail2.getText()))) {
            this.email2Error.setText("Emails are not matching !");
            check = false;
        }
        else if (check != false)
            this.email2Error.setText("");
        if (inputPassword.getText().length() < 8) {
            this.passwordError.setText("Password need to be at least 3 character length");
            check = false;
        }
        else if (check != false)
            this.passwordError.setText("");
        if (!(inputPassword.getText().matches("(.*[A-Z].*)" )))
        {
            this.passwordError.setText("Password need to contain at least 1 uppercase character");
            check = false;
        }
        else if (check != false)
            this.passwordError.setText("");
        if (!(inputPassword.getText().matches("(.*[a-z].*)" )))
        {
            this.passwordError.setText("Password need to contain at least 1 lowercase character");
            check = false;
        }
        else if (check != false)
            this.passwordError.setText("");
        if (!(inputPassword.getText().matches("(.*[0-9].*)" )))
        {
            this.passwordError.setText("Password need to contain at least 1 digit");
            check = false;
        }
        else if (check != false)
            this.passwordError.setText("");
        if (!(inputPassword.getText().matches("(.*[@,#,!,.,/,;,?,$,%].*$)" )))
        {
            this.passwordError.setText("Password need to contain at least 1 special character");
            check = false;
        }
        else if (check != false)
            this.passwordError.setText("");
        if (!(inputPassword.getText().equals(inputPassword2.getText()))) {
            this.password2Error.setText("Passwords are not matching !");
            check = false;
        }
        else if (check != false)
            this.password2Error.setText("");
        return (check);
    }
    public boolean checkPseudoExist(Connection connection) throws SQLException {
        boolean check = false;

        String sqlMailRequest = "SELECT * FROM iStoreUsers WHERE pseudo LIKE ?";
        PreparedStatement preparedMailStatement = connection.prepareStatement(sqlMailRequest);
        preparedMailStatement.setString(1,this.inputPseudo.getText());
        ResultSet resultPseudo = preparedMailStatement.executeQuery();
        if (resultPseudo.next()) {
            this.pseudoError.setText("An account is already registered with this nickname!");
            check = true;
        }
        else
            this.pseudoError.setText("");
        preparedMailStatement.close();
        return (check);
    }
    public boolean checkEmailOnWaitingList(Connection connection) throws SQLException {
        boolean check = false;

        String sqlWMailRequest = "SELECT * FROM iStoreWhitelistRequest WHERE email LIKE ?";
        PreparedStatement preparedWMailStatement = connection.prepareStatement(sqlWMailRequest);
        preparedWMailStatement.setString(1, this.inputEmail.getText());
        ResultSet resultEmail = preparedWMailStatement.executeQuery();
        if (resultEmail.next()) {
            this.emailError.setText("This email is already waiting for whitelisting");
            check = true;
        }
        else
            this.emailError.setText("");
        preparedWMailStatement.close();
        return (check);
    }

    public boolean checkEmailExist(Connection connection) throws SQLException {
        boolean check = false;

        String sqlMailRequest = "SELECT * FROM iStoreUsers WHERE email LIKE ?";
        PreparedStatement preparedMailStatement = connection.prepareStatement(sqlMailRequest);
        preparedMailStatement.setString(1, this.inputEmail.getText());
        ResultSet resultEmail = preparedMailStatement.executeQuery();
        if (resultEmail.next()) {
            this.emailError.setText("An account is already registered with this email");
            check = true;
        }
        else
            this.emailError.setText("");
        preparedMailStatement.close();
        return (check);
    }
    public boolean checkForm() {
        boolean check = true;

        if (this.inputPseudo.getText().isEmpty()) {
            this.pseudoError.setText("You need to choose a pseudo for your account");
            check = false;
        }
        else
            this.pseudoError.setText("");
        if (this.inputEmail.getText().isEmpty()) {
            this.emailError.setText("Please inform your mail");
            check = false;
        }
        else if (check != false)
            this.pseudoError.setText("");
        if (this.inputEmail2.getText().isEmpty()) {
            this.email2Error.setText("You need to confirm your email");
            check = false;
        }
        else if (check != false)
            this.email2Error.setText("");
        if (this.inputPassword.getText().isEmpty()) {
            this.passwordError.setText("Please choose a password");
            check = false;
        }
        else if (check != false)
            this.passwordError.setText("");
        if (this.inputPassword2.getText().isEmpty()) {
            this.password2Error.setText("You need to confirm your password");
            check = false;
        }
        else if (check != false)
            this.password2Error.setText("");
        if (check == false)
            return (false);
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            if (checkPseudoExist(connection) == true)
                check = false;
            else if (checkEmailExist(connection) == true)
                check = false;
            else if (checkEmailOnWaitingList(connection) == true)
                check = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (checkValidFields() == false)
            check = false;
        this._email = inputEmail.getText();
        this._pseudo = inputPseudo.getText();
        return (check);
    }

    public void submitForm() {
        if (this.checkForm() == false)
            return ;
        Connection connection = null;
        String role = null;
        PreparedStatement preparedRegisterStatement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlRegister = "INSERT INTO iStoreUsers(pseudo, email, psswd, role) VALUES (?,?,?,?)";
            preparedRegisterStatement = connection.prepareStatement(sqlRegister);
            preparedRegisterStatement.setString(1, this.inputPseudo.getText());
            preparedRegisterStatement.setString(2, this.inputEmail.getText());
            String hashed_password = HashPassword(this.inputPassword.getText());
            preparedRegisterStatement.setString(3, hashed_password);
            if(isFirstUser()){
                role = "admin";
            }else{
                role = "standart";
            }
            preparedRegisterStatement.setString(4, role);
            preparedRegisterStatement.execute();
            preparedRegisterStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
        try { //LAUNCH APP IF WHITELISTED, ELSE REQUEST WHITELIST AND GO TO LOGINPAGE W/ MSG
            if ((role.equals("standart"))
                    || (role.equals("employee"))) {
                if (isWhitelisted(this.inputEmail.getText(), connection) == true) {
                    User user = new User(this._pseudo, this._email);
                    Store(user);
                } else { // ELSE REQUEST WHITELIST
                    requestWhitelist(connection);
                    WhiteListValidation();
                }
            }else if(role.equals("admin")){
                User user = new User(this._pseudo, this._email);
                AdminDashboard(user);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isFirstUser() throws SQLException {
        // Check if its the first user
        boolean check;
        Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");

        String sqlRequest = "SELECT * FROM iStoreUsers";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);

        ResultSet resultEmail = preparedStatement.executeQuery();
        if (resultEmail.next()) {
            check = false;
        }
        else{
            check = true;
        }
        preparedStatement.close();
        connection.close();
        return check;

    }

    public void Store(User user) {
        Stage currentStage2 = (Stage) backButton.getScene().getWindow();
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
    public void WhiteListValidation() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        currentStage.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("WhiteListValidation.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 336, 144);
            primaryStage.setTitle("iStore - Whitelist requested");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    @FXML
    private Button _disconnectButton;
    public void Disconnect() {
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
    public void AdminDashboard(User user) {
        // Redirect to Admin Dashboard
        Stage currentStage2 = (Stage) _disconnectButton.getScene().getWindow();
        currentStage2.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AdminView.fxml"));
        try {
            SingletonUserHolder userHolder = SingletonUserHolder.getInstance();
            userHolder.setUser(user);
            Scene scene = new Scene(fxmlLoader.load(), 600.0, 620);
            primaryStage.setTitle("iStore");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
