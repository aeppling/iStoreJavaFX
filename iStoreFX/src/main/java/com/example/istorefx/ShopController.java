package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

import javafx.scene.control.Button;

public class ShopController {
    private StoreRecord _store;
    private Store       _shop;
    private User        _user;
    @FXML
    private Label       _storeName;
    @FXML
    private GridPane    _gridPane;

    @FXML
    private Button          _categoriesButton;
    @FXML
    private Button          _homeButton;
    @FXML
    private Button          _allstoresButton;
    public void initButtons() {
        //ALL STORES BTN
        Image image = new Image(getClass().getResourceAsStream("allstores-icon.png"));
        ImageView img = new ImageView();
        img.setImage(image);
        img.setFitWidth(80);
        img.setFitHeight(80);
        this._allstoresButton.setGraphic(img);
        //CATEGORIES BTN
        Image image2 = new Image(getClass().getResourceAsStream("categories-icon.png"));
        ImageView img2 = new ImageView();
        img2.setImage(image2);
        img2.setFitWidth(70);
        img2.setFitHeight(75);
        this._categoriesButton.setGraphic(img2);
        //HOME BTN
        Image image3 = new Image(getClass().getResourceAsStream("home-icon.png"));
        ImageView img3 = new ImageView();
        img3.setImage(image3);
        img3.setFitWidth(80);
        img3.setFitHeight(80);
        this._homeButton.setGraphic(img3);
    }
    public void createTitleProduct(int x, int y, Product product) {


    }
    public void buyConfirm(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Buying product");
        alert.setHeaderText(null);
        alert.setContentText("Confirm buy of " + product.getName() + " for " + product.getPrice() + "$");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK) {
            System.out.println("BOUGHT");
        }
    }
    public void createImageProduct(int x, int y, Product product) {
        Image       image = new Image(getClass().getResourceAsStream("product-icon.png"));
        ImageView   img = new ImageView();
        Button      label = new Button(product.getName() + " : " + product.getPrice() + "$");

        // ADD PRODUCT IMAGE
        img.setImage(image);
        img.setPickOnBounds(true);
        img.setOnMouseClicked(e -> buyConfirm(product));
        img.setFitWidth(70);
        img.setFitHeight(70);
        this._gridPane.setHalignment(img, HPos.CENTER);
        this._gridPane.setValignment(img, VPos.TOP);
        this._gridPane.setMargin(img, new Insets(50, 0, 20, 0));
        this._gridPane.add(img, x, y);
        label.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-radius: 5em");
        this._gridPane.setHalignment(label, HPos.CENTER);
        this._gridPane.setValignment(label, VPos.BOTTOM);
        this._gridPane.setMargin(label, new Insets(0, 0, 0, 0));
        this._gridPane.add(label, x, y);
   /*     this._gridPane.setHalignment(buyButton, HPos.CENTER);
        this._gridPane.setValignment(buyButton, VPos.BOTTOM);
        this._gridPane.setMargin(buyButton, new Insets(0, 0, 0, 0));
        this._gridPane.add(buyButton, x, y);

    */
    }
    public void displayInventory() {
        int     tot_count = 0;
        int     row_count = 0;
        int     col_count = 0;

        while (tot_count < this._shop.getInventorySize()) {
            if (col_count == 3) {
                this._gridPane.addRow(row_count);
                col_count = 0;
                row_count++;
            }
            Product product = this._shop.getProduct(tot_count);
            createImageProduct(col_count, row_count, product);
            tot_count++;
            col_count++;
            System.out.println(this._shop.getInventorySize() - tot_count);
        }
    }
    public void initialize() {
        Connection connection = null;
        SingletonStoreHolder holder = SingletonStoreHolder.getInstance();
        SingletonUserHolder holder2 = SingletonUserHolder.getInstance();
        this._store = holder.getStore();
        this._storeName.setText(this._store.getName());
        this._user = holder2.getUser();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this._shop = new Store(this._store.getName(), this._store.getId(), connection);
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initButtons();
        displayInventory();
    }
    public void Home() {
        Stage currentStage2 = (Stage) this._storeName.getScene().getWindow();
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
        System.out.println("Allstores");
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
}
