package com.saveblue.saveblueapp.models;

public class RegisterUser {

    private String username;

    private String email;

    private String password;


    // Contructor

    public RegisterUser(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
