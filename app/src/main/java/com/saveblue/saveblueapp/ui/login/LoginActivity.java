package com.saveblue.saveblueapp.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.LoginUser;
import com.saveblue.saveblueapp.models.JWT;
import com.saveblue.saveblueapp.models.RegisterUser;
import com.saveblue.saveblueapp.ui.dashboard.DashboardActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements RegisterDialog.RegisterDialogListener {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    private EditText usernameEditText;
    private TextInputLayout usernameLayout;

    private EditText passwordEditText;
    private TextInputLayout passwordLayout;

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


        usernameEditText = findViewById(R.id.usernameLogin);
        usernameLayout = findViewById(R.id.usernameLoginLayout);

        passwordEditText = findViewById(R.id.passwordLogin);
        passwordLayout = findViewById(R.id.passwordLoginLayout);


        // TODO remove
        usernameEditText.setText("Sinane");
        passwordEditText.setText("Password1");


        // Set onClickListeners
        registerButton.setOnClickListener(v -> showRegisterDialog());


        loginButton.setOnClickListener(v -> {
            if (handleInputFields())
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        });


        // --------------------------------------------------------
        // Text field handling
        // ---------------------------------------------------------

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


    /**
     * Login Method
     */

    private void login(String username, String password) {

        LoginUser loginUser = new LoginUser(username, password);

        Call<JWT> callLoginUser = api.loginUser(loginUser);

        callLoginUser.enqueue(new Callback<JWT>() {
            @Override
            public void onResponse(@NotNull Call<JWT> call, @NotNull Response<JWT> response) {
                if (!response.isSuccessful()) {
                    //Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.constraintLayout), "Wrong username or password", Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();

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
            public void onFailure(@NotNull Call<JWT> call, @NotNull Throwable t) {
                //Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.constraintLayout), "Can't connect to server :(", Snackbar.LENGTH_LONG)
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

        Snackbar.make(findViewById(R.id.constraintLayout), "Account registered :D", Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();

        usernameEditText.setText(usernameRegister);
        passwordEditText.setText(passwordRegister);

    }

    public void storeJWT(String jwt) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SaveBluePref", 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("JWT", jwt);
        editor.apply();
    }

}