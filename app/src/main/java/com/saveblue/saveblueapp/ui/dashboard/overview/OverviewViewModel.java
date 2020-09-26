package com.saveblue.saveblueapp.ui.dashboard.overview;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewViewModel extends AndroidViewModel {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);

    private MutableLiveData<List<Account>> accountList;

    public OverviewViewModel(@NonNull Application application) {
        super(application);
    }

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

                    //logout if jwt in not valid any more
                    Logout.logout(getApplication().getApplicationContext());
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


    public void addNewAccount(String jwt, String id, Account account){

        Call<List<Account>> callAddNewAccount = api.addNewAccount(jwt, id, account);

        callAddNewAccount.enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                // if request was denied
                if (!response.isSuccessful()) {
                    //Toast.makeText((), "Request Error", Toast.LENGTH_LONG).show();
                    System.out.println("Request Error");

                    //logout if jwt in not valid any more
                    Logout.logout(getApplication().getApplicationContext());
                    return;
                }

                accountList.setValue(response.body());

            }

            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "Other Error", Toast.LENGTH_SHORT).show();
                System.out.println("Server unreachable!");
            }
        });
    }


}