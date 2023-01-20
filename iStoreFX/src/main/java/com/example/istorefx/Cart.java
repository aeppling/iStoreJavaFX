package com.example.istorefx;

import java.util.ArrayList;

/* LA CLASSE PANIER CONTENU DANS USER
Permet à l'utilisateur de stocker et gérer ces produits
 */
public class Cart {
    ArrayList<Product> _productList;
    public          Cart() {
        this._productList = new ArrayList<Product>();
    }
    public void     addProduct(Product product) {

    }
    public Product  delProduct(int id) {
        return (this._productList.get(id));
    }
}
