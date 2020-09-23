package com.saveblue.saveblueapp.models;

import com.google.gson.annotations.SerializedName;

public class JWT {

    @SerializedName("JWT Token")
    private String token;

    // Getter

    public String getToken() {
        return token;
    }


    // Setter

    public void setToken(String token) {
        this.token = token;
    }
}
