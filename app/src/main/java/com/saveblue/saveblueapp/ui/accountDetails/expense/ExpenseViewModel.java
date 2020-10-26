package com.saveblue.saveblueapp.ui.accountDetails.expense;

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
import com.saveblue.saveblueapp.models.Expense;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseViewModel extends AndroidViewModel {

    private final SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private MutableLiveData<List<Expense>> expenseList;


    public ExpenseViewModel(@NonNull Application application) {
        super(application);
    }

    // returns the live data list of all accounts
    public LiveData<List<Expense>> getExpenses(String accountID, String jwt) {
        if (expenseList == null) {
            expenseList = new MutableLiveData<>();
        }

        callApiGetExpenses(accountID, jwt);

        return expenseList;
    }

    // async api call to get user's accounts
    private void callApiGetExpenses(String id, String jwt) {
        Call<List<Expense>> callAsync = api.getAccountsExpenses(jwt, id);

        callAsync.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(@NotNull Call<List<Expense>> call, @NotNull Response<List<Expense>> response) {

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

                // if call not found set empty list
                if (response.code() == 404) {
                    expenseList.setValue(new ArrayList<>());
                    return;
                }

                // on success set the fetched account list
                expenseList.setValue(response.body());


            }

            @Override
            public void onFailure(@NotNull Call<List<Expense>> call, @NotNull Throwable t) {
                //Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
                System.out.println("No Network Connectivity!");
            }
        });
    }


}
