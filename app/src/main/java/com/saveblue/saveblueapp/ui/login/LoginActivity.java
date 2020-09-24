package com.saveblue.saveblueapp.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.LoginUser;
import com.saveblue.saveblueapp.models.JWT;
import com.saveblue.saveblueapp.models.RegisterUser;
import com.saveblue.saveblueapp.ui.dashboard.DashboardActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements RegisterDialog.RegisterDialogListener {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

    }

    public void initUI() {
        // Find views
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        EditText usernameEditText = findViewById(R.id.usernameLogin);
        EditText passwordEditText = findViewById(R.id.passwordLogin);



        // TODO remove
        usernameEditText.setText("Sinane");
        passwordEditText.setText("Password1");

        // Set onClickListeners
        registerButton.setOnClickListener(v -> showRegisterDialog());


        // TODO: where to check input data (login or onCLickListener)
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }


    /**
     * Login Method
     */

    private void login(String username, String password) {

        LoginUser loginUser = new LoginUser(username, password);

        Call<JWT> callLoginUser = api.loginUser(loginUser);

        callLoginUser.enqueue(new Callback<JWT>() {
            @Override
            public void onResponse(Call<JWT> call, Response<JWT> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //store jwt in shared preferences
                storeJWT(response.body().getToken());

                //redirect to dashboard activity started on a new stack
                Intent intentDashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                intentDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentDashboard);

            }

            @Override
            public void onFailure(Call<JWT> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Register Methods
     */

    // Open Register Dialog
    private void showRegisterDialog() {

        RegisterDialog registerDialog = new RegisterDialog();
        registerDialog.show(getSupportFragmentManager(), "register dialog");
    }

    // Override the interface, set in the RegisterDialog class
    @Override
    public void sendRegisterData(String emailRegister, String usernameRegister, String passwordRegister) {

        register(emailRegister, usernameRegister, passwordRegister);
    }

    // Register User
    public void register(String email, String username, String password) {

        RegisterUser registerUser = new RegisterUser(email, username, password);

        Call<ResponseBody> callRegisterUser = api.registerUser(registerUser);

        callRegisterUser.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    // copy data to login fields
                    Toast.makeText(getApplicationContext(), "Register", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong email, username or password", Toast.LENGTH_SHORT).show();
                    // TODO: check what went wrong
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param jwt
     */


    public void storeJWT(String jwt) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SaveBluePref", 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("JWT", jwt);
        editor.apply();
    }

    /**
     * TODO: Clean
     */
    /*private void callApiUser(String id) {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmNDY0NTUzMThiNTI4MDljOGMzNWMyYSIsImlhdCI6MTYwMDg2Nzg5MCwiZXhwIjoxNjAwOTU0MjkwfQ.wf-_EkfjEox9neho97gvfIU6WJ_Sz_nLiVbWOQ5KNZw";

        Call<User> callUserAsync = api.getUserData(jwt, id);

        callUserAsync.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
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
    }*/
}