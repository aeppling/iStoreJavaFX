package com.example.istorefx;

import java.sql.*;

public class UpdateEmployeeController {

    private User _admin;

    private String _email;

    private int _id;
    private String password;

    public void initialize() {
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._admin = holder.getUser();
        this._id = GetAccountIdFromMail();
    }
    public void DeleteAccount() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");
            // Delete from StoreUserLink
            String sqlDeleteLink = "DELETE * FROM StoreUserLink WHERE UserID = ?";
            PreparedStatement preparedDeleteLinkStatement = connection.prepareStatement(sqlDeleteLink);
            preparedDeleteLinkStatement.setInt(1, this._id);
            preparedDeleteLinkStatement.execute();
            preparedDeleteLinkStatement.close();

            // Delete account from iStoreUsers
            String sqlDeleteAccount = "DELETE * FROM iStoreUsers WHERE email = ?";
            PreparedStatement preparedDeleteAccountStatement = connection.prepareStatement(sqlDeleteAccount);
            preparedDeleteAccountStatement.setString(1, this._email);
            preparedDeleteAccountStatement.execute();
            preparedDeleteAccountStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int GetAccountIdFromMail(){
        int id = 0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://bdhwxvxddidxmx75bp76-mysql.services.clever-cloud.com:3306/bdhwxvxddidxmx75bp76", "uka5u4mcxryqvq9d", "cDxsM6QAf1IcnXfN4AGC");

            String sqlGetId = "SELECT id FROM iStoreUsers WHERE email = ?";
            PreparedStatement preparedGetIdStatement = connection.prepareStatement(sqlGetId);
            preparedGetIdStatement.setString(1, this._email);
            ResultSet resultId = preparedGetIdStatement.executeQuery();
            preparedGetIdStatement.close();
            id = resultId.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;

    }
}
