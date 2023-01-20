package com.example.istorefx;

public class    ProductStock {
    private int _id;
    private int _stock;
    public          ProductStock(int id) {
        this._id = id;
        this._stock = 0;
    }
    public int      getId() {
        return (this._id);
    }
    public int      getStock() {
        return (this._stock);
    }
    public void     incrStock() {
        this._stock = this._stock + 1;
    }
    public void     decrStock() {
        this._stock = this._stock - 1;
    }
}
