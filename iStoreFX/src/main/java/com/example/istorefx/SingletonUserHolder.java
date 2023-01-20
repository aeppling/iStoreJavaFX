package com.example.istorefx;

public final class SingletonUserHolder {

    private User _user;
    private final static SingletonUserHolder INSTANCE = new SingletonUserHolder();

    private SingletonUserHolder() {}

    public static SingletonUserHolder getInstance() {
        return INSTANCE;
    }

    public void setUser(User user) {
        this._user = user;
    }

    public User getUser() {
        return this._user;
    }
}