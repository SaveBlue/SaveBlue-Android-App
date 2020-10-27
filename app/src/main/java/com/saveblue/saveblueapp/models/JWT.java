package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;

public class JWT {

    // Attributes
    @SerializedName("JWT Token")
    private String token;


    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
