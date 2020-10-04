package com.saveblue.saveblueapp.ui.dashboard.profile;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        String task = getIntent().getStringExtra("Task");

        if(Objects.equals(task, "PASS")){
            initUIPass();
        }
        else {
            initUIEdit();
        }


    }
    public void initToolbar(String text){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(text);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }
    }

    public void initUIPass(){
        initToolbar("Change Password");

        EditText editText1 = findViewById(R.id.inputField1);
        EditText editText2 = findViewById(R.id.inputField2);
        Button confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setPassword(editText1.getText().toString());

                callApiUpdatetUser(user);
            }
        });

    }

    public void initUIEdit(){
        initToolbar("Edit Profile");

        EditText editText1 = findViewById(R.id.inputField1);
        EditText editText2 = findViewById(R.id.inputField2);
        TextInputLayout layout1 = findViewById(R.id.inputLayout1);
        TextInputLayout layout2 = findViewById(R.id.inputLayout2);
        Button confirmButton = findViewById(R.id.confirmButton);

        // rename fields
        layout1.setHint("Username");
        layout2.setHint("Email");
        confirmButton.setText("Edit Profile");

        editText1.setInputType(InputType.TYPE_CLASS_TEXT);
        editText2.setInputType(InputType.TYPE_CLASS_TEXT);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();

                if(editText1.getText().length() > 0)
                    user.setUsername(editText1.getText().toString());

                if(editText2.getText().length() > 0)
                    user.setEmail(editText2.getText().toString());

                callApiUpdatetUser(user);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up

        return false;
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
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
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