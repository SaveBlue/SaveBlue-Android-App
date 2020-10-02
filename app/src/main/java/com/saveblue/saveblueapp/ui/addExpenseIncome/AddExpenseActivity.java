package com.saveblue.saveblueapp.ui.addExpenseIncome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Expense;
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

public class AddExpenseActivity extends AppCompatActivity {

    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private JwtHandler jwtHandler;

    private OverviewViewModel overviewViewModel;
    private ArrayAdapter<String> spinnerArrayAdapter;

    private List<Account> accountList = new ArrayList<>();
    private List<String> accountListNames = new ArrayList<>();

    private String expenseID;
    String task;

    // UI elements for easier work
    Spinner spinnerAccountExpenseAdd;
    EditText editTextNameAddExpense;
    EditText editTextDescriptionAddExpense;
    EditText editTextDateAddExpense;
    EditText editTextAmountAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

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
            expenseID = getIntent().getStringExtra("ExpenseID");
            initUIEdit();
        }

        // Get user's accounts
        overviewViewModel = new ViewModelProvider(this).get(OverviewViewModel.class);
        observerSetup();
    }

    private void initUIElements(){
        // Set UI elements
        spinnerAccountExpenseAdd = findViewById(R.id.spinnerAccountAddExpense);
        editTextNameAddExpense = findViewById(R.id.editTextNameAddExpense);
        editTextDescriptionAddExpense = findViewById(R.id.editTextDescriptionAddExpense);
        editTextDateAddExpense = findViewById(R.id.editTextDateAddExpense);
        editTextAmountAddExpense = findViewById(R.id.editTextAmountAddExpense);

        // Populate spinner
        spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountListNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountExpenseAdd.setAdapter(spinnerArrayAdapter);
    }

    private void initUIAdd(){
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Expense");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button buttonAddExpense = findViewById(R.id.buttonAddExpense);

        // Set onClickListeners
        buttonAddExpense.setOnClickListener(v -> {
                    String jwt = jwtHandler.getJwt();
                    String userId = jwtHandler.getId();
                    String accountId = (accountList.get((int) spinnerAccountExpenseAdd.getSelectedItemId()).getId());

                    //TODO pohendli polja za vnos v newExpense

                    Expense newExpense = new Expense(accountId, userId, editTextNameAddExpense.getText().toString(), editTextDescriptionAddExpense.getText().toString(), editTextDateAddExpense.getText().toString(), Float.parseFloat(editTextAmountAddExpense.getText().toString()));
                    addExpense(newExpense, jwt);
                }
        );
    }

    private void initUIEdit(){
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Expense");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }

        // setup buttons
        Button buttonEditExpense = findViewById(R.id.buttonAddExpense);
        buttonEditExpense.setText("Edit Expense");

        Button buttonDeleteExpense = findViewById(R.id.buttonDeleteExpense);
        buttonDeleteExpense.setVisibility(View.VISIBLE);

        // Set onClickListeners
        buttonEditExpense.setOnClickListener(v -> {
                    String userId = jwtHandler.getId();
                    String accountId = (accountList.get((int) spinnerAccountExpenseAdd.getSelectedItemId()).getId());

                    //TODO pohendli polja za vnos v newExpense

            Expense editedExpense = new Expense(accountId, userId, editTextNameAddExpense.getText().toString(), editTextDescriptionAddExpense.getText().toString(), editTextDateAddExpense.getText().toString(), Float.parseFloat(editTextAmountAddExpense.getText().toString()));
                    updateExpense(expenseID, editedExpense, jwtHandler.getJwt());
                }
        );

        buttonDeleteExpense.setOnClickListener(v -> deleteExpense(expenseID, jwtHandler.getJwt()));

    }

    public void setSpinnerToRightAccount(String baseAccountID){
        for (int i = 0; i < accountList.size(); i++) {
            if(accountList.get(i).getId().equals(baseAccountID)){
                spinnerAccountExpenseAdd.setSelection(i);
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
                getExpense(expenseID, jwtHandler.getJwt());
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

    private void addExpense(Expense newExpense, String jwt) {

        Call<ResponseBody> callAddExpense = api.addExpense(jwt, newExpense);

        callAddExpense.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Expense added", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getExpense(String expenseID, String jwt){
        System.out.println("----------------------------------");

        Call<Expense> callGetExpense = api.getExpense(jwt, expenseID);

        callGetExpense.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(@NotNull Call<Expense> call, @NotNull Response<Expense> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }
                Expense expense = response.body();
                assert expense != null;

                //fill UI elements from fetched income;
                editTextNameAddExpense.setText(expense.getName());
                editTextDescriptionAddExpense.setText(expense.getDescription());
                editTextDateAddExpense.setText(expense.getDate());
                editTextAmountAddExpense.setText(String.valueOf(expense.getAmount()));

                setSpinnerToRightAccount(expense.getAccountID());

            }

            @Override
            public void onFailure(@NotNull Call<Expense> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Errorr", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void updateExpense(String expenseID, Expense expense, String jwt){
        Call<ResponseBody> callUpdateExpense = api.editExpense(jwt, expenseID, expense);

        callUpdateExpense.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Expense updated", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteExpense(String expenseID, String jwt){
        Call<ResponseBody> callDeleteExpense = api.deleteExpense(jwt, expenseID);

        callDeleteExpense.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}