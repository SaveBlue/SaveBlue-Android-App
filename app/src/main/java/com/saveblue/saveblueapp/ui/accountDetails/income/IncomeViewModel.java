package com.saveblue.saveblueapp.ui.accountDetails.income;

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
import com.saveblue.saveblueapp.models.Income;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomeViewModel extends AndroidViewModel {

    private final SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private MutableLiveData<List<Income>> incomeList;

    public IncomeViewModel(@NonNull Application application) {
        super(application);
    }

    // Return the live data list of all incomes
    public LiveData<List<Income>> getIncomes(String accountID, String jwt) {
        if (incomeList == null) {
            incomeList = new MutableLiveData<>();

        }
        callApiGetIncomes(accountID, jwt);

        return incomeList;
    }

    // Api call to get user's account incomes
    private void callApiGetIncomes(String id, String jwt) {

        Call<List<Income>> callAsync = api.getAccountsIncomes(jwt, id);

        callAsync.enqueue(new Callback<List<Income>>() {

            @Override
            public void onResponse(@NotNull Call<List<Income>> call, @NotNull Response<List<Income>> response) {

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
                if(response.code() == 404) {
                    incomeList.setValue(new ArrayList<>());
                    return;
                }

                // On success set the fetched incomes list
                incomeList.setValue(response.body());


            }

            @Override
            public void onFailure(@NotNull Call<List<Income>> call, @NotNull Throwable t) {

                Toast.makeText(getApplication(), getApplication().getString(R.string.serverMessage), Toast.LENGTH_LONG).show();
            }
        });
    }

}
