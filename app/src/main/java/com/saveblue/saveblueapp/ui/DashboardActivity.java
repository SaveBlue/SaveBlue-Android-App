package com.saveblue.saveblueapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.adapters.DashboardAccountAdapter;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private DashboardAccountAdapter dashboardAccountAdapter;

    private List<Account> accountList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initUI();

        callApiAccounts(getIdFromJWT(getJWTfromSharedPref()));

    }

    public void initUI(){
        RecyclerView recyclerView = findViewById(R.id.accountRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        dashboardAccountAdapter = new DashboardAccountAdapter(getApplicationContext(), accountList);
        recyclerView.setAdapter(dashboardAccountAdapter);
    }

    private void callApiAccounts(String id){

        Call<List<Account>> callAsync = api.getUsersAccounts(getJWTfromSharedPref(),id);

        callAsync.enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Request Error", Toast.LENGTH_LONG).show();
                    return;
                }

                dashboardAccountAdapter.setAccountsList(response.body());

                System.out.println(response.body().get(0).getName());

            }

            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * JWT functions
     */

    public String getJWTfromSharedPref(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SaveBluePref", 0);

        return sharedPref.getString("JWT", "");
    }

    public String getIdFromJWT(String jwt){

        String jwtPayload = jwt.split("\\.")[1];
        String body = new String(Base64.decode(jwtPayload, Base64.URL_SAFE));
        System.out.println(body);

        return body.split("\"")[3];

    }
}