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
import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.TimestampHandler;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Expense;
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

public class AddExpenseActivity extends AppCompatActivity {

    private final SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private JwtHandler jwtHandler;

    private OverviewViewModel overviewViewModel;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private ArrayAdapter<String> spinnerCategoryAdapter1;
    private ArrayAdapter<String> spinnerCategoryAdapter2;
    private String setSpinner2Flag = "";

    private List<Account> accountList = new ArrayList<>();
    private final List<String> accountListNames = new ArrayList<>();

    private String expenseID;
    private String task;

    // UI elements for easier work
    private EditText editTextAmount;
    private TextView textDate;

    private Spinner spinnerAccount;
    private Spinner catSpinner1;
    private Spinner catSpinner2;
    private EditText descriptionEditText;
    private TextInputLayout amountLayout;
    private TextView catSpinner1Error;


    // differentiate between adding and editing expense + setup viewmodel
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

    // initialize general ui elements
    private void initUIElements() {
        // Set UI elements
        spinnerAccount = findViewById(R.id.spinnerAccountAddExpense);
        catSpinner1 = findViewById(R.id.catSpinner1);
        catSpinner2 = findViewById(R.id.catSpinner2);
        catSpinner1Error = findViewById(R.id.catSpinner1Error);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        textDate = findViewById(R.id.date);
        editTextAmount = findViewById(R.id.amount);
        // Text field limiting of decimal size
        editTextAmount.setFilters(new InputFilter[] {new DigitsInputFilter(7,2)});
        amountLayout = findViewById(R.id.amountLayout);


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
        dateButton.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "Select date of Expense"));

        initCategorySelector();
    }

    // initialize category selectors + fill them up according to right category
    private void initCategorySelector() {
        Resources res = getResources();
        String[] categories1 = res.getStringArray(R.array.categoriesE1);

        spinnerCategoryAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_menu_item, categories1);
        spinnerCategoryAdapter1.setDropDownViewResource(R.layout.dropdown_menu_item);
        catSpinner1.setAdapter(spinnerCategoryAdapter1);


        catSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] categories2 = res.getStringArray(R.array.categoriesE2);

                catSpinner2.setEnabled(true);

                switch (position) {
                    case 0:
                        catSpinner2.setEnabled(false);
                        break;

                    case 1:
                        categories2 = res.getStringArray(R.array.categoriesE21);
                        break;

                    case 2:
                        categories2 = res.getStringArray(R.array.categoriesE22);
                        break;

                    case 3:
                        categories2 = res.getStringArray(R.array.categoriesE23);
                        break;

                    case 4:
                        categories2 = res.getStringArray(R.array.categoriesE24);
                        break;

                    case 5:
                        categories2 = res.getStringArray(R.array.categoriesE25);
                        break;

                    case 6:
                        categories2 = res.getStringArray(R.array.categoriesE26);
                        break;
                }

                // clear spinner error message
                if (position > 0)
                    catSpinner1Error.setVisibility(View.GONE);

                spinnerCategoryAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_menu_item, categories2);
                spinnerCategoryAdapter2.setDropDownViewResource(R.layout.dropdown_menu_item);
                catSpinner2.setAdapter(spinnerCategoryAdapter2);

                // required to set spinner 2 for edit task
                if (task.equals("EDIT") && !setSpinner2Flag.equals("")) {
                    catSpinner2.setSelection(Arrays.asList(categories2).indexOf(setSpinner2Flag));
                    setSpinner2Flag = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // initialize ui elements for adding expense
    private void initUIAdd() {
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.addExpenseTitle));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button buttonAddExpense = findViewById(R.id.buttonAddExpense);

        // Set onClickListeners
        buttonAddExpense.setOnClickListener(v -> {
                    if (handleInputFields()) {
                        String jwt = jwtHandler.getJwt();
                        String userId = jwtHandler.getId();
                        String accountId = (accountList.get((int) spinnerAccount.getSelectedItemId()).getId());

                        String description = descriptionEditText.getText().length() == 0 ? "" : descriptionEditText.getText().toString();
                        String date2Api = TimestampHandler.parse2Mongo(textDate.getText().toString());
                        String cat1 = catSpinner1.getSelectedItem().toString();
                        String cat2 = catSpinner2.getSelectedItem().toString();

                        Expense newExpense = new Expense(accountId, userId, description, date2Api, Float.parseFloat(editTextAmount.getText().toString()), cat1, cat2);
                        addExpense(newExpense, jwt);
                    }

                }
        );
    }

    // initialize ui elements for editing expense
    private void initUIEdit() {
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.editExpenseTitle));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }

        // setup buttons
        Button buttonEditExpense = findViewById(R.id.buttonAddExpense);
        buttonEditExpense.setText(getString(R.string.updateExpenseText));

        Button buttonDeleteExpense = findViewById(R.id.buttonDeleteExpense);
        buttonDeleteExpense.setVisibility(View.VISIBLE);

        // Set onClickListeners
        buttonEditExpense.setOnClickListener(v -> {
                    if (handleInputFields()) {
                        String userId = jwtHandler.getId();
                        String accountId = (accountList.get((int) spinnerAccount.getSelectedItemId()).getId());

                        String date2Api = TimestampHandler.parse2Mongo(textDate.getText().toString());
                        String description = descriptionEditText.getText().length() == 0 ? "" : descriptionEditText.getText().toString();
                        String cat1 = catSpinner1.getSelectedItem().toString();
                        String cat2 = catSpinner2.getSelectedItem().toString();

                        Expense editedExpense = new Expense(accountId, userId, description, date2Api, Float.parseFloat(editTextAmount.getText().toString()), cat1, cat2);
                        updateExpense(expenseID, editedExpense, jwtHandler.getJwt());
                    }
                }
        );

        buttonDeleteExpense.setOnClickListener(v -> deleteExpense(expenseID, jwtHandler.getJwt()));

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
    public void setSpinnersToRightCategory(String cat1, String cat2) {
        final ArrayList<String> cat1Strings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categoriesE1)));
        catSpinner1.setSelection(cat1Strings.indexOf(cat1));
        setSpinner2Flag = cat2;
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
                getExpense(expenseID, jwtHandler.getJwt());
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

    // api call for adding new expense
    private void addExpense(Expense newExpense, String jwt) {

        Call<ResponseBody> callAddExpense = api.addExpense(jwt, newExpense);

        callAddExpense.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {

                // JWT expired
                if (response.code() == 401) {
                    Logout.logout(getApplication().getApplicationContext(), 1);
                    return;
                }

                // Other Error
                if (!response.isSuccessful() && response.code() != 404) {
                    Toast.makeText(getApplication(), getApplication().getString(R.string.serverErrorMessage), Toast.LENGTH_LONG).show();
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

    // api call for fetching expenses
    private void getExpense(String expenseID, String jwt) {

        Call<Expense> callGetExpense = api.getExpense(jwt, expenseID);

        callGetExpense.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(@NotNull Call<Expense> call, @NotNull Response<Expense> response) {

                // JWT expired
                if (response.code() == 401) {
                    Logout.logout(getApplication().getApplicationContext(), 1);
                    return;
                }

                // Other Error
                if (!response.isSuccessful() && response.code() != 404) {
                    Toast.makeText(getApplication(), getApplication().getString(R.string.serverErrorMessage), Toast.LENGTH_LONG).show();
                    return;
                }

                Expense expense = response.body();
                assert expense != null;

                // fill ui from fetched expense
                descriptionEditText.setText(expense.getDescription());
                editTextAmount.setText(String.valueOf(expense.getAmount()));
                textDate.setText(TimestampHandler.parseMongoTimestamp(expense.getDate()));

                setSpinnerToRightAccount(expense.getAccountID());
                setSpinnersToRightCategory(expense.getCategory1(), expense.getCategory2());
            }

            @Override
            public void onFailure(@NotNull Call<Expense> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    // api call for updating expense
    private void updateExpense(String expenseID, Expense expense, String jwt) {
        Call<ResponseBody> callUpdateExpense = api.editExpense(jwt, expenseID, expense);

        callUpdateExpense.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {

                // JWT expired
                if (response.code() == 401) {
                    Logout.logout(getApplication().getApplicationContext(), 1);
                    return;
                }

                // Other Error
                if (!response.isSuccessful() && response.code() != 404) {
                    Toast.makeText(getApplication(), getApplication().getString(R.string.serverErrorMessage), Toast.LENGTH_LONG).show();
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

    // api call for deleting expense
    private void deleteExpense(String expenseID, String jwt) {
        Call<ResponseBody> callDeleteExpense = api.deleteExpense(jwt, expenseID);

        callDeleteExpense.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {

                // JWT expired
                if (response.code() == 401) {
                    Logout.logout(getApplication().getApplicationContext(), 1);
                    return;
                }

                // Other Error
                if (!response.isSuccessful() && response.code() != 404) {
                    Toast.makeText(getApplication(), getApplication().getString(R.string.serverErrorMessage), Toast.LENGTH_LONG).show();
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


