package com.example.istorefx;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import java.sql.*;
import java.util.ArrayList;
import java.lang.*;
import java.util.Objects;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;

public class StoreController {
    private User            _user;
    ArrayList<StoreRecord>  _storeList;

    @FXML
    private ImageView       _profileIcon;
    @FXML
    private ScrollBar       _scrollBar;

    @FXML
    private GridPane        _gridPane;
    @FXML
    private Label           _emailLabel;
    @FXML
    private Label           _pseudoLabel;
    @FXML
    private Button          _categoriesButton;
    @FXML
    private Button          _allstoresButton;


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
        this._pseudoLabel.setText(this._user.getPseudo());
        this._emailLabel.setText(this._user.getEmail());
    }
    public void initialize() {
        System.out.println("Receiving data");
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
            System.out.println(this._storeList.get(tot_count).getName());
            System.out.println(this._storeList.get(tot_count).getId());
            System.out.println(this._storeList.get(tot_count).getStoreImg());
            System.out.println("in col = " + col_count + " and row = " + row_count);
            createStoreWindow(col_count, row_count, this._storeList.get(tot_count));
            col_count++;
            tot_count++;
        }
    }
    public void createStoreWindow(int x, int y, StoreRecord storeRecord) {
        //IMAGE CREATING
        Button button = new Button(storeRecord.getName());
        Image image = new Image(getClass().getResourceAsStream("icons8-department-shop-64.png"));
        ImageView img = new ImageView();

        // ADD STORE IMAGE
        img.setImage(image);
        img.setPickOnBounds(true); // allows click on transparent areas
        img.setOnMouseClicked(e -> System.out.println("Clicked store : " + storeRecord.getName()));
        //this._gridPane.getColumnConstraints().get(x).getMaxWidth();
        //img.setFitWidth();
       // System.out.println("Height : " + image.getWidth());
        img.setFitWidth(80);
        img.setFitHeight(80);
        this._gridPane.setHalignment(img, HPos.CENTER);
        this._gridPane.setValignment(img, VPos.TOP);
        this._gridPane.setMargin(button, new Insets(0, 0, 0, 0));
        System.out.println("Height after : " + img.getFitWidth());
        this._gridPane.add(img, x, y);
        // ADD STORE BUTTON
        button.setStyle("-fx-text-fill: white; -fx-background-color: black;");
        this._gridPane.setMargin(button, new Insets(0, 0, 10, 0));
        this._gridPane.setHalignment(button, HPos.CENTER);
        this._gridPane.setValignment(button, VPos.BOTTOM);
        this._gridPane.add(button, x, y);

    }
    public static ArrayList<ArrayList<Node>> gridPaneToArrayList(GridPane gridPane){

        ArrayList<ArrayList<Node>> matrix = new ArrayList<ArrayList<Node>>();

        for(int row = 0; row < gridPane.getRowCount(); row++) {
            ArrayList<Node> column = new ArrayList<>();
            for (int col = 0; col < gridPane.getColumnCount(); col++) {
                column.add(gridPane.getChildren().get(row * gridPane.getColumnCount() + col));
            }
            matrix.add(column);
        }

        return matrix;
    }

    public void AllStores() {
        System.out.println("Allstores");
    }
    public void Categories() {
        System.out.println("Categories");
    }
}
