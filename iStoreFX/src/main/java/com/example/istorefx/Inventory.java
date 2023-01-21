package com.example.istorefx;

import java.sql.ResultSet;
import java.util.ArrayList;

/* LA CLASSE INVENTAIRE COMPRISE DANS STORE
Permet de gérer les stock produits des magasins
Les données de stock sont dans la BDD
 */
public class Inventory {
    ArrayList<Product>  _productList;
    public          Inventory(ResultSet resultSet) {

        this._productList = new ArrayList<Product>();
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
}
