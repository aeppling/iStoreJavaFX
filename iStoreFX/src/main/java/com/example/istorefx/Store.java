package com.example.istorefx;

import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;


/* LA CLASSE MAGASIN
Crée lorsque qu'un  utilisateur souhaite accéder à un magasin
Données récupérée dans la BDD à ce moment
1 instance de Store à la fois
 */
public class Store {
    private Inventory   _inventory;
    private String      _name;
    private int         _id;

    /*
    Select * From iStoreProducts INNER JOIN StoreProductLink ON iStoreProducts.id = StoreProductLink.ProductID
     INNER JOIN iStoreStores ON iStoreStores.id = StoreProductLink.StoreID
        //// REQUEST PRODUCT FROM STORE
     */
    public              Store(String name, int id, Connection connection) {
        this._name = name;
        this._id = id;
        try {
            this._inventory = new Inventory(this.getResultSet(connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ResultSet    getResultSet(Connection connection) throws SQLException {
        String query = "SELECT * From iStoreProducts LEFT JOIN StoreProductLink ON iStoreProducts.id = StoreProductLink.ProductID LEFT JOIN iStoreStores ON iStoreStores.id = StoreProductLink.StoreID WHERE iStoreStores.id = ?;";
        PreparedStatement preparedProductStatement = connection.prepareStatement(query);
        preparedProductStatement.setInt(1, this._id);
        ResultSet results = preparedProductStatement.executeQuery();
        return (results);
    }

    /* Check if id already in array, if true, increment stock of id in array*/
    public boolean      check_if_already_in(int id, ArrayList<ProductStock> list) {
        boolean         value = false;
        int             i = 0;

        while (i < list.size()) {
            if (list.get(i).getId() == id) {
                value = true;
                break ;
            }
            i++;
        }
        return (value);
    }

    // FUNCTION TO CALL AT THE END OF STORE USAGE
    // This will update the products stock in the DB
    public void         sendUpdate(ArrayList<ProductStock> productList, Connection connection) {
        int             i = 0;

        while (i < productList.size()) {
            ResultSet results = null;
            String query = "UPDATE iStoreProducts SET iStoreProducts.current_stock = ? WHERE iStoreProducts.id LIKE ?";
            try {
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, productList.get(i).getStock());
                pstmt.setInt(2, productList.get(i).getId());
                results = pstmt.executeQuery(query);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            i++;
        }
    }
    /* This function will update the stock of products in the database from
     the store inventory */
    public void         updateDB(Connection connection) {
        int i = 0;
        ArrayList<ProductStock> productStock = new ArrayList<ProductStock>();
        // Fill ProductStock array which will UPDATE the DB
        while (i < this._inventory.getSize()) {
            if (check_if_already_in(this._inventory.getElem(i).getId(), productStock) == false)
                productStock.add(new ProductStock(productStock.get(i).getId()));
            else { // else Id is already in array, parse array and increment stock
                int y = 0;
                while (y < productStock.size()) {
                    if (productStock.get(y).getId() == productStock.get(i).getId()) {
                        productStock.get(y).incrStock();
                        break;
                    }
                    y++;
                }
            }
            // GET ID OF PRODUCT AND ADD ONE
            i++;
        }
        // End of fill, now update DB with ProductStock array
        sendUpdate(productStock, connection);
    }

    //USAGE EX : user.cart.addProduct(takeProduct(id))
    public Product      takeProduct(int id) {
        return (this._inventory.takeProduct(id));
    }

    // USAGE EX: store.dropProduct(user.cart.delProduct(id))
    public void         dropProduct(Product product) {
        this._inventory.dropProduct(product);
    }

    public int          getInventorySize()
    {
        return (this._inventory.getSize());
    }

    public Product      getProduct(int i) {
        return (this._inventory.getElem(i));
    }

    public ArrayList<Product>    getInventory() {
        return (this._inventory.getInventory());
    }

    public ObservableList<String>  getInventoryString() {
        return (this._inventory.getInventoryString());
    }
}
