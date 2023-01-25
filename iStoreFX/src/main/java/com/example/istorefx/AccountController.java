package com.example.istorefx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Optional;

public class AccountController {

    @FXML
    private Button      _accountButton;
    @FXML
    private Button      _allStoresButton;
    @FXML
    private Button      _homeButton;
    @FXML
    private Button      _categoriesButton;
    @FXML
    private ImageView   _logoHeader;
    private User        _user;

    @FXML
    private ImageView       _profileIcon;
    @FXML
    private javafx.scene.control.Label _pseudoLabel;
    @FXML
    private Label _emailLabel;

    @FXML
    private Label _pseudoInfo;
    @FXML
    private Label _emailInfo;
    public String cutProfileString(String input) {
        String output;
        if (input.length() > 20) {
            output = input.substring(0, Math.min(input.length(), 20));
            output = output + "...";
        }
        else
            output = input;
        return (output);
    }

    @FXML
    private Button _changePassword;
    @FXML
    private Button _changeEmail;
    @FXML
    private Button _changePseudo;
    @FXML
    private Button _deleteAccount;



    public void updateEmail(String new_email) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlUpdateRequest = "UPDATE iStoreUsers SET email = ? WHERE id = ?";
            PreparedStatement preparedUpdateStatement = connection.prepareStatement(sqlUpdateRequest);
            preparedUpdateStatement.setString(1, new_email);
            preparedUpdateStatement.setInt(2, this._user.getId());
            preparedUpdateStatement.executeUpdate();
            successChange("Email");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void successChange(String mode) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(mode + " changed");
        alert.setHeaderText(mode + " changed");
        alert.setContentText("Your " + mode + " has been successfully changed");
        alert.showAndWait();
    }
    public void updatePassword(String new_password) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlUpdateRequest = "UPDATE iStoreUsers SET psswd = ? WHERE id = ?";
            PreparedStatement preparedUpdateStatement = connection.prepareStatement(sqlUpdateRequest);
            preparedUpdateStatement.setString(1, new_password);
            preparedUpdateStatement.setInt(2, this._user.getId());
            connection.close();
            successChange("Password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setNewPassword() {
        PasswordField newPsswd = new PasswordField();
        PasswordField newPsswdConfirm = new PasswordField();
        TextInputDialog pop_up = new TextInputDialog();
        pop_up.setTitle("iStores -  Password Changing");
        pop_up.setHeaderText("New password confirmation");
        pop_up.setContentText("New password :");
        pop_up.getDialogPane().setContent(newPsswd);
        Optional<String> result = pop_up.showAndWait();
        pop_up.show();
        String new_p = new String();
        String confirm_p = new String();
        if (result.isPresent()) {
            new_p = newPsswd.getText();
            TextInputDialog pop_upConf = new TextInputDialog();
            pop_upConf.setTitle("iStores -  Password Changing");
            pop_upConf.setHeaderText("New password confirmation");
            pop_upConf.setContentText("New password :");
            pop_upConf.getDialogPane().setContent(newPsswdConfirm);
            Optional<String> resultConfirm = pop_upConf.showAndWait();
            if (resultConfirm.isPresent()) {
                confirm_p = newPsswdConfirm.getText();
                System.out.println(new_p);
                System.out.println(confirm_p);
                try {
                    if (new_p.equals(confirm_p)) {
                        updatePassword(HashPassword(new_p));
                    }
                    else {
                        errorPasswordMatch();
                        return ;
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void errorPasswordMatch() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Password changing");
        alert.setHeaderText("Not matching");
        alert.setContentText("Try again...");
        alert.showAndWait();
    }
    public void errorPassword() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Password changing");
        alert.setHeaderText("Wrong password");
        alert.setContentText("Try again...");
        alert.showAndWait();
    }
    public boolean checkPasswordMatch(String password) throws SQLException {
        boolean check = false;

        Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
        String sqlPasswordRequest = "SELECT * FROM iStoreUsers WHERE id LIKE ?";
        PreparedStatement preparedPasswordStatement = connection.prepareStatement(sqlPasswordRequest);
        preparedPasswordStatement.setInt(1, this._user.getId());
        ResultSet resultPassword = preparedPasswordStatement.executeQuery();
        while (resultPassword.next()) {
            if (resultPassword.getInt("id") == this._user.getId()) {
                if (resultPassword.getString("psswd").equals(password)) {
                    System.out.println("MATCH!");
                    setNewPassword();
                }
                else {
                    errorPassword();
                    return (false);
                }
            }
        }
        connection.close();
        return (check);
    }

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
    public void changePassword() {
        PasswordField psswd = new PasswordField();
        TextInputDialog pop_up = new TextInputDialog();
        pop_up.setTitle("iStores -  Password Changing");
        pop_up.setHeaderText("Current password");
        pop_up.setContentText("Enter current password :");
        pop_up.getDialogPane().setContent(psswd);
        Optional<String> result = pop_up.showAndWait();
        String input_psswd = null;
        if (result.isPresent()) {
            try {
                input_psswd = psswd.getText();
                checkPasswordMatch(HashPassword(input_psswd));
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e2) {
                e2.printStackTrace();
            }
        }
    }

    public boolean checkEmailExist(Connection connection, String new_email) throws SQLException {
        boolean check = false;

        String sqlEmailRequest = "SELECT * FROM iStoreUsers";
        PreparedStatement preparedMailStatement = connection.prepareStatement(sqlEmailRequest);
        ResultSet resultEmail = preparedMailStatement.executeQuery();
        while (resultEmail.next()) {
            if (resultEmail.getString("email").equals(new_email)) {
                check = true;
                break;
            }
        }
        preparedMailStatement.close();
        return (check);
    }

    public void errorEmailExist() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Email changing");
        alert.setHeaderText("Email already used");
        alert.setContentText("Can't set-up this email as new email");
        alert.showAndWait();
    }

    public void errorEmailMatch() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Email changing");
        alert.setHeaderText("Emails are not matching");
        alert.setContentText("Try again...");
        alert.showAndWait();
    }

    public void errorPseudoMatch() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Pseudo changing");
        alert.setHeaderText("Pseudo are not matching");
        alert.setContentText("Try again...");
        alert.showAndWait();
    }
    public void changeEmail() {


        TextInputDialog pop_up2 = new TextInputDialog();
        pop_up2.setTitle("iStores - Email Changing");
        pop_up2.setHeaderText("Email change");
        pop_up2.setContentText("Enter wanted email :");
        Optional<String> result2 = pop_up2.showAndWait();
        if (result2.isPresent()) {
            TextInputDialog pop_up = new TextInputDialog();
            pop_up.setTitle("iStores - Email Changing");
            pop_up.setHeaderText("Email change");
            pop_up.setContentText("Confirm wanted email :");
            Optional<String> result = pop_up.showAndWait();
            if (result.isPresent()) {
                if (result2.get().equals(result.get())) {
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
                        if (checkEmailExist(connection, result.get()) == false) {
                            updateEmail(result.get());
                        } else {
                            errorEmailExist();
                        }
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    errorEmailMatch();
                }
            }
        }
    }

    public void updatePseudo(String new_pseudo) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlUpdateRequest = "UPDATE iStoreUsers SET pseudo = ? WHERE id = ?";
            PreparedStatement preparedUpdateStatement = connection.prepareStatement(sqlUpdateRequest);
            preparedUpdateStatement.setString(1, new_pseudo);
            preparedUpdateStatement.setInt(2, this._user.getId());
            connection.close();
            successChange("Pseudo");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void changePseudo() {
        TextInputDialog pop_up = new TextInputDialog();
        pop_up.setTitle("iStores - Pseudo Changing");
        pop_up.setHeaderText("Pseudo change");
        pop_up.setContentText("Enter new pseudo :");
        Optional<String> result = pop_up.showAndWait();
        if (result.isPresent()) {
            String new_pseudo = result.get();
            TextInputDialog pop_up_conf = new TextInputDialog();
            pop_up_conf.setTitle("iStores - Pseudo Changing");
            pop_up_conf.setHeaderText("New pseudo confirm");
            pop_up_conf.setContentText("Confirm new pseudo :");
            Optional<String> result_conf = pop_up_conf.showAndWait();
            if (result_conf.isPresent()) {
                String conf_pseudo = result_conf.get();
                System.out.println("new : " + new_pseudo);
                System.out.println("conf : " + conf_pseudo);
                if (new_pseudo.equals(conf_pseudo)) {
                    updatePseudo(new_pseudo);
                } else {
                    errorPseudoMatch();
                }
            }
        }
    }

    public void deleteAccount() {
        /*Button yesButton = (Button) alert.getDialogPane().lookupButton( ButtonType.YES );
        yesButton.setDefaultButton( false );

        //Activate Defaultbehavior for no-Button:
        Button noButton = (Button) alert.getDialogPane().lookupButton( ButtonType.NO );
        noButton.setDefaultButton( true );
        */

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("iStore - Account deleting");
        alert.setHeaderText("You're going to delete your account");
        alert.setContentText("Do you want to continue ?");
        alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        // ZDZDQZDZ
        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setDefaultButton(false);
        Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        noButton.setDefaultButton(true);
        //DZDZDDZ
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.YES) {
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setTitle("iStore - Adios");
            alert2.setHeaderText("Account deletion");
            alert2.setContentText("Confirm to delete account");
            alert2.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            alert2.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
            alert2.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            yesButton.setDefaultButton(false);
            noButton.setDefaultButton(true);
            Optional<ButtonType> action2 = alert2.showAndWait();
            if (action2.get() == ButtonType.YES) {
                System.out.println("DELETE");
                String deleteRequest = new String("DELETE FROM iStoreUsers WHERE id = ?");
                String deleteRequest2 = new String("DELETE FROM StoreUserLink WHERE UserID = ?");
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
                    PreparedStatement preparedDelete2Request = connection.prepareStatement(deleteRequest2);
                    preparedDelete2Request.setInt(1, this._user.getId());
                    preparedDelete2Request.execute();
                    PreparedStatement preparedDeleteRequest = connection.prepareStatement(deleteRequest);
                    preparedDeleteRequest.setInt(1, this._user.getId());
                    preparedDeleteRequest.execute();
                    connection.close();
                    Platform.exit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        //DELETE FROM table_name WHERE condition;
    }
    public void displayProfile() {
        Image image = new Image(getClass().getResourceAsStream("profile-icon.png"));

        // ADD PROFIL ICON
        this._profileIcon.setImage(image);
        this._profileIcon.setPickOnBounds(true); // allows click on transparent areas
        this._profileIcon.setOnMouseClicked(e -> System.out.println("Clicked profile : " + this._user.getPseudo()));
        this._profileIcon.setFitWidth(40);
        this._profileIcon.setFitWidth(40);
        // ADD PROFIL INFO
        this._pseudoLabel.setText(cutProfileString(this._user.getPseudo()));
        this._emailLabel.setText(cutProfileString(this._user.getEmail()));
    }

    public void initImage() {
        Image image = new Image(getClass().getResourceAsStream("logo-no-background.png"));
        this._logoHeader.setImage(image);
        this._logoHeader.setFitWidth(170);
        this._logoHeader.setFitHeight(170);
    }
    public void initButtons() {
        //ALL STORES BTN
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("allstores-icon.png"));
        ImageView img = new ImageView();
        img.setImage(image);
        img.setFitWidth(55);
        img.setFitHeight(55);
        this._allStoresButton.setGraphic(img);
        //CATEGORIES BTN
        javafx.scene.image.Image image2 = new javafx.scene.image.Image(getClass().getResourceAsStream("categories-icon.png"));
        ImageView img2 = new ImageView();
        img2.setImage(image2);
        img2.setFitWidth(50);
        img2.setFitHeight(55);
        this._categoriesButton.setGraphic(img2);
        //HOME BTN
        javafx.scene.image.Image image3 = new Image(getClass().getResourceAsStream("home-icon.png"));
        ImageView img3 = new ImageView();
        img3.setImage(image3);
        img3.setFitWidth(60);
        img3.setFitHeight(60);
        this._homeButton.setGraphic(img3);
    }
    public void initialize() {
        SingletonUserHolder holder2 = SingletonUserHolder.getInstance();
        this._user = holder2.getUser();
        initButtons();
        initImage();
        displayProfile();
        this._pseudoInfo.setText(this._user.getPseudo());
        this._emailInfo.setText(this._user.getEmail());
    }
    public void Home() {
        Stage currentStage2 = (Stage) this._homeButton.getScene().getWindow();
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
    public void Categories() {
        Stage currentStage2 = (Stage) this._homeButton.getScene().getWindow();
        currentStage2.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Categories.fxml"));
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
    public void AllStores() {
        Stage currentStage = (Stage) _homeButton.getScene().getWindow();
        currentStage.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AllStores.fxml"));
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

    @FXML
    private Button AllUsersButton;
    public void AllUsers() {
        Stage currentStage = (Stage) _homeButton.getScene().getWindow();
        currentStage.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AllUsers.fxml"));
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
    public void Account() {
        Stage currentStage = (Stage) this._allStoresButton.getScene().getWindow();
        currentStage.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Account.fxml"));
        try {
            SingletonUserHolder userHolder = SingletonUserHolder.getInstance();
            userHolder.setUser(this._user);
            Scene scene = new Scene(fxmlLoader.load(), 910, 616);
            primaryStage.setTitle("iStore - Account");
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
            Stage currentStage = (Stage) _homeButton.getScene().getWindow();
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
