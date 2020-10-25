package com.saveblue.saveblueapp.ui.addExpenseIncome;

import android.content.Intent;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.DigitsInputFilter;
import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.TimestampHandler;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Income;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddIncomeActivity extends AppCompatActivity {

    private final SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private JwtHandler jwtHandler;

    private OverviewViewModel overviewViewModel;
    private ArrayAdapter<String> spinnerArrayAdapter;

    private List<Account> accountList = new ArrayList<>();
    private final List<String> accountListNames = new ArrayList<>();

    private String incomeID;
    private String task;

    // UI elements for easier work
    private EditText editTextAmount;
    private TextView textDate;

    private Spinner spinnerAccount;
    private Spinner catSpinner1;
    private EditText descriptionEditText;
    private TextInputLayout amountLayout;
    private TextView catSpinner1Error;


    // differentiate between adding and editing income + setup viewmodel
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
        } else {
            incomeID = getIntent().getStringExtra("IncomeID");
            initUIEdit();
        }

        // Get user's accounts
        overviewViewModel = new ViewModelProvider(this).get(OverviewViewModel.class);
        observerSetup();
    }

    // initialize general ui elements
    private void initUIElements() {
        // Set UI elements
        spinnerAccount = findViewById(R.id.spinnerAccountAddIncome);
        catSpinner1 = findViewById(R.id.catSpinner1);
        catSpinner1Error = findViewById(R.id.catSpinner1Error);

        descriptionEditText = findViewById(R.id.descriptionEditText);
        textDate = findViewById(R.id.date);
        editTextAmount = findViewById(R.id.amount);
        amountLayout = findViewById(R.id.amountLayout);
        // Text field limiting of decimal size
        editTextAmount.setFilters(new InputFilter[] {new DigitsInputFilter(7,2)});


        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(editTextAmount.getText()).length() > 0) {
                    amountLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // Populate spinner
        spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, accountListNames);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.dropdown_menu_item);
        spinnerAccount.setAdapter(spinnerArrayAdapter);

        // setup date operations
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        textDate.setText(df.format(Calendar.getInstance().getTime()));

        MaterialDatePicker.Builder<Long> dateBuilder = MaterialDatePicker.Builder.datePicker();
        dateBuilder.setTitleText(getString(R.string.datePickerTitle));
        MaterialDatePicker<Long> datePicker = dateBuilder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> textDate.setText(df.format(new Date(selection))));

        // display date picker on icon click
        Button dateButton = findViewById(R.id.dateButton);
        dateButton.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "Select date of Income"));

        initCategorySelector();
    }

    // initialize category selectors + fill them up according to right category
    private void initCategorySelector() {
        Resources res = getResources();
        String[] categories1 = res.getStringArray(R.array.categoriesI1);

        ArrayAdapter<String> spinnerCategoryAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_menu_item, categories1);
        spinnerCategoryAdapter1.setDropDownViewResource(R.layout.dropdown_menu_item);
        catSpinner1.setAdapter(spinnerCategoryAdapter1);

        catSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // clear spinner error message
                if (position > 0)
                    catSpinner1Error.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // initialize ui elements for adding income
    private void initUIAdd() {
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.addIncomeTitle));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button buttonAddIncome = findViewById(R.id.buttonAddIncome);

        // Set onClickListeners
        buttonAddIncome.setOnClickListener(v -> {
                    if (handleInputFields()) {
                        String jwt = jwtHandler.getJwt();
                        String userId = jwtHandler.getId();
                        String accountId = (accountList.get((int) spinnerAccount.getSelectedItemId()).getId());

                        String description = descriptionEditText.getText().length() == 0 ? "" : descriptionEditText.getText().toString();
                        String date2Api = TimestampHandler.parse2Mongo(textDate.getText().toString());
                        String cat1 = catSpinner1.getSelectedItem().toString();

                        Income newIncome = new Income(accountId, userId, description, date2Api, Float.parseFloat(editTextAmount.getText().toString()), cat1);
                        addIncome(newIncome, jwt);
                    }

                }
        );
    }

    // initialize ui elements for editing income
    private void initUIEdit() {
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.editIncomeTitle));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }

        // setup buttons
        Button buttonEditIncome = findViewById(R.id.buttonAddIncome);
        buttonEditIncome.setText(getString(R.string.updateIncomeText));

        Button buttonDeleteIncome = findViewById(R.id.buttonDeleteIncome);
        buttonDeleteIncome.setVisibility(View.VISIBLE);

        // Set onClickListeners
        buttonEditIncome.setOnClickListener(v -> {
                    if (handleInputFields()) {
                        String userId = jwtHandler.getId();
                        String accountId = (accountList.get((int) spinnerAccount.getSelectedItemId()).getId());

                        String date2Api = TimestampHandler.parse2Mongo(textDate.getText().toString());
                        String description = descriptionEditText.getText().length() == 0 ? "" : descriptionEditText.getText().toString();
                        String cat1 = catSpinner1.getSelectedItem().toString();

                        Income editedIncome = new Income(accountId, userId, description, date2Api, Float.parseFloat(editTextAmount.getText().toString()), cat1);
                        updateIncome(incomeID, editedIncome, jwtHandler.getJwt());
                    }
                }
        );

        buttonDeleteIncome.setOnClickListener(v -> deleteIncome(incomeID, jwtHandler.getJwt()));

    }

    // handles input field correctness
    private boolean handleInputFields() {
        boolean detectedError = false;

        if (Objects.requireNonNull(editTextAmount.getText()).length() == 0) {
            amountLayout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        if (catSpinner1.getSelectedItemId() == 0) {
            catSpinner1Error.setVisibility(View.VISIBLE);
            detectedError = true;
        }

        return !detectedError;
    }

    // on edit task set account spinner to right account
    public void setSpinnerToRightAccount(String baseAccountID) {
        for (int i = 0; i < accountList.size(); i++) {
            if (accountList.get(i).getId().equals(baseAccountID)) {
                spinnerAccount.setSelection(i);
            }
        }
    }

    //sets both spinners to the right category
    public void setSpinnersToRightCategory(String cat1) {
        final ArrayList<String> cat1Strings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categoriesI1)));
        catSpinner1.setSelection(cat1Strings.indexOf(cat1));
    }


    // setup observer for fetching account list
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
                getIncome(incomeID, jwtHandler.getJwt());
            }

        });
    }

    // handle back button press
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

    // api call for adding new editedIncome
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

    // api call for fetching income
    private void getIncome(String incomeID, String jwt) {

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

                // fill ui from fetched income
                descriptionEditText.setText(income.getDescription());
                editTextAmount.setText(String.valueOf(income.getAmount()));
                textDate.setText(TimestampHandler.parseMongoTimestamp(income.getDate()));

                setSpinnerToRightAccount(income.getAccountID());
                setSpinnersToRightCategory(income.getCategory1());
            }

            @Override
            public void onFailure(@NotNull Call<Income> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    // api call for updating income
    private void updateIncome(String incomeID, Income income, String jwt) {
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

    // api call for deleting income
    private void deleteIncome(String incomeID, String jwt) {
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


