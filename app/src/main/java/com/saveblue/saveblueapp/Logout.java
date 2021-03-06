package com.saveblue.saveblueapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.saveblue.saveblueapp.ui.login.LoginActivity;

public class Logout extends Application {

    //Delete user data from shared preferences and log out user
    // reason =  0 -> normal logout
    // reason = 1 -> jwt expired
    public static void logout(Context applicationContext, int reason) {

        SharedPreferences sharedPref = applicationContext.getSharedPreferences("SaveBluePref", 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("JWT", "jwt");
        editor.putString("USERNAME", "username");
        editor.putString("PASS", "pass");
        editor.apply();

        switch (reason) {
            case 0:
                Toast.makeText(applicationContext, applicationContext.getString(R.string.logoutMessage0), Toast.LENGTH_LONG).show();
                break;

            case 1:
                Toast.makeText(applicationContext, applicationContext.getString(R.string.logoutMessage1), Toast.LENGTH_LONG).show();
                break;
        }

        // Redirect to login activity
        Intent intentDashboard = new Intent(applicationContext, LoginActivity.class);
        intentDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(intentDashboard);
    }

}
