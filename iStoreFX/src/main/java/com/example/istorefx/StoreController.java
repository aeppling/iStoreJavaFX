package com.example.istorefx;

public class StoreController {
    private User _user;
    public void initialize() {
        System.out.println("Receiving data");
        SingletonUserHolder holder = SingletonUserHolder.getInstance();
        this._user = holder.getUser();


    }
}
