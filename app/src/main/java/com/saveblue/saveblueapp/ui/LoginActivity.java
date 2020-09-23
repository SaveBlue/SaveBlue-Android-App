package com.saveblue.saveblueapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.User;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        callApiUser("5f46455318b52809c8c35c2a");
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

                TextView t1 = findViewById(R.id.textView1);
                TextView t2 = findViewById(R.id.textView2);

                t1.setText(user.getUsername());
                t2.setText(user.getEmail());


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
            }
        });
    }
}