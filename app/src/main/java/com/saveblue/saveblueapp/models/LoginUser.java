package com.saveblue.saveblueapp.models;

public class LoginUser {

    // Attributes
    private String username;
    private String password;

    // Constructor
    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }


    // Getters and Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
