package com.saveblue.saveblueapp.ui.dashboard.profile;

import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    String task;

    EditText editText1;
    EditText editText2;
    TextInputLayout layout1;
    TextInputLayout layout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        task = getIntent().getStringExtra("Task");

        if (Objects.equals(task, "PASS")) {
            initUIPass();
        } else {
            initUIEdit();
        }


    }

    public void initToolbar(String text) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(text);

    }

    public void initUI() {
        editText1 = findViewById(R.id.inputField1);
        editText2 = findViewById(R.id.inputField2);
        layout1 = findViewById(R.id.inputLayout1);
        layout2 = findViewById(R.id.inputLayout2);
    }

    public void initUIPass() {
        initToolbar("Change Password");
        initUI();

        layout1.setStartIconDrawable(R.drawable.ic_baseline_lock_24);
        layout2.setStartIconDrawable(R.drawable.ic_baseline_lock_24);

        layout1.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        layout2.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        Button confirmButton = findViewById(R.id.confirmButton);
        setTextListeners();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleInputFields()) {
                    User user = new User();
                    user.setPassword(editText1.getText().toString());

                    callApiUpdatetUser(user);
                }
            }
        });

    }

    public void initUIEdit() {
        initToolbar("Edit Profile");
        initUI();

        layout1.setStartIconDrawable(R.drawable.ic_baseline_person_24);
        layout2.setStartIconDrawable(R.drawable.ic_baseline_email_24);

        Button confirmButton = findViewById(R.id.confirmButton);
        setTextListeners();
        fillUserData();

        // rename fields
        layout1.setHint("Username");
        layout2.setHint("Email");
        confirmButton.setText("Edit Profile");

        editText1.setInputType(InputType.TYPE_CLASS_TEXT);
        editText2.setInputType(InputType.TYPE_CLASS_TEXT);



        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(handleInputFields()) {
                   User user = new User();
                   user.setUsername(editText1.getText().toString());
                   user.setEmail(editText2.getText().toString());

                   callApiUpdatetUser(user);
               }
            }
        });
    }

    private void fillUserData(){
        //fetch jwt from dedicated handler class
        JwtHandler jwtHandler = new JwtHandler(getApplicationContext());
        String jwt = jwtHandler.getJwt();
        String id = jwtHandler.getId();

        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        profileViewModel.getUser(id, jwt).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

                editText1.setText(user.getUsername());
                editText2.setText(user.getEmail());
            }
        });
    }

    // --------------------------------------------------------
    // Input field handling
    // ---------------------------------------------------------

    private boolean handleInputFields() {
        boolean detectedError = false;

        if (Objects.requireNonNull(editText1.getText()).length() == 0) {
            layout1.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        if (Objects.requireNonNull(editText2.getText()).length() == 0) {
            layout2.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        if (Objects.equals(task, "PASS") && !Objects.requireNonNull(editText2.getText().toString()).equals(editText1.getText().toString())) {
            layout2.setError(getString(R.string.matchError));
            detectedError = true;
        }

        Pattern emailRegex = Pattern.compile("(?:[a-z0-9!#$%&'+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");
        if(Objects.equals(task, "EDIT") && !emailRegex.matcher(Objects.requireNonNull(editText2.getText().toString())).matches()){
            layout2.setError(getString(R.string.emailError));
            detectedError = true;
        }

        return !detectedError;
    }

    private void setTextListeners() {

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(editText1.getText()).length() > 0) {
                    layout1.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(editText2.getText()).length() > 0) {
                    layout2.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    // --------------------------------------------------------
    // API calls
    // ---------------------------------------------------------

    private void callApiUpdatetUser(User user) {
        JwtHandler jwtHandler = new JwtHandler(getApplicationContext());


        Call<ResponseBody> callUpdateUser = api.editUserData(jwtHandler.getJwt(), jwtHandler.getId(), user);

        callUpdateUser.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Snackbar.make(findViewById(R.id.constraintLayout), "Couldn't update User data :(", Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "User data updated", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


}