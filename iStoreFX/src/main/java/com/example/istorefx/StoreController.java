package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javafx.geometry.HPos;
import javafx.geometry.VPos;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.lang.*;
import java.util.Objects;
import java.util.Optional;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class StoreController {
    private User _user;
    ArrayList<StoreRecord> _storeList;

    @FXML
    private ImageView _profileIcon;
    @FXML
    private ScrollBar _scrollBar;

    @FXML
    private GridPane _gridPane;
    @FXML
    private Label _emailLabel;
    @FXML
    private Label _pseudoLabel;
    @FXML
    private Button _homeButton;
    @FXML
    private Button _allstoresButton;
    @FXML
    private ImageView _logoHeader;
    @FXML
    private Pane      _logoHeaderPane;
    @FXML
    private Button      _accountButton;
    @FXML
    private Button _adminDashboardButton;

    public void getStores() {
        Connection connection = null;
        ResultSet resultStore = null;
        this._storeList = new ArrayList<>();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlStoreRequest = "SELECT * FROM iStoreStores";
            PreparedStatement preparedStoreStatement = connection.prepareStatement(sqlStoreRequest);
            resultStore = preparedStoreStatement.executeQuery();
            while (resultStore.next()) {
                StoreRecord storeRecord = new StoreRecord(resultStore.getString("name"), resultStore.getInt("id"), resultStore.getString("store_img"));
                this._storeList.add(storeRecord);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
    public void displayProfile() {
        Image image = new Image(getClass().getResourceAsStream("profile-icon.png"));

        // ADD PROFIL ICON
        this._profileIcon.setImage(image);
        this._profileIcon.setPickOnBounds(true); // allows click on transparent areas
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
    public void initButtons(){
        //ALL STORES BTN
        Image image = new Image(getClass().getResourceAsStream("allstores-icon.png"));
        ImageView img = new ImageView();
        img.setImage(image);
        img.setFitWidth(55);
        img.setFitHeight(55);
        this._allstoresButton.setGraphic(img);
        //HOME BTN
        Image image3 = new Image(getClass().getResourceAsStream("home-icon.png"));
        ImageView img3 = new ImageView();
        img3.setImage(image3);
        img3.setFitWidth(60);
        img3.setFitHeight(60);
        this._homeButton.setGraphic(img3);
        // Admin Dashboard BTN


        if(this._user.getRole().equals("admin")){
            this._adminDashboardButton.setVisible(true);

        }else{
            this._adminDashboardButton.setVisible(false);
        }

    }
    public void initialize() throws SQLException {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._user = holder.getUser();
        getStores();
        this._scrollBar.setMin(0);
        this._scrollBar.setMax(800);
        this._scrollBar.setValue(400);
        this._scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            this._gridPane.setTranslateX(newValue.doubleValue());
          //  this._test.setLayoutX(newValue.doubleValue());
        });
        initImage();
        initButtons();
        displayProfile();
        displayStores();
    }

    public void displayStores() {
        int col_count = 0;
        int row_count = 0;
        int tot_count = 0;

        while ((tot_count < this._storeList.size()) && (tot_count < 24)) {
            if (col_count > 7) {
                col_count = 0;
                row_count++;
            }
            if (row_count > 2)
                break;
            createStoreWindow(col_count, row_count, this._storeList.get(tot_count));
            col_count++;
            tot_count++;
        }
    }
    public void createStoreWindow(int x, int y, StoreRecord storeRecord) {
        //IMAGE CREATING
        Button button = new Button(" " + storeRecord.getName() + " ");
        Image image = new Image(getClass().getResourceAsStream("icons8-department-shop-64.png"));
        ImageView img = new ImageView();

        // ADD STORE IMAGE
        img.setImage(image);
        img.setPickOnBounds(true);
        img.setOnMouseClicked(e -> enterStore(storeRecord));
        img.setFitWidth(80);
        img.setFitHeight(80);
        this._gridPane.setHalignment(img, HPos.CENTER);
        this._gridPane.setValignment(img, VPos.TOP);
        this._gridPane.setMargin(button, new Insets(0, 0, 0, 0));
        this._gridPane.add(img, x, y);
        // ADD STORE BUTTON
        button.setStyle("-fx-font-weight: bold; -fx-border-width: 0.3em; -fx-text-fill: #4F4F4F; -fx-background-color: #E7ECEF; -fx-background-radius: 3em; -fx-border-color: #24D3FF28; -fx-border-radius: 3em");
        button.setOnAction(e -> enterStore(storeRecord));
        this._gridPane.setMargin(button, new Insets(0, 0, 20, 0));
        this._gridPane.setHalignment(button, HPos.CENTER);
        this._gridPane.setValignment(button, VPos.BOTTOM);
        this._gridPane.add(button, x, y);

    }
    public void enterStore(StoreRecord store) {
            Stage currentStage = (Stage) _allstoresButton.getScene().getWindow();
            currentStage.close();
            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("shop.fxml"));
            try {
                SingletonUserHolder userHolder = SingletonUserHolder.getInstance();
                userHolder.setUser(this._user);
                SingletonStoreHolder storeHolder = SingletonStoreHolder.getInstance();
                storeHolder.setStore(store);
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
        Stage currentStage = (Stage) _allstoresButton.getScene().getWindow();
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

    public void Account() {
        Stage currentStage = (Stage) _allstoresButton.getScene().getWindow();
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

    @FXML
    private Button _disconnectButton;
    public void Disconnect() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
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
    public void AdminDashboard() {
        // Redirect to Admin Dashboard
        Stage currentStage2 = (Stage) _disconnectButton.getScene().getWindow();
        currentStage2.close();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AdminView.fxml"));
        try {
            SingletonUserHolder userHolder = SingletonUserHolder.getInstance();
            userHolder.setUser(this._user);
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
