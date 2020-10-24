package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;

public class Expense {

    // Fields
    @SerializedName("_id")
    private String id;

    private String accountID;

    private String userID;

    private String category1;

    private String category2;

    private String description;

    private String date;

    private float amount;

    // Constructor

    public Expense(String accountID, String userID, String description, String date, float amount, String category1, String category2) {
        this.accountID = accountID;
        this.userID = userID;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.category1 = category1;
        this.category2 = category2;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getUserID() {
        return userID;
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

    public String getCategory1() {
        return category1;
    }

    public String getCategory2() {
        return category2;
    }
}
