package com.saveblue.saveblueapp.ui.dashboard.overview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewViewModel extends ViewModel {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    private MutableLiveData<List<Account>> accountList;

    // returns the live data list of all accounts
    public LiveData<List<Account>> getAccounts(String id, String jwt) {
        if (accountList == null) {
            accountList = new MutableLiveData<List<Account>>();
            callApiAccounts(id, jwt);
        }

        return accountList;
    }

    // async api call to get user's accounts
    private void callApiAccounts(String id, String jwt) {
        Call<List<Account>> callAsync = api.getUsersAccounts(jwt, id);

        callAsync.enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                // if request was denied
                if (!response.isSuccessful()) {
                    //Toast.makeText((), "Request Error", Toast.LENGTH_LONG).show();
                    System.out.println("Request Error");
                    return;
                }

                // on success set the fetched account list
                accountList.setValue(response.body());

            }

            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
                System.out.println("No Network Connectivity!");
            }
        });
    }


}