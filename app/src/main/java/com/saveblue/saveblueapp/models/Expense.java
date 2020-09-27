package com.saveblue.saveblueapp.models;

public class Expense {

    // Fields

    private String accountID;

    private String userID;

    private String name;

    private String description;

    private String date;

    private float amount;

    // Constructor

    public Expense(String accountID, String userID, String name, String description, String date, float amount) {
        this.accountID = accountID;
        this.userID = userID;
        this.name = name;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }
}
