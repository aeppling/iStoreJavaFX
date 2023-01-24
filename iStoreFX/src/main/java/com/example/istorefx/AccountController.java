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
    public void changePassword() {
        TextInputDialog pop_up = new TextInputDialog("Eeeeee");
        pop_up.setTitle("iStores -  Password Changing");
        pop_up.setHeaderText("Changing password");
        pop_up.setContentText("Enter your current password");
        pop_up.show();
        Optional<String> result = pop_up.showAndWait();
        String  input_psswd = null;
        if (result.isPresent()) {
            input_psswd = result.get();
        }
    }

    public void changeEmail() {

    }

    public void changePseudo() {

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
