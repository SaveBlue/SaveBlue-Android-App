package com.saveblue.saveblueapp.ui.addExpenseIncome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;
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
import com.saveblue.saveblueapp.models.Income;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewViewModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        jwtHandler = new JwtHandler(getApplicationContext());

        initUI();
    }

    private void initUI() {
        // init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Income");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set UI elements
        Spinner spinnerAccountIncomeAdd = findViewById(R.id.spinnerAccountAddIncome);
        EditText editTextNameAddIncome = findViewById(R.id.editTextNameAddIncome);
        EditText editTextDescriptionAddIncome = findViewById(R.id.editTextDescriptionAddIncome);
        EditText editTextDateAddAccount = findViewById(R.id.editTextDateAddAccount);
        EditText editTextAmountAddIncome = findViewById(R.id.editTextAmountAddIncome);
        Button buttonAddIncome = findViewById(R.id.buttonAddIncome);

        // Get user's accounts
        overviewViewModel = new ViewModelProvider(this).get(OverviewViewModel.class);
        observerSetup();

        // Populate spinner
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountListNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountIncomeAdd.setAdapter(spinnerArrayAdapter);


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}