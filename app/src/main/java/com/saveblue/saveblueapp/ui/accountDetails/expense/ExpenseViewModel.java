package com.saveblue.saveblueapp.ui.accountDetails.expense;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Expense;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseViewModel extends AndroidViewModel {

    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private MutableLiveData<List<Expense>> expenseList;


    public ExpenseViewModel(@NonNull Application application) {
        super(application);
    }

    // returns the live data list of all accounts
    public LiveData<List<Expense>> getExpenses(String accountID, String jwt) {
        if (expenseList == null) {
            expenseList = new MutableLiveData<>();
            callApiGetExpenses(accountID, jwt);
        }

        return expenseList;
    }

    // async api call to get user's accounts
    private void callApiGetExpenses(String id, String jwt) {
        Call<List<Expense>> callAsync = api.getAccountsExpenses(jwt, id);

        callAsync.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                // if request was denied, ignore call not found
                if (!response.isSuccessful() && response.code() != 404) {
                    //Toast.makeText((), "Request Error", Toast.LENGTH_LONG).show();
                    System.out.println("Request Error");

                    //logout if jwt in not valid any more
                    Logout.logout(getApplication().getApplicationContext());
                    return;
                }

                // skip setting list if call not found
                if(response.code() == 404)
                    return;

                // on success set the fetched account list
                expenseList.setValue(response.body());


            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
                System.out.println("No Network Connectivity!");
            }
        });
    }


}