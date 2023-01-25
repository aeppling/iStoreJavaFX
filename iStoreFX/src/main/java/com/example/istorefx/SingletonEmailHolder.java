package com.example.istorefx;

public final class SingletonEmailHolder {

    private String _email;
    private final static SingletonEmailHolder INSTANCE = new SingletonEmailHolder();

    private SingletonEmailHolder() {}

    public static SingletonEmailHolder getInstance() {
        return INSTANCE;
    }

    public void setEmail(String email) {
        this._email = email;
    }

    public String getEmail() {
        return this._email;
    }
}