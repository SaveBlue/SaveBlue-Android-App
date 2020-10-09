package com.saveblue.saveblueapp.ui.addExpenseIncome;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Expense;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    EditText editTextAmount;
    TextView textDate;

    Spinner spinnerAccount;
    EditText editTextNameAddExpense;
    EditText editTextDescriptionAddExpense;


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
        } else {
            expenseID = getIntent().getStringExtra("ExpenseID");
            initUIEdit();
        }

        // Get user's accounts
        overviewViewModel = new ViewModelProvider(this).get(OverviewViewModel.class);
        observerSetup();
    }

    private void initUIElements() {
        // Set UI elements
        spinnerAccount = findViewById(R.id.spinnerAccountAddExpense);

        // TODO fix
        editTextNameAddExpense = findViewById(R.id.editTextNameAddIncome);
        editTextDescriptionAddExpense = findViewById(R.id.editTextDescriptionAddIncome);

        textDate = findViewById(R.id.date);
        editTextAmount = findViewById(R.id.amount);

        // Populate spinner
        spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountListNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccount.setAdapter(spinnerArrayAdapter);

        // setup date operations
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        textDate.setText(df.format(Calendar.getInstance().getTime()));

        MaterialDatePicker.Builder<Long> dateBuilder = MaterialDatePicker.Builder.datePicker();
        dateBuilder.setTitleText("Select Date");
        MaterialDatePicker<Long> datePicker = dateBuilder.build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                textDate.setText(df.format(new Date(selection)));
            }
        });

        // display date picker on icon click
        Button dateButton = findViewById(R.id.dateButton);
        dateButton.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "Select date of Expense"));
    }

    private void initUIAdd() {
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
                    String accountId = (accountList.get((int) spinnerAccount.getSelectedItemId()).getId());

                    //TODO pohendli polja za vnos v newExpense
                    String date2Api = new Date(textDate.getText().toString()).toString();

                    Expense newExpense = new Expense(accountId, userId, editTextNameAddExpense.getText().toString(), editTextDescriptionAddExpense.getText().toString(), date2Api, Float.parseFloat(editTextAmount.getText().toString()));
                    addExpense(newExpense, jwt);
                }
        );
    }

    private void initUIEdit() {
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Update Expense");
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
                    String accountId = (accountList.get((int) spinnerAccount.getSelectedItemId()).getId());

                    //TODO pohendli polja za vnos v newExpense

            Expense editedExpense = new Expense(accountId, userId, editTextNameAddExpense.getText().toString(), editTextDescriptionAddExpense.getText().toString(), textDate.getText().toString(), Float.parseFloat(editTextAmount.getText().toString()));
                    updateExpense(expenseID, editedExpense, jwtHandler.getJwt());
                }
        );

        buttonDeleteExpense.setOnClickListener(v -> deleteExpense(expenseID, jwtHandler.getJwt()));

    }

    public void setSpinnerToRightAccount(String baseAccountID) {
        for (int i = 0; i < accountList.size(); i++) {
            if (accountList.get(i).getId().equals(baseAccountID)) {
                spinnerAccount.setSelection(i);
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
            if (Objects.equals(getIntent().getStringExtra("CallingActivity"), "Details") && task.equals("ADD")) {
                setSpinnerToRightAccount(getIntent().getStringExtra("BaseAccountID"));
            }

            if (task.equals("EDIT")) {
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

    public String parseMongoTimestamp(String timestamp){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" , Locale.getDefault());
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy" , Locale.getDefault());
        try {
            Date mongoDate = df.parse(timestamp);

            return df2.format(mongoDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "No Date";
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

    private void getExpense(String expenseID, String jwt) {
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
                editTextAmount.setText(String.valueOf(expense.getAmount()));

                textDate.setText(parseMongoTimestamp(expense.getDate()));

                setSpinnerToRightAccount(expense.getAccountID());

            }

            @Override
            public void onFailure(@NotNull Call<Expense> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Errorr", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void updateExpense(String expenseID, Expense expense, String jwt) {
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

    private void deleteExpense(String expenseID, String jwt) {
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


