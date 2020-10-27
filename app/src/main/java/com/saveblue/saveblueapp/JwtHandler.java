package com.saveblue.saveblueapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

// Custom class to handle jwt fetching from shared preferences and decoding it
public class JwtHandler extends Application {

    private final String jwt;
    private final String id;
    private final Context context;

    public JwtHandler(Context context) {
        this.context = context;

        jwt = getJWTfromSharedPref();
        id = getIdFromJWT();
    }

    public String getJwt() {
        return jwt;
    }

    public String getId() {
        return id;
    }

    // Get jwt from shared preferences
    private String getJWTfromSharedPref() {
        SharedPreferences sharedPref = context.getSharedPreferences("SaveBluePref", 0);

        return sharedPref.getString("JWT", "");
    }

    // Decode id from jwt
    private String getIdFromJWT() {

        String jwtPayload = jwt.split("\\.")[1];
        String body = new String(Base64.decode(jwtPayload, Base64.URL_SAFE));

        return body.split("\"")[3];
    }
}
