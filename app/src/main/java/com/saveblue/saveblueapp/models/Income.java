package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;


public class Income {

    // Fields
    @SerializedName("_id")
    private String id;

    private String accountID;

    private String userID;

    private String description;

    private String date;

    private float amount;

    private String category1;
    // Constructor


    public Income(String accountID, String userID, String description, String date, float amount, String category1) {
        this.accountID = accountID;
        this.userID = userID;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.category1 = category1;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getUserID() {
        return userID;
    }

    public String getCategory1() {
        return category1;
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

    public String getId() { return id; }
}
