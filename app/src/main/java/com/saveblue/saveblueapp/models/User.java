package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;

public class User {

    private String username;

    private String email;

    @SerializedName("_id")
    private String id;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
