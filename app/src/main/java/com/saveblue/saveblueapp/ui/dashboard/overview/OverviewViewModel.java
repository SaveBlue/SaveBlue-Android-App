package com.saveblue.saveblueapp.ui.dashboard.overview;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewViewModel extends AndroidViewModel {
    private final SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    private MutableLiveData<List<Account>> accountList;

    public OverviewViewModel(@NonNull Application application) {
        super(application);
    }

    // returns the live data list of all accounts
    public LiveData<List<Account>> getAccounts(String id, String jwt) {
        if (accountList == null) {
            accountList = new MutableLiveData<>();
        }
        callApiAccounts(id, jwt);

        return accountList;
    }

    // async api call to get user's accounts
    private void callApiAccounts(String id, String jwt) {
        Call<List<Account>> callAsync = api.getUsersAccounts(jwt, id);

        callAsync.enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(@NotNull Call<List<Account>> call, @NotNull Response<List<Account>> response) {

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

                // on success set the fetched account list
                accountList.setValue(response.body());

            }

            @Override
            public void onFailure(@NotNull Call<List<Account>> call, @NotNull Throwable t) {
                //Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
                System.out.println("No Network Connectivity!");
            }
        });
    }


    public void addNewAccount(String jwt, String id, Account account){

        Call<List<Account>> callAddNewAccount = api.addNewAccount(jwt, id, account);

        callAddNewAccount.enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(@NotNull Call<List<Account>> call, @NotNull Response<List<Account>> response) {

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

                accountList.setValue(response.body());

            }

            @Override
            public void onFailure(@NotNull Call<List<Account>> call, @NotNull Throwable t) {
                //Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
                System.out.println("Server unreachable!");
            }
        });
    }


}