package com.example.istorefx;

public class StoreRecord {
    private int     _id;
    private String  _name;

    private String _store_img;

    StoreRecord(String name, int id, String store_img) {
        this._name = name;
        this._id = id;
        this._store_img = store_img;
    }

    public String   getName() {
        return (this._name);
    }

    public int      getId() {
        return (this._id);
    }

    public String   getStoreImg() { return (this._store_img);}
}
