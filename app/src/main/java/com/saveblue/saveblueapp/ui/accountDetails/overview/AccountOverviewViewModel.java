package com.saveblue.saveblueapp.ui.accountDetails.overview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Income;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountOverviewViewModel extends AndroidViewModel {

    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private MutableLiveData<Account> account;


    public AccountOverviewViewModel(@NonNull Application application) {
        super(application);
    }

    // returns the live data of the account
    public LiveData<Account> getAccount(String accountID, String jwt) {
        if (account == null) {
            account = new MutableLiveData<>();

        }
        callApiGetAccount(accountID, jwt);

        return account;
    }

    // async api call to get user's account incomes
    private void callApiGetAccount(String id, String jwt) {
        Call<Account> callAsync = api.getAccount(jwt, id);

        callAsync.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                // if request was denied, ignore call not found
                if (!response.isSuccessful() && response.code() != 404) {
                    //Toast.makeText((), "Request Error", Toast.LENGTH_LONG).show();
                    System.out.println("Request Error");

                    //logout if jwt in not valid any more
                    Logout.logout(getApplication().getApplicationContext());
                    return;
                }

                // if call not found set empty list
                if(response.code() == 404) {
                    account.setValue(new Account("",1));
                    return;
                }

                // on success set the fetched incomes list
                account.setValue(response.body());


            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
                System.out.println("No Network Connectivity!");
            }
        });
    }
}
