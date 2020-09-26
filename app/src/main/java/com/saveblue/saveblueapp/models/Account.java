package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;

public class Account {

    private String name;

    private float currentBalance;

    private int startOfMonth;

    @SerializedName("_id")
    private String id;

    public Account(String name, float currentBalance, int startOfMonth) {
        this.name = name;
        this.currentBalance = currentBalance;
        this.startOfMonth = startOfMonth;
    }

    public String getName() {
        return name;
    }

    public float getCurrentBalance() {
        return currentBalance;
    }

    public int getStartOfMonth() {
        return startOfMonth;
    }

    public String getId() {
        return id;
    }
}
