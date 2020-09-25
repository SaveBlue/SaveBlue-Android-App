package com.saveblue.saveblueapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.saveblue.saveblueapp.ui.login.LoginActivity;

public class Logout extends Application {

    public static void logout(Context applicationContext) {
        //delete jwt from shared preferences
        SharedPreferences sharedPref = applicationContext.getSharedPreferences("SaveBluePref", 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("JWT", "jwt");
        editor.apply();

        //redirect to login activity
        Intent intentDashboard = new Intent(applicationContext, LoginActivity.class);
        intentDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(intentDashboard );
    }

}
