package com.example.istorefx;

public class StoreRecord {
    private int     _id;
    private String  _name;

    StoreRecord(String name, int id) {
        this._name = name;
        this._id = id;
    }

    public String   getName() {
        return (this._name);
    }

    public int      getId() {
        return (this._id);
    }
}
