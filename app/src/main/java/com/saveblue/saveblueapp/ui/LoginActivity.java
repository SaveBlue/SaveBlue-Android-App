package com.saveblue.saveblueapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.AuthUser;
import com.saveblue.saveblueapp.models.JWT;
import com.saveblue.saveblueapp.models.User;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    private Button loginButton;

    private EditText username;

    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views
        loginButton = findViewById(R.id.loginButton);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // Set onClickListener
        loginButton.setOnClickListener(v -> login());


        //Intent intentAbout = new Intent(this, DashboardActivity.class);
        //startActivity(intentAbout);

        //callApiUser("5f46455318b52809c8c35c2a");
    }

    // Login Method
    private void login(){

        AuthUser authUser = new AuthUser(username.getText().toString(),password.getText().toString());

        Call<JWT> callLoginUser = api.loginUser(authUser);

        callLoginUser.enqueue(new Callback<JWT>() {
            @Override
            public void onResponse(Call<JWT> call, Response<JWT> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(getApplicationContext(), response.body().getToken(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JWT> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void callApiUser(String id){
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmNDY0NTUzMThiNTI4MDljOGMzNWMyYSIsImlhdCI6MTYwMDg2Nzg5MCwiZXhwIjoxNjAwOTU0MjkwfQ.wf-_EkfjEox9neho97gvfIU6WJ_Sz_nLiVbWOQ5KNZw";

        Call<User> callUserAsync = api.getUserData(jwt,id);

        callUserAsync.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Request Error", Toast.LENGTH_LONG).show();
                    return;
                }

                User user = response.body();

                //TextView t1 = findViewById(R.id.textView1);
                //TextView t2 = findViewById(R.id.textView2);

                //t1.setText(user.getUsername());
                //t2.setText(user.getEmail());


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
            }
        });
    }
}