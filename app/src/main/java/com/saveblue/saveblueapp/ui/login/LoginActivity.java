package com.saveblue.saveblueapp.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.JWT;
import com.saveblue.saveblueapp.models.LoginUser;
import com.saveblue.saveblueapp.ui.dashboard.DashboardActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements RegisterDialog.RegisterDialogListener {
    private final SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    private EditText usernameEditText;
    private TextInputLayout usernameLayout;

    private EditText passwordEditText;
    private TextInputLayout passwordLayout;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

        tryLogin();
    }

    // initialize ui elements
    public void initUI() {

        // Find views
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        usernameEditText = findViewById(R.id.usernameLogin);
        usernameLayout = findViewById(R.id.usernameLoginLayout);

        passwordEditText = findViewById(R.id.passwordLogin);
        passwordLayout = findViewById(R.id.passwordLoginLayout);

        // Set onClickListeners
        registerButton.setOnClickListener(v -> showRegisterDialog());
        loginButton.setOnClickListener(v -> {
            if (handleInputFields()) {
                progressBar.setVisibility(View.VISIBLE);
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString(), 0);
            }
        });

        progressBar = findViewById(R.id.progressBar);


        // --------------------------------------------------------
        // Text field handling
        // ---------------------------------------------------------

        //clearing of error messages under text fields
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(usernameEditText.getText()).length() > 0) {
                    usernameLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(passwordEditText.getText()).length() > 0) {
                    passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // on login click check for input field correctness
    private boolean handleInputFields() {
        boolean detectedError = false;

        if (Objects.requireNonNull(usernameEditText.getText()).length() == 0) {
            usernameLayout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        if (Objects.requireNonNull(passwordEditText.getText()).length() == 0) {
            passwordLayout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        return !detectedError;
    }


    // --------------------------------------------------------
    // API calls
    // ---------------------------------------------------------

    // logs in the user and saves jwt in shared preferences
    // mode 0 -> normal
    // mode 1 -> auto login try
    private void login(String username, String password, int mode) {

        LoginUser loginUser = new LoginUser(username, password);

        Call<JWT> callLoginUser = api.loginUser(loginUser);

        callLoginUser.enqueue(new Callback<JWT>() {
            @Override
            public void onResponse(@NotNull Call<JWT> call, @NotNull Response<JWT> response) {
                if (!response.isSuccessful()) {
                    if (mode != 1) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(findViewById(R.id.constraintLayout), getString(R.string.loginMessage), Snackbar.LENGTH_LONG)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    }
                    return;
                }

                //hide progress bar
                progressBar.setVisibility(View.GONE);

                //store jwt in shared preferences
                storeUserData(response.body().getToken(), username, password);

                //redirect to dashboard activity started on a new stack
                Intent intentDashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                intentDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentDashboard);

            }

            @Override
            public void onFailure(@NotNull Call<JWT> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.constraintLayout), getString(R.string.serverMessage), Snackbar.LENGTH_LONG)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
            }
        });
    }


    /**
     * Register Methods
     */

    // Open Register Dialog
    private void showRegisterDialog() {

        RegisterDialog.display(getSupportFragmentManager());
    }

    // Override the interface, set in the RegisterDialog class
    @Override
    public void sendRegisterData(String usernameRegister, String passwordRegister) {

        Snackbar.make(findViewById(R.id.constraintLayout), getString(R.string.registerErrorMessage), Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();

        // fill login fields with newly registered user data
        usernameEditText.setText(usernameRegister);
        passwordEditText.setText(passwordRegister);

    }

    // try to login user from saved data
    private void tryLogin() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SaveBluePref", 0);
        String username = sharedPref.getString("USERNAME", "");
        String password = sharedPref.getString("PASS", "");

        if (!username.equals("username") && !password.equals("pass")) {
            login(username, password, 1);
        }
    }

    // stores the JWT to shared preferences
    public void storeUserData(String jwt, String username, String password) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SaveBluePref", 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("JWT", jwt);
        editor.putString("USERNAME", username);
        editor.putString("PASS", password);
        editor.putString("JWT", jwt);
        editor.apply();
    }

}