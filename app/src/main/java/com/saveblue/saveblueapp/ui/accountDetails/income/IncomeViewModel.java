package com.saveblue.saveblueapp.ui.accountDetails.income;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Expense;
import com.saveblue.saveblueapp.models.Income;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomeViewModel extends AndroidViewModel {

    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private MutableLiveData<List<Income>> incomeList;


    public IncomeViewModel(@NonNull Application application) {
        super(application);
    }

    // returns the live data list of all incomes
    public LiveData<List<Income>> getIncomes(String accountID, String jwt) {
        if (incomeList == null) {
            incomeList = new MutableLiveData<>();

        }
        callApiGetIncomes(accountID, jwt);

        return incomeList;
    }

    // async api call to get user's account incomes
    private void callApiGetIncomes(String id, String jwt) {
        Call<List<Income>> callAsync = api.getAccountsIncomes(jwt, id);

        callAsync.enqueue(new Callback<List<Income>>() {
            @Override
            public void onResponse(Call<List<Income>> call, Response<List<Income>> response) {
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

                // on success set the fetched incomes list
                incomeList.setValue(response.body());


            }

            @Override
            public void onFailure(Call<List<Income>> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
                System.out.println("No Network Connectivity!");
            }
        });
    }

}
