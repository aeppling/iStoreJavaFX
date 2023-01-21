package com.example.istorefx;

public class SingletonStoreHolder {
    private StoreRecord _store;
    private final static SingletonStoreHolder INSTANCE = new SingletonStoreHolder();

    private SingletonStoreHolder() {}

    public static SingletonStoreHolder getInstance() {
        return INSTANCE;
    }

    public void setStore(StoreRecord store) {
        this._store = store;
    }

    public StoreRecord getStore() {
        return this._store;
    }
}
