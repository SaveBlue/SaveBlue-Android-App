package com.saveblue.saveblueapp.ui.accountDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.User;
import com.saveblue.saveblueapp.ui.accountDetails.overview.DeleteAccountDialog;
import com.saveblue.saveblueapp.ui.dashboard.DashboardActivity;
import com.saveblue.saveblueapp.ui.dashboard.overview.AddAccountDialog;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewFragment;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccountActivity extends AppCompatActivity implements DeleteAccountDialog.DeleteAccountListener {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private JwtHandler jwtHandler;

    private Toolbar toolbar;
    private EditText accountName;
    private EditText startOfMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        jwtHandler = new JwtHandler(getApplicationContext());

        initUI();
    }

    private void initUI(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Account");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }

        accountName = findViewById(R.id.inputField1);
        startOfMonth = findViewById(R.id.inputField2);

        Button button = findViewById(R.id.confirmButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                callApiUpdateAccount(new Account(accountName.getText().toString(), Integer.parseInt(String.valueOf(startOfMonth.getText()))));
            }
        });

        Button deleteButton = findViewById(R.id.DeleteButton);
        deleteButton.setOnClickListener(v -> showDeleteAccountDialog());

        callApiGetAccount(getIntent().getStringExtra("accountID"),jwtHandler.getJwt());

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up

        return false;
    }


    //delete dialog handle
    @Override
    public void deleteAccountConfirm(){
        callApiDeleteAccount();
    }


    public void showDeleteAccountDialog(){
        DeleteAccountDialog deleteAccountDialog = new DeleteAccountDialog();
        deleteAccountDialog.show(getSupportFragmentManager(), "delete dialog");
    }



    // --------------------------------------------------------
    // API calls
    // ---------------------------------------------------------

    private void callApiUpdateAccount(Account account) {
        String accountID = getIntent().getStringExtra("accountID");
        Call<ResponseBody> callUpdateAccount = api.editAccount(jwtHandler.getJwt(), accountID, account);

        callUpdateAccount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Account data updated", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // async api call to get user's account incomes
    private void callApiGetAccount(String id, String jwt) {
        Call<Account> callAsync = api.getAccount(jwt, id);

        callAsync.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(@NotNull Call<Account> call, @NotNull Response<Account> response) {
                // if request was denied, ignore call not found
                if (!response.isSuccessful()) {
                    //Toast.makeText((), "Request Error", Toast.LENGTH_LONG).show();
                    System.out.println("Request Error");

                    //logout if jwt in not valid any more
                    Logout.logout(getApplication().getApplicationContext());
                    return;
                }

                Account receivedAccount = response.body();

                // on success set the fetched account data
                assert receivedAccount != null;
                toolbar.setTitle("Edit Account: " + receivedAccount.getName());
                accountName.setText(receivedAccount.getName());
                startOfMonth.setText(String.valueOf(receivedAccount.getStartOfMonth()));



            }

            @Override
            public void onFailure(@NotNull Call<Account> call, @NotNull Throwable t) {
                //Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
                System.out.println("No Network Connectivity!");
            }
        });
    }

    private void callApiDeleteAccount() {
        String accountID = getIntent().getStringExtra("accountID");
        Call<ResponseBody> callUpdateAccount = api.deleteAccount(jwtHandler.getJwt(), accountID);

        callUpdateAccount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display toast and close activity
                Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                Intent deletedAccountIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                deletedAccountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(deletedAccountIntent);


            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}