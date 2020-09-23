package com.saveblue.saveblueapp.models;

public class AuthUser {

    private String username;

    private String password;

    public AuthUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
