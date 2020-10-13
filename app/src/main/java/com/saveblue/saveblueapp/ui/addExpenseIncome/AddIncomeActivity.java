package com.saveblue.saveblueapp.ui.addExpenseIncome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.TimestampHandler;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Income;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddIncomeActivity extends AppCompatActivity {

    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private JwtHandler jwtHandler;

    private OverviewViewModel overviewViewModel;
    private ArrayAdapter<String> spinnerArrayAdapter;

    private List<Account> accountList = new ArrayList<>();
    private List<String> accountListNames = new ArrayList<>();

    private String incomeID;
    String task;


    // UI elements for easier work
    Spinner spinnerAccountIncomeAdd;
    EditText editTextNameAddIncome;
    EditText editTextDescriptionAddIncome;
    EditText editTextDateAddIncome;
    EditText editTextAmountAddIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        jwtHandler = new JwtHandler(getApplicationContext());

        // handle intent from calling activity
        Intent receivedIntent = getIntent();
        task = receivedIntent.getStringExtra("Task");

        initUIElements();

        assert task != null;
        if (task.equals("ADD")) {
            initUIAdd();
        }
        else {
            incomeID = getIntent().getStringExtra("IncomeID");
            initUIEdit();
        }

        // Get user's accounts
        overviewViewModel = new ViewModelProvider(this).get(OverviewViewModel.class);
        observerSetup();

    }

    private void initUIElements(){
        // Set UI elements
        spinnerAccountIncomeAdd = findViewById(R.id.spinnerAccountAddIncome);
        editTextNameAddIncome = findViewById(R.id.editTextNameAddIncome);
        editTextDescriptionAddIncome = findViewById(R.id.editTextDescriptionAddIncome);
        editTextDateAddIncome = findViewById(R.id.editTextDateAddIncome);
        editTextAmountAddIncome = findViewById(R.id.editTextAmountAddIncome);

        // Populate spinner
        spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountListNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountIncomeAdd.setAdapter(spinnerArrayAdapter);
    }

    private void initUIAdd() {
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Income");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button buttonAddIncome = findViewById(R.id.buttonAddIncome);

        // Set onClickListeners
        buttonAddIncome.setOnClickListener(v -> {
                    String jwt = jwtHandler.getJwt();
                    String userId = jwtHandler.getId();
                    String accountId = (accountList.get((int) spinnerAccountIncomeAdd.getSelectedItemId()).getId());

                    //TODO pohendli polja za vnos v newIncome
                    String date2Api = TimestampHandler.parse2Mongo( editTextDateAddIncome.getText().toString());

                    Income newIncome = new Income(accountId, userId, editTextNameAddIncome.getText().toString(), editTextDescriptionAddIncome.getText().toString(),date2Api, Float.parseFloat(editTextAmountAddIncome.getText().toString()));
                    addIncome(newIncome, jwt);
                }
        );
    }

    private void initUIEdit(){
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Income");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }

        // setup buttons
        Button buttonEditIncome = findViewById(R.id.buttonAddIncome);
        buttonEditIncome.setText("Update Income");

        Button buttonDeleteIncome = findViewById(R.id.buttonDeleteIncome);
        buttonDeleteIncome.setVisibility(View.VISIBLE);

        // Set onClickListeners
        buttonEditIncome.setOnClickListener(v -> {
                    String userId = jwtHandler.getId();
                    String accountId = (accountList.get((int) spinnerAccountIncomeAdd.getSelectedItemId()).getId());

                    //TODO pohendli polja za vnos v newIncome
                    String date2Api = TimestampHandler.parse2Mongo( editTextDateAddIncome.getText().toString());


            Income editedIncome = new Income(accountId, userId, editTextNameAddIncome.getText().toString(), editTextDescriptionAddIncome.getText().toString(), date2Api, Float.parseFloat(editTextAmountAddIncome.getText().toString()));
                    updateIncome(incomeID, editedIncome, jwtHandler.getJwt());
                }
        );

        buttonDeleteIncome.setOnClickListener(v -> deleteIncome(incomeID, jwtHandler.getJwt()));



    }

    public void setSpinnerToRightAccount(String baseAccountID){
        for (int i = 0; i < accountList.size(); i++) {
            if(accountList.get(i).getId().equals(baseAccountID)){
                spinnerAccountIncomeAdd.setSelection(i);
            }
        }
    }


    private void observerSetup() {
        //fetch jwt from dedicated handler class
        String jwt = jwtHandler.getJwt();
        String id = jwtHandler.getId();

        overviewViewModel.getAccounts(id, jwt).observe(this, newAccountList -> {
            accountList = newAccountList;

            for (Account account : newAccountList) {
                accountListNames.add(account.getName());
            }

            spinnerArrayAdapter.notifyDataSetChanged();

            // set spinner to current account if called from account details activity
            if(Objects.equals(getIntent().getStringExtra("CallingActivity"), "Details") && task.equals("ADD")) {
                setSpinnerToRightAccount(getIntent().getStringExtra("BaseAccountID"));
            }

            if(task.equals("EDIT")){
                // fetch income data and fill UI elements
                getIncome(incomeID, jwtHandler.getJwt());
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up

        return false;
    }

    // --------------------------------------------------------
    // API calls
    // ---------------------------------------------------------

    private void addIncome(Income newIncome, String jwt) {

        Call<ResponseBody> callAddIncome = api.addIncome(jwt, newIncome);

        callAddIncome.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Income added", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIncome(String incomeID, String jwt){
        System.out.println("----------------------------------");

        Call<Income> callGetIncome = api.getIncome(jwt, incomeID);

        callGetIncome.enqueue(new Callback<Income>() {
            @Override
            public void onResponse(@NotNull Call<Income> call, @NotNull Response<Income> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }
                Income income = response.body();
                assert income != null;

                //fill UI elements from fetched income;
                editTextNameAddIncome.setText(income.getName());
                editTextDescriptionAddIncome.setText(income.getDescription());
                editTextDateAddIncome.setText(income.getDate());
                editTextAmountAddIncome.setText(String.valueOf(income.getAmount()));

                setSpinnerToRightAccount(income.getAccountID());

            }

            @Override
            public void onFailure(@NotNull Call<Income> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void updateIncome(String incomeID, Income income, String jwt){
        Call<ResponseBody> callUpdateIncome = api.editIncome(jwt, incomeID, income);

        callUpdateIncome.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Income updated", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteIncome(String incomeID, String jwt){
        Call<ResponseBody> callDeleteIncome = api.deleteIncome(jwt, incomeID);

        callDeleteIncome.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Income deleted", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }



}