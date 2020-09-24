package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;

public class Account {

    private String name;

    private String currentBalance;

    private String startOfMonth;

    @SerializedName("_id")
    private String id;

    public String getName() {
        return name;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public String getStartOfMonth() {
        return startOfMonth;
    }

    public String getId() {
        return id;
    }
}
