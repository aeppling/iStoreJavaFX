package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class AllUsersController {

    @FXML
    private Button      _allStoresButton;
    @FXML
    private Button      _homeButton;
    @FXML
    private ImageView   _logoHeader;
    @FXML
    private Button      _accountButton;
    @FXML
    private GridPane    _usersListPane;
    private User        _user;

    private ArrayList<User> _usersList;
    @FXML
    private ImageView       _profileIcon;
    @FXML
    private Label           _pseudoLabel;
    @FXML
    private Label           _emailLabel;

    @FXML
    private TextField       _searchBar;
    @FXML
    private Button          _searchButton;
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
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("allstores-icon.png"));
        ImageView img = new ImageView();
        img.setImage(image);
        img.setFitWidth(55);
        img.setFitHeight(55);
        this._allStoresButton.setGraphic(img);
        //HOME BTN
        javafx.scene.image.Image image3 = new Image(getClass().getResourceAsStream("home-icon.png"));
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
        if(resultRole.getString("role").equals("admin")){
            this._adminDashboardButton.setVisible(true);

        }else{
            this._adminDashboardButton.setVisible(false);
        }
        preparedRoleStatement.close();
        connection.close();
    }

    public void searchRequest() {
        String  research = new String(this._searchBar.getText());
        displayUsers(research);
    }
    public void displayUsers(String request) {
        this._usersListPane.getChildren().clear();
        int count = 0;
        int jump_count = 0;

        while (jump_count < this._usersList.size()) {
            int occurence = (this._usersList.get(jump_count).getPseudo().toLowerCase().split(request.toLowerCase()).length) - 1;
            if (occurence > 0) {
                Label userInfos = new Label(this._usersList.get(jump_count).getPseudo() + "    " + this._usersList.get(jump_count).getEmail());
                this._usersListPane.addRow(0);
                this._usersListPane.add(userInfos, 0, count);
                this._usersListPane.setVgap(20);
                count++;
            }
            jump_count++;
        }
    }

    public void getUsersList() {
        this._usersList = new ArrayList<User>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlUsersRequest = "SELECT pseudo, email FROM iStoreUsers";
            PreparedStatement preparedStoreStatement = connection.prepareStatement(sqlUsersRequest);
            ResultSet resultUsers = preparedStoreStatement.executeQuery();
            while (resultUsers.next()) {
                User newUser = new User(resultUsers.getString("pseudo"), resultUsers.getString("email"));
                this._usersList.add(newUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this._accountButton.getParent().setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode().toString().equals("ENTER"))
            {searchRequest();}
        });
    }
    public void initialize() throws SQLException {
        SingletonUserHolder holder2 = SingletonUserHolder.getInstance();
        this._user = holder2.getUser();
        initButtons();
        initImage();
        displayProfile();
        getUsersList();
        displayUsers("");
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
    public void Account() {
        Stage currentStage = (Stage) _allStoresButton.getScene().getWindow();
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
