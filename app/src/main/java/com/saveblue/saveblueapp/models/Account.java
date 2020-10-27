package com.saveblue.saveblueapp.models;


import com.google.gson.annotations.SerializedName;

public class Account {

    // Attributes
    @SerializedName("_id")
    private String id;
    private String name;
    private float totalBalance;
    private float availableBalance;
    private int startOfMonth;


    // Constructor
    public Account(String name, int startOfMonth) {
        this.name = name;
        this.startOfMonth = startOfMonth;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public float getTotalBalance() {
        return totalBalance;
    }

    public float getAvailableBalance() { return availableBalance; }

    public int getStartOfMonth() {
        return startOfMonth;
    }

    public String getId() {
        return id;
    }

}
