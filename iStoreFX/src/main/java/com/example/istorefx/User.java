
/* LA CLASSE USER
Définit l'utilisateur actuel de l'app,
Classe parent de AdminUser, StandartUser, EmployeeUser
Crée lors de la connexion utilisateur via la BDD
 */

package com.example.istorefx;

public class User {
    private String  _pseudo;
    private String  _email;
    private String  _role;
    private int     _id = -1;

    public          User() {
        this._pseudo = "Anonymous_User";
        this._id = 0;
    }
    public          User(String pseudo, String email) {
        this._pseudo = pseudo;
        this._email = email;
        this._id = 0;
    }

    public          User(String pseudo, String email, int id) {
        this._pseudo = pseudo;
        this._email = email;
        this._id = id;
    }
    public          User(String pseudo, String email, int id, String role) {
        this._pseudo = pseudo;
        this._email = email;
        this._id = id;
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
}