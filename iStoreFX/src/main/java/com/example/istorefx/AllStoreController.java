package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class AllStoreController {

    private ArrayList<StoreRecord> _storeList;
    private User        _user;
    @FXML
    private Button      _homeButton;
    @FXML
    private GridPane    _gridPane;
    @FXML
    private TextField   _searchBar;

    private GridPane    _baseGrid;

    @FXML
    private Button          _allstoresButton;
    @FXML
    private ImageView       _logoHeader;
    @FXML
    private ImageView       _profileIcon;
    @FXML
    private Label           _pseudoLabel;
    @FXML
    private Label           _emailLabel;
    @FXML
    private Button      _accountButton;
    @FXML
    private Button _adminDashboardButton;

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
    public void initButtons() throws SQLException {
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
        String sqlRoleRequest = "SELECT role FROM iStoreUsers WHERE id = ?";
        Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
        PreparedStatement preparedRoleStatement = connection.prepareStatement(sqlRoleRequest);
        preparedRoleStatement.setInt(1, this._user.getId());
        ResultSet resultRole = preparedRoleStatement.executeQuery();
        resultRole.next();
        if(this._user.getRole().equals("admin")){
            this._adminDashboardButton.setVisible(true);

        }else{
            this._adminDashboardButton.setVisible(false);
        }
        preparedRoleStatement.close();
        connection.close();

    }

    public void displayStore() {
        int i = 6;
        int col_count = 0;
        int row_count = 3;

        this._gridPane.getChildren().clear();
        while (i - 6 < this._storeList.size()) {
            int occurence = (this._storeList.get(i - 6).getName().toLowerCase().split(this._searchBar.getText().toLowerCase()).length) - 1;
            if (occurence > 0) {
                if (col_count > 2) {
                    col_count = 0;
                    row_count++;
                    if (row_count > 2) {
                        this._gridPane.addRow(row_count);
                    }
                }
                Button storeName = new Button(this._storeList.get(i - 6).getName());
                Image image = new Image(getClass().getResourceAsStream("icons8-department-shop-64.png"));
                ImageView img = new ImageView();
                img.setImage(image);
                img.setPickOnBounds(true);
                StoreRecord store = this._storeList.get(i - 6);
                img.setOnMouseClicked(e -> enterStore(store));
                img.setFitWidth(70);
                img.setFitHeight(70);
                this._gridPane.setHalignment(img, HPos.CENTER);
                this._gridPane.setValignment(img, VPos.TOP);
                this._gridPane.add(img, col_count, row_count);
                storeName.setStyle("-fx-font-weight: bold; -fx-text-fill: #4F4F4F; -fx-background-color: #E7ECEF; -fx-background-radius: 3em; -fx-border-color: #24D3FF28; -fx-border-width: 0.3em; -fx-border-radius: 3em");
                storeName.setOnAction(e -> enterStore(store));
                this._gridPane.setHalignment(storeName, HPos.CENTER);
                this._gridPane.setValignment(storeName, VPos.BOTTOM);
                this._gridPane.setMargin(img, new Insets(40, 0, 33, 0));
                this._gridPane.add(storeName, col_count, row_count);
                col_count++;
            }
            i++;
        }
        this._gridPane.addRow(++row_count);
        this._gridPane.addRow(++row_count);

    }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void initialize() throws SQLException {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._user = holder.getUser();
        getStores();
        this._baseGrid = this._gridPane;
        initButtons();
        initImage();
        displayProfile();
        this._logoHeader.getParent().setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode().toString().equals("ENTER"))
            {displayStore();}
        });
        displayStore();

    }

    public void enterStore(StoreRecord store) {
        Stage currentStage = (Stage) _gridPane.getScene().getWindow();
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
