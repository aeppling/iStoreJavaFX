package com.example.istorefx;

/* LA CLASSE PRODUIT
Pouvant être contenu dans l'inventaire du store
Ou le panier utilisateur.
Récupérer et mis dans l'inventaire du store lors de la création
du store (demande d'accès de l'utilisateur au magasin)
 */

public class Product {
    private String  _name;
    private int     _id;
    private float   _price;

    private int     _current_stock;
    private int     _max_stock;
    public          Product(String name, int id, float price, int current_stock, int max_stock) {
        this._name = name;
        this._id = id;
        this._price = price;
        this._current_stock = current_stock;
        this._max_stock = max_stock;
    }

    public float    getPrice() {
        return (this._price);
    }
    public String   getName() {
        return (this._name);
    }
    public int      getId() {
        return (this._id);
    }

    public void     incrStock() {
        this._current_stock++;
    }

    public void     decrStock() {
        this._current_stock--;
    }

    public void     addStock(int nb) {
        this._current_stock = this._current_stock + nb;
    }

    public void     lowerStock(int nb) {
        this._current_stock = this._current_stock - nb;
    }

    public int      getCurrentStock() {
        return (this._current_stock);
    }
    public int      getMaxStock() {
        return (this._max_stock);
    }

    @Override
    public String toString() {
        return (this._name + " - id:" + this._id);
    }

}