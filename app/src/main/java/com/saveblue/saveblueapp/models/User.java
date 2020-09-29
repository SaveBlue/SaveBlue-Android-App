package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;

public class User {

    private String username;

    private String email;

    private String password;

    @SerializedName("_id")
    private String id;


    // Constructor
    public User() {}

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }


    // Setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
