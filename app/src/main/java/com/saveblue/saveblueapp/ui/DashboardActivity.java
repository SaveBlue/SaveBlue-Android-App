package com.saveblue.saveblueapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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

        callApiAccounts("5f5004238f0b0545d4b7524b");

    }

    public void initUI(){
        RecyclerView recyclerView = findViewById(R.id.accountRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        dashboardAccountAdapter = new DashboardAccountAdapter(getApplicationContext(), accountList);
        recyclerView.setAdapter(dashboardAccountAdapter);
    }

    private void callApiAccounts(String id){
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmNTAwNDIzOGYwYjA1NDVkNGI3NTI0YiIsImlhdCI6MTYwMDg5NTA5OCwiZXhwIjoxNjAwOTgxNDk4fQ.P1uXlS7ksJihXqsRL54DBaRejasoPqBepyJgQRr2v_E";

        Call<List<Account>> callAsync = api.getUsersAccounts(jwt,id);

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
}