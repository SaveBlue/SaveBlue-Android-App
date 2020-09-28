package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;


public class Income {

    // Fields
    @SerializedName("_id")
    private String id;

    private String accountID;

    private String userID;

    private String name;

    private String description;

    private String date;

    private float amount;

    // Constructor


    public Income(String accountID, String userID, String name, String description, String date, float amount) {
        this.accountID = accountID;
        this.userID = userID;
        this.name = name;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }
}
