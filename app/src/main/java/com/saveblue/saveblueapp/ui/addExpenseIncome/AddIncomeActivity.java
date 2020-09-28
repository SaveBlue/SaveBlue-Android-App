package com.saveblue.saveblueapp.ui.addExpenseIncome;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Income;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewViewModel;

import java.util.ArrayList;
import java.util.List;

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

    private String task;
    private String incomeID;


    // UI elements for easier work
    Spinner spinnerAccountIncomeAdd;
    EditText editTextNameAddIncome;
    EditText editTextDescriptionAddIncome;
    EditText editTextDateAddAccount;
    EditText editTextAmountAddIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        jwtHandler = new JwtHandler(getApplicationContext());
        task = getIntent().getStringExtra("Task");

        initUIElements();

        assert task != null;
        if (task.equals("ADD")) {
            initUIAdd();
        } else {
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
        editTextDateAddAccount = findViewById(R.id.editTextDateAddAccount);
        editTextAmountAddIncome = findViewById(R.id.editTextAmountAddIncome);

        // Populate spinner
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountListNames);
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

                    Income newIncome = new Income(accountId, userId, editTextNameAddIncome.getText().toString(), editTextDescriptionAddIncome.getText().toString(), editTextDateAddAccount.getText().toString(), Float.parseFloat(editTextAmountAddIncome.getText().toString()));
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
        buttonEditIncome.setText("Edit Income");

        Button buttonDeleteIncome = findViewById(R.id.buttonDeleteIncome);
        buttonDeleteIncome.setVisibility(View.VISIBLE);

        // Set onClickListeners
        buttonEditIncome.setOnClickListener(v -> {
                    String userId = jwtHandler.getId();
                    String accountId = (accountList.get((int) spinnerAccountIncomeAdd.getSelectedItemId()).getId());

                    //TODO pohendli polja za vnos v newIncome

                    Income editedIncome = new Income(accountId, userId, editTextNameAddIncome.getText().toString(), editTextDescriptionAddIncome.getText().toString(), editTextDateAddAccount.getText().toString(), Float.parseFloat(editTextAmountAddIncome.getText().toString()));
                    updateIncome(incomeID, editedIncome, jwtHandler.getJwt());
                }
        );

        buttonDeleteIncome.setOnClickListener(v -> deleteIncome(incomeID, jwtHandler.getJwt()));

        // fetch income data and fill UI elements
        getIncome(incomeID, jwtHandler.getJwt());

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

        overviewViewModel.getAccounts(id, jwt).observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> newAccountList) {
                accountList = newAccountList;

                for (Account account : newAccountList) {
                    accountListNames.add(account.getName());
                }

                spinnerArrayAdapter.notifyDataSetChanged();
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
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Income added", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIncome(String incomeID, String jwt){
        System.out.println("----------------------------------");

        Call<Income> callGetIncome = api.getIncome(jwt, incomeID);

        callGetIncome.enqueue(new Callback<Income>() {
            @Override
            public void onResponse(Call<Income> call, Response<Income> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }
                Income income = response.body();

                //fill UI elements from fetched income;
                editTextNameAddIncome.setText(income.getName());
                editTextDescriptionAddIncome.setText(income.getDescription());
                editTextDateAddAccount.setText(income.getDate());
                editTextAmountAddIncome.setText(String.valueOf(income.getAmount()));

                setSpinnerToRightAccount(income.getAccountID());

            }

            @Override
            public void onFailure(Call<Income> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void updateIncome(String incomeID, Income income, String jwt){
        Call<ResponseBody> callUpdateIncome = api.editIncome(jwt, incomeID, income);

        callUpdateIncome.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Income updated", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteIncome(String incomeID, String jwt){
        Call<ResponseBody> callDeleteIncome = api.deleteIncome(jwt, incomeID);

        callDeleteIncome.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Income deleted", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }



}