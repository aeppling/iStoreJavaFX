package com.example.istorefx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/* LA CLASSE INVENTAIRE COMPRISE DANS STORE
Permet de gérer les stock produits des magasins
Les données de stock sont dans la BDD
 */
public class Inventory {
    ArrayList<Product>  _productList;
    public          Inventory(ResultSet resultSet) {

        this._productList = new ArrayList<Product>();

        try {
            while (resultSet.next()) {
                this._productList.add(new Product(resultSet.getString("name"),
                        resultSet.getInt("id"),
                        resultSet.getFloat("price"),
                        resultSet.getInt("current_stock"),
                        resultSet.getInt("max_storage")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // RECUP WITH SQL HERE
    }

    public Product  takeProduct(int id) {
        int         i = 0;
        Product     bought = null;
        while (i < this._productList.size()) {
            if (this._productList.get(i).getId() == id) {
                bought = this._productList.get(i);
                this._productList.remove(i);
                break;
            }
            i++;
        }
        return (bought);
    }

    public void     dropProduct(Product product) {
        this._productList.add(product);
    }

    public int      getSize() {
        return (this._productList.size());
    }

    public Product  getElem(int index) {
        return (this._productList.get(index));
    }

    public ArrayList<Product> getInventory() {
        return (this._productList);
    }

    public ObservableList<String>           getInventoryString() {
        ObservableList<String> InventoryObs = FXCollections.observableArrayList();
        String[] inventoryString = new String[this._productList.size()];
        int count = 0;

        while (count < this._productList.size()) {
            inventoryString[count] = this._productList.get(count).getName();
            count++;
        }
        for (String s : inventoryString) {
            InventoryObs.add(s);
        }
        return (InventoryObs);
    }
}
