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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
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


    public void displayStore() {
        int i = 6;
        int col_count = 0;
        int row_count = 3;

        this._gridPane = this._baseGrid;
        System.out.println(this._searchBar.getText());
        while (i - 6 < this._storeList.size()) {
            int occurence = (this._storeList.get(i - 6).getName().split(this._searchBar.getText()).length) - 1;
            if (occurence > 0) {
                System.out.println("DISPLAY" + i);
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
                //this._gridPane.getColumnConstraints().get(x).getMaxWidth();
                //img.setFitWidth();
                // System.out.println("Height : " + image.getWidth());
                img.setFitWidth(60);
                img.setFitHeight(60);
                this._gridPane.setHalignment(img, HPos.CENTER);
                this._gridPane.setValignment(img, VPos.TOP);
                this._gridPane.add(img, col_count, row_count);
                storeName.setStyle("-fx-text-fill: white; -fx-background-color: black; -fx-border-radius: 5em");
                this._gridPane.setHalignment(storeName, HPos.CENTER);
                this._gridPane.setValignment(storeName, VPos.BOTTOM);
                this._gridPane.setMargin(img, new Insets(40, 0, 40, 0));
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
    public void initialize() {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._user = holder.getUser();
        getStores();
        this._baseGrid = this._gridPane;
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
}
