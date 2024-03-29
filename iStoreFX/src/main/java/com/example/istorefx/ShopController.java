package com.example.istorefx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class ShopController {
    private StoreRecord     _store;
    private Store           _shop;
    private User            _user;
    @FXML
    private Label           _storeName;
    @FXML
    private GridPane        _gridPane;
    @FXML
    private Button          _homeButton;
    @FXML
    private Button          _allstoresButton;
    @FXML
    private ImageView       _logoHeader;
    @FXML
    private GridPane        _employeeMenu;

    @FXML
    private ChoiceBox       _actionType;
    @FXML
    private ChoiceBox       _productAimed;
    @FXML
    private TextField       _actionAmount;
    @FXML
    private Button          _confirmAction;

    private boolean         _visibilityPanel;
    @FXML
    private ImageView       _profileIcon;
    @FXML
    private Label           _pseudoLabel;
    @FXML
    private Label           _emailLabel;
    @FXML
    private Button          _accountButton;
    @FXML
    private Button          _deleteStoreButton;
    @FXML
    private Button          _storeEmployeeButton;
    @FXML
    private Pane            _storeEmployeePane;
    @FXML
    private GridPane        _employeeGridPane;

    private ArrayList<User> _employeeList;
    @FXML
    private Button          _adminDashboardButton;

    @FXML
    private Button          _addNewProductButton;

    @FXML
    private Button          _deleteProductButton;

    public void validationProductDeleting(Product deleted) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product removed");
        alert.setHeaderText("Successfully removed '" + deleted.getName() + " id:" + deleted.getId());
        alert.setContentText("Successfully removed '" + deleted.getName() + "' from '" + this._store.getName() + "'");
        alert.showAndWait();
    }
    public void deleteProductDatabaseAndStore(Product to_delete) {
        this._shop.getInventory().remove(to_delete);
        String deleteQuery = new String("DELETE FROM iStoreProducts WHERE id = ?");
        String deleteLinkQuery = new String("DELETE FROM StoreProductLink WHERE ProductID = ? AND StoreID = ?");

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            PreparedStatement deleteLinkStatement = connection.prepareStatement(deleteLinkQuery);
            deleteLinkStatement.setInt(1, to_delete.getId());
            deleteLinkStatement.setInt(2, this._store.getId());
            deleteLinkStatement.execute();
            deleteStatement.setInt(1, to_delete.getId());
            deleteStatement.execute();
            validationProductDeleting(to_delete);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void confirmProductDelete(Product to_delete) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(this._store.getName() + " - Product removing");
        alert.setHeaderText("Removing '" + to_delete.getName() + "'");
        alert.setContentText("Removing '" + to_delete.getName() + "' id :" + to_delete.getId() + "\n" + "From '" + this._store.getName() + "'");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK) {
            deleteProductDatabaseAndStore(to_delete);
        }
    }
    public void deleteProductList() {
        Product choosen = null;
        ChoiceDialog<Product> dialog = new ChoiceDialog<Product>(this._shop.getProduct(0), this._shop.getInventory());
        dialog.setTitle(this._store.getName() + " - Deleting item");
        dialog.setHeaderText("Choose item to remove");
        dialog.setContentText("Choose an item to remove from '" + this._store.getName() + "' store");
        Optional<Product> result = dialog.showAndWait();
        if (result.isPresent()) {
            choosen = result.get();
            confirmProductDelete(choosen);
        }
    }

    public void validationProductAdding(String name, float price, int max_stock) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product adding");
        alert.setHeaderText("Successfully added '" + name + "' !");
        alert.setContentText("At " + price + "$ \n" + "With " + max_stock + " max stock.");
        alert.showAndWait();
    }
    public void addNewProductFinalStep(String name, float price, int max_stock) {
        String insertQuery = new String("INSERT INTO iStoreProducts (name, price, max_storage, current_stock) VALUES (?, ?, ?, 0)");
        String insertLinkQuery = new String("INSERT INTO StoreProductLink (ProductID, StoreID) VALUES (?, ?)");

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, name);
            insertStatement.setFloat(2, price);
            insertStatement.setInt(3, max_stock);
            int nb_affected = insertStatement.executeUpdate();
            if (nb_affected == 0) {
                connection.close();
                errorProductAdding("Error on database side...");
                return ;
            }
            int user_id = -1;
            // get new product id
            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user_id = (int)generatedKeys.getLong(1);
                }
                else {
                    connection.close();
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            //
            if (user_id == -1) {
                connection.close();
                errorProductAdding("Error on database side...");
                return ;
            }
            PreparedStatement insertLinkStatement = connection.prepareStatement(insertLinkQuery);
            insertLinkStatement.setInt(1, user_id);
            insertLinkStatement.setInt(2, this._store.getId());
            int nb_affected_link = insertLinkStatement.executeUpdate();
            if (nb_affected_link == 0) {
                errorProductAdding("Error on database side...");
                connection.close();
                return ;
            }
            validationProductAdding(name, price, max_stock);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void confirmProductAdding(String name, float price, int max_stock) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Product adding");
        alert.setHeaderText("Product preview");
        alert.setContentText("Adding '" + name + "'\n\n" + "At " + price + "$ \n" + "With " + max_stock + " max stock.");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK) {
            addNewProductFinalStep(name, price, max_stock);
        }
    }
    public void errorProductAdding(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Product adding error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public void addNewProductCheckingStep(String name, String price, String max_stock) {
        float c_price = -1;
        int   c_max_stock = -1;

        if ((name.isEmpty()) || (price.isEmpty()) || (max_stock.isEmpty())) {
            errorProductAdding("Please fill out the required fields");
            return;
        }
        try {
            c_max_stock = Integer.parseInt(max_stock);
            c_price = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (c_price <= 0) {
            errorProductAdding("Price must be a valid positive > 0 decimal number");
            return;
        }
        if (c_max_stock <= 0) {
            errorProductAdding("Max stock must be a valid positive > 0 number");
            return ;
        }
        confirmProductAdding(name, c_price, c_max_stock);
        // display preview and confirm
    }
    public void addNewProduct() {
        Dialog<Button> dialog = new Dialog<Button>();
        GridPane gridContainer = new GridPane();
        Label productNameLabel = new Label();
        TextField productName = new TextField();
        productName.setPromptText("Enter product name...");
        Label priceLabel = new Label();
        TextField price = new TextField();
        price.setPromptText("Enter price (ex : 3.46)");
        Label maxStockLabel = new Label();
        TextField maxStock = new TextField();
        maxStock.setPromptText("Enter maximum stock...");
        dialog.setTitle(this._store.getName() + " - Adding new item");
        gridContainer.addRow(0);
        gridContainer.add(productNameLabel, 0, 0);
        gridContainer.addRow(1);
        gridContainer.add(productName, 0, 1);
        gridContainer.addRow(2);
        gridContainer.add(priceLabel, 0, 2);
        gridContainer.addRow(3);
        gridContainer.add(price, 0, 3);
        gridContainer.add(maxStock, 0, 2);
        Button test = new Button("Add product");
        test.setOnAction(e -> {
            addNewProductCheckingStep(productName.getText(), price.getText(), maxStock.getText());
        });
        gridContainer.addRow(4);
        gridContainer.add(test, 0, 4);
        dialog.getDialogPane().setContent(gridContainer);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
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

    public void updateDatabaseStock(String actionType, Product product, int quantity) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            if (actionType == "Lower") {
                String lowerUpdateRequest = "UPDATE iStoreProducts SET iStoreProducts.current_stock = iStoreProducts.current_stock - ? WHERE iStoreProducts.id = ?";
                PreparedStatement preparedUpdateRequest = connection.prepareStatement(lowerUpdateRequest);
                preparedUpdateRequest.setInt(1, quantity);
                preparedUpdateRequest.setInt(2, product.getId());
                preparedUpdateRequest.executeUpdate();
            } else if (actionType == "Add") {
                String addUpdateRequest = "UPDATE iStoreProducts SET iStoreProducts.current_stock = iStoreProducts.current_stock + ? WHERE iStoreProducts.id = ?";
                PreparedStatement preparedUpdateRequest = connection.prepareStatement(addUpdateRequest);
                preparedUpdateRequest.setInt(1, quantity);
                preparedUpdateRequest.setInt(2, product.getId());
                preparedUpdateRequest.executeUpdate();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateLocalStock(String actionType, Product product, int quantity) {
        if (actionType == "Lower") {
            product.lowerStock(quantity);
        }
        else if (actionType == "Add") {
            product.addStock(quantity);
        }
    }
    public void executeStockAction(String actionType, Product product, int quantity, String mode) {
        updateLocalStock(actionType, product, quantity);
        updateDatabaseStock(actionType, product, quantity);
    }
    public void errorStockAlert(String msg, String mode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (mode == "user") {
            alert.setTitle("Product purchase error");
        }
        else {
            alert.setTitle("Stock managing error");
        }
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public void employeeConfirm(String actionType, String productAimed, String quantity_s, Product product) {
        int quantity = Integer.valueOf(quantity_s);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Stock managing");
        alert.setHeaderText(null);
        alert.setContentText("Confirm " + actionType + " of " + quantity + " unit(s) of " + product.getName());
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK) {
            executeStockAction(actionType, product, quantity, "employee");
        }
    }

    public boolean isActionPossible(String actionType, String productAimed, String actionAmount, String mode, Product product) {
        boolean bool = false;
        Integer actionAmountInt = Integer.valueOf(actionAmount);

        if (actionType == "Add") {
            if ((product.getMaxStock() - product.getCurrentStock()) < actionAmountInt) {
                //PAS POSSIBLE D'AJOUT
                String msg = new String ("You're trying to add " + actionAmountInt + " but here is only " + (product.getMaxStock() - product.getCurrentStock()) + " storage left");
                errorStockAlert(msg, mode);
            } else {
                bool = true;
            }
        }
        else if (actionType == "Lower") {
            String actionString = "lower";
            if (mode == "user") {
                actionString = "buy";
            }
            if ((product.getCurrentStock() - actionAmountInt) < 0) {
                //PAS POSSIBLE DE SOUSTRAIRE
                String msg = new String("You're trying to " + actionString + " " + actionAmountInt + " but there is only " + product.getCurrentStock() + " in stock");
                errorStockAlert(msg, mode);
            } else {
                bool = true;
            }
        }
        return (bool);
    }
    public void employeeAction() {
        String  actionAmount = _actionAmount.getText();
        if ((_actionType.getSelectionModel().getSelectedItem() == null)
                || (_productAimed.getSelectionModel().getSelectedItem() == null)) {
            errorStockAlert("You need to fill all the fields", "employee");
            return;
        }
        String  actionType = _actionType.getSelectionModel().getSelectedItem().toString();
        String  productAimed = _productAimed.getSelectionModel().getSelectedItem().toString();
        if (actionType.isEmpty() || productAimed.isEmpty() || (actionAmount.isEmpty())) {
            errorStockAlert("You need to fill all the fields", "employee");
            return;
        }
        Product product = null;
        Integer actionAmountInt = Integer.valueOf(actionAmount);
        int count = 0;
        while (count < this._shop.getInventorySize()) {
            if (this._shop.getProduct(count).getName() == productAimed) {
                product = this._shop.getProduct(count);
                break;
            }
            count++;
        }
        if (actionAmountInt <= 0) {
            errorStockAlert("You can't make an operation of 0 or negative unit", "employee");
        }
        else if (isActionPossible(actionType, productAimed, actionAmount, "employee", product) == true) {
            employeeConfirm(actionType, productAimed, actionAmount, product);
        }
    }

    public ArrayList<Integer> getLinkedId() {
        String query = new String("SELECT * FROM StoreUserLink WHERE StoreID = ?");
        ArrayList<Integer>  listID = new ArrayList<Integer>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            PreparedStatement preparedEmployeeIDRequest = connection.prepareStatement(query);
            preparedEmployeeIDRequest.setInt(1, this._store.getId());
            ResultSet EmployeeIDList = preparedEmployeeIDRequest.executeQuery();
            while (EmployeeIDList.next()) {
                listID.add(EmployeeIDList.getInt("UserID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (listID);
    }

    public void getEmployeeArray() {
        String query = new String("SELECT * FROM iStoreUsers");
        ArrayList<Integer> listID = getLinkedId();

        this._employeeList.clear();
     //   final String[] data = yourList.toArray(new String[yourList.size()]);
       // final java.sql.Array sqlArray = connection.createArrayOf(typeName, data);
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet allUsers = preparedStatement.executeQuery();
            while (allUsers.next()) {
                int count = 0;
                while (count < listID.size()) {
                    if (allUsers.getInt("id") == listID.get(count)) {
                        User employeeToAdd = new User(allUsers.getString("pseudo"),
                                allUsers.getString("email"), allUsers.getInt("id"));
                        this._employeeList.add(employeeToAdd);
                        break;
                    }
                    count++;
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // CHANGE SINGLE EMPLOYEE CHECK
    }

    public void adminConfirmationMessage(int id) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setTitle(this._store.getName() + " - Deleting employee");
            alert.setHeaderText("Confirm deletion");
            alert.setContentText("Confirm removing id:" + id + " from " + this._store.getName());
            Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK) {
                deleteEmployee(id);
        }
    }

    public void adminValidationMessage(int id, String mode) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (mode == "delete") {
            alert.setTitle(this._store.getName() + " - Deleted employee");
            alert.setHeaderText("Employee deleted");
            alert.setContentText("Employee with id : " + id + " has been deleted from " + this._store.getName());
        }
        else {
            alert.setTitle(this._store.getName() + " - Added employee");
            alert.setHeaderText("Employee added");
            alert.setContentText("Employee with id : " + id + " has been added to " + this._store.getName());
        }
        alert.showAndWait();
    }
    public void deleteEmployee(int id) {
        String queryDeleteLink = new String("DELETE FROM StoreUserLink WHERE UserID = ?");
        int count = 0;

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            PreparedStatement preparedDeleteStatement = connection.prepareStatement(queryDeleteLink);
            preparedDeleteStatement.setInt(1, id);
            preparedDeleteStatement.execute();
            connection.close();
        } catch (SQLException e ) {
            e.printStackTrace();
        }
        while (count < this._employeeList.size()) {
            if (this._employeeList.get(count).getId() == id) {
                this._employeeList.remove(count);
                break ;
            }
            count++;
        }
        adminValidationMessage(id, "delete");
        // refreshing window
        getEmployeeArray();
        setEmployeeList();
        // x2 showStoreEmployee() to refresh window
        showStoreEmployee();
        showStoreEmployee();
    }

    public ArrayList<String> getAllUsersCompacted() {
        ArrayList<String> returnTab = new ArrayList<String>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String sqlUsersRequest = "SELECT pseudo, email, id FROM iStoreUsers LEFT JOIN StoreUserLink ON iStoreUsers.id = StoreUserLink.UserID WHERE StoreUserLink.StoreID != ? OR StoreUserLink.StoreID is null";
            PreparedStatement preparedStoreStatement = connection.prepareStatement(sqlUsersRequest);
            preparedStoreStatement.setInt(1, this._store.getId());
            ResultSet resultUsers = preparedStoreStatement.executeQuery();
            while (resultUsers.next()) {
                String userCompacted = new String(resultUsers.getInt("id") +
                        "   " + resultUsers.getString("email") +
                        "   " + resultUsers.getString("pseudo"));
                returnTab.add(userCompacted);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (returnTab);
    }

    public void errorAddEmployee(String compactedNewEmployee) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(this._store.getName() + " - Employee management error");
        alert.setHeaderText("Error on adding employee");
        alert.setContentText("Error on adding " + compactedNewEmployee + " to " + this._store.getName() + ", already employee here");
        alert.showAndWait();
    }
    public int getIDfromCompacted(String compactedNewEmployee) {
        int id = -1;

        id = Integer.valueOf(compactedNewEmployee.substring(0, compactedNewEmployee.indexOf(' ')));
        return (id);
    }
    public void addNewEmployeeDatabase(String compactedNewEmployee) {
        String insertQuery = new String("INSERT INTO StoreUserLink (UserID, StoreID) VALUES (?, ?)");
        String alreadyEmployeeQuery = new String("SELECT * FROM StoreUserLink WHERE UserID = ?");

        int id = getIDfromCompacted(compactedNewEmployee);
        if (id == -1) {
            errorAddEmployee(compactedNewEmployee);
            return ;
        }
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            PreparedStatement alreadyEmployee = connection.prepareStatement(alreadyEmployeeQuery);
            alreadyEmployee.setInt(1, id);
            ResultSet results = alreadyEmployee.executeQuery();
            if (results.next()) {
                errorAddEmployee(compactedNewEmployee);
                return ;
            }
            PreparedStatement preparedInsertStatement = connection.prepareStatement(insertQuery);
            preparedInsertStatement.setInt(1, id);
            preparedInsertStatement.setInt(2, this._store.getId());
            preparedInsertStatement.execute();
            connection.close();
            adminValidationMessage(id, "add");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addNewEmployee() {
        String choosen = null;
        ArrayList userList = getAllUsersCompacted();
        ChoiceDialog<String> dialog = new ChoiceDialog<>("none", userList);
        dialog.setTitle(this._store.getName() + " - Adding employee");
        dialog.setHeaderText("Choose new employee");
        dialog.setContentText("Choose user to add as employee to " + this._store.getName());
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            choosen = result.get();
            if (choosen == "none")
                return ;
            addNewEmployeeDatabase(choosen);
        }
        // refreshing window
        getEmployeeArray();
        setEmployeeList();
        // x2 showStoreEmployee() to refresh window
        showStoreEmployee();
        showStoreEmployee();
    }
    public void setEmployeeList() {
        int count = 0;

        //ADD COLUMN HERE FOR DELETE BUTTON
        // THEN AT LAST LINE, ADD ONE ROW FOR ADDING BUTTON
        this._employeeGridPane.getChildren().clear();
        this._employeeGridPane.addColumn(0);
        this._employeeGridPane.addColumn(1);
        this._employeeGridPane.addColumn(2);
        while (count < this._employeeList.size()) {
            Label   employeeInfos = new Label(this._employeeList.get(count).getPseudo() + "   "
                                                + this._employeeList.get(count).getEmail() + "   id:" +
                                                    this._employeeList.get(count).getId());

            this._employeeGridPane.addRow(count);
            this._employeeGridPane.add(employeeInfos, 0, count);
            this._employeeGridPane.setMargin(employeeInfos, new Insets(0, 0, 0, 50));
            if (this._user.getRole().equals("admin")) {
                Button deleteButton = new Button(" remove ");
                int id = this._employeeList.get(count).getId();
                deleteButton.setOnAction(e -> adminConfirmationMessage(id));
                this._employeeGridPane.add(deleteButton, 1, count);
                this._employeeGridPane.setMargin(deleteButton, new Insets(0, 50, 0, 0));
            }
            this._employeeGridPane.setVgap(20);
            this._employeeGridPane.setHgap(40);
            count++;
        }
        if (this._user.getRole().equals("admin")) {
            Button addButton = new Button("Add new employee to " + this._store.getName());
            addButton.setOnAction(e -> addNewEmployee());
            this._employeeGridPane.addRow(count);
            this._employeeGridPane.add(addButton, 0, count);
            this._employeeGridPane.setMargin(addButton, new Insets(10, 0, 0, 270));// SET MARGIN HERE WITH inset(0, 0, 0, 0)
        }
    }

    public void showStoreEmployee() {
        if (this._visibilityPanel == true) {
            this._storeEmployeePane.setVisible(false);
            this._visibilityPanel = false;
        } else {
            this._storeEmployeePane.setVisible(true);
            this._visibilityPanel = true;
        }
    }
    public void employeeInit() {
        ObservableList<String>      actionTypes = FXCollections.observableArrayList();
        actionTypes.add("Add");
        actionTypes.add("Lower");
        this._employeeList = new ArrayList<User>();

        this._storeEmployeeButton.setVisible(true);
        this._employeeMenu.setVisible(true);
        this._productAimed.setItems(this._shop.getInventoryString());
        this._actionType.setItems(actionTypes);
        getEmployeeArray();
        if (this._employeeList != null)
            setEmployeeList();
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

        if(this._user.getRole().equals("admin")){
            this._adminDashboardButton.setVisible(true);
            this._deleteStoreButton.setVisible(true);

        }else{
            this._adminDashboardButton.setVisible(false);
            this._deleteStoreButton.setVisible(false);
        }

    }

    public void buyProduct(Product product, int quantity) {
        if (isActionPossible("Lower", product.getName(), Integer.toString(quantity), "user", product) == false)
            return;
        executeStockAction("Lower", product, quantity, "user");
    }

    public void errorBuyNull() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Buying error");
        alert.setHeaderText(null);
        alert.setContentText("You can't operate with values inferiors or equal to 0");
        alert.showAndWait();
    }
    public void buyConfirm(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        int quantity = 0;
        String buyMsg = new String("Confirm buy of " + product.getName() + " for " + product.getPrice() + "$");
        alert.setTitle("iStores -  Buy Confirmation");
        alert.setHeaderText(buyMsg);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(quantity, 1, 2, 3, 4, 5, 10, 20, 50, 100);
        dialog.setTitle("iStore - Purchasing");
        dialog.setHeaderText("Choose the amount");
        dialog.setContentText(product.getPrice() + "$ x ");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            quantity = result.get();
            if (quantity <= 0) {
                errorBuyNull();
                return ;
            }
            float total_price = (float)quantity * product.getPrice();
            total_price = (float)((int)(total_price * 100f))/100f;
            alert.setHeaderText("Confirm buy of " + product.getName());
            alert.setContentText(quantity + " x " + product.getName() + " (" + product.getPrice() + "$) = " + total_price + "$");
            Optional<ButtonType> action = alert.showAndWait();
            if (action.get() == ButtonType.OK) {
                buyProduct(product, quantity);
            }
        }
    }
    public void createImageProduct(int x, int y, Product product) {
        Image       image = new Image(getClass().getResourceAsStream("product-icon.png"));
        ImageView   img = new ImageView();
        Button      label = null;

        if (((this._user.getRole().equals("employee")) && isEmployeeWorkingHere(this._user))
                || (this._user.getRole().equals("admin"))) {
            label = new Button(product.getName() + " : " + product.getPrice() + "$" + "\n->" + product.getCurrentStock() + "/" + product.getMaxStock() + " left in stock");
        }
        else {
            label = new Button(product.getName() + " : " + product.getPrice() + "$");
        }
        if (label == null)
            return;
        label.setAlignment(Pos.CENTER);
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
        }
    }

    public boolean isEmployeeWorkingHere(User user) {
        boolean check = false;

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            String lowerUpdateRequest = "SELECT * FROM iStoreUsers INNER JOIN StoreUserLink ON iStoreUsers.id = StoreUserLink.UserID WHERE StoreID = ?";
            PreparedStatement preparedEmployeeRequest = connection.prepareStatement(lowerUpdateRequest);
            preparedEmployeeRequest.setInt(1, this._store.getId());
            ResultSet EmployeeList = preparedEmployeeRequest.executeQuery();
            while (EmployeeList.next()) {
                if (EmployeeList.getInt("id") == user.getId()) {
                    check = true;
                    connection.close();
                    return (check);
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (check);
    }
    public void initialize() throws SQLException {
        this._visibilityPanel = false;
        this._employeeMenu.setVisible(false);
        this._storeEmployeeButton.setVisible(false);
        this._storeEmployeePane.setVisible(false);
        Connection connection = null;
        SingletonStoreHolder holder = SingletonStoreHolder.getInstance();
        SingletonUserHolder holder2 = SingletonUserHolder.getInstance();
        this._store = holder.getStore();
        this._storeName.setText(this._store.getName());
        this._storeName.setStyle("-fx-font-weight: bold;");
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
        initImage();
        if (((this._user.getRole().equals("employee")) && isEmployeeWorkingHere(this._user))
            || (this._user.getRole().equals("admin"))) {
            employeeInit();
        }
        displayProfile();
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

    public void DeleteStoreDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
        String DeleteStoreUserRequest = "DELETE FROM StoreUserLink WHERE StoreID = ?";
        PreparedStatement preparedDeleteStoreUserRequest = connection.prepareStatement(DeleteStoreUserRequest);
        preparedDeleteStoreUserRequest.setInt(1, this._store.getId());
        preparedDeleteStoreUserRequest.execute();
        preparedDeleteStoreUserRequest.close();
        // Delete links with products
        String DeleteStoreProductRequest = "DELETE FROM StoreProductLink WHERE StoreID = ?";
        PreparedStatement preparedDeleteStoreProductRequest = connection.prepareStatement(DeleteStoreProductRequest);
        preparedDeleteStoreProductRequest.setInt(1, this._store.getId());
        preparedDeleteStoreProductRequest.execute();
        preparedDeleteStoreProductRequest.close();
        // delete store
        String DeleteStoreRequest = "DELETE FROM iStoreStores WHERE id = ?";
        PreparedStatement preparedDeleteStoreRequest = connection.prepareStatement(DeleteStoreRequest);
        preparedDeleteStoreRequest.setInt(1, this._store.getId());
        preparedDeleteStoreRequest.execute();
        preparedDeleteStoreRequest.close();
        connection.close();
        AllStores();
    }
    public void DeleteStore() {
        // Delete the store and All link with the store
        // Delete link with users
        //
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting store");
        alert.setHeaderText("Deleting '" + this._store.getName() + "' from iStore.");
        alert.setContentText("Do you want to continue ?");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK) {
            try {
                DeleteStoreDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
