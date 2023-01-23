
/* LA CLASSE USER
Définit l'utilisateur actuel de l'app,
Classe parent de AdminUser, StandartUser, EmployeeUser
Crée lors de la connexion utilisateur via la BDD
 */

package com.example.istorefx;

public class User {
    private Cart    _cart;
    private String  _pseudo;
    private String  _email;
    private String  _role;
    private int     _id = -1;

    public          User() {
        this._pseudo = "Anonymous_User";
        this._id = 0;
        this._cart = new Cart();
    }
    public          User(String pseudo, String email) {
        this._pseudo = pseudo;
        this._email = email;
        this._id = 0;
        this._cart = new Cart();
    }
    public          User(String pseudo, String email, int id, String role) {
        this._pseudo = pseudo;
        this._email = email;
        this._id = id;
        this._cart = new Cart();
        this._role = role;
    }
    public String   getPseudo() {
        return (this._pseudo);
    }
    public int      getId() {
        return (this._id);
    }
    public String   getEmail() { return (this._email);}

    public String   getRole() { return (this._role);}
    public void     buyProduct(Product product) {
        this._cart.addProduct(product);
    }

    public Product  dropProduct(int id) {
        return (this._cart.delProduct(id));
    }
}