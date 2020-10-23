package com.saveblue.saveblueapp.ui.dashboard.profile;

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
import com.saveblue.saveblueapp.models.User;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    private final SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private MutableLiveData<User> userMutableLiveData;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    // returns the live data with an object containing user's data
    public LiveData<User> getUser(String id, String jwt) {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        callApiGetUser(id, jwt);

        return userMutableLiveData;
    }

    // async api call to get user's data
    private void callApiGetUser(String id, String jwt) {
        Call<User> callAsync = api.getUserData(jwt, id);

        callAsync.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                // if request was denied
                if (!response.isSuccessful()) {
                    //logout if jwt in not valid any more
                    Logout.logout(getApplication().getApplicationContext(), 1);

                    return;
                }

                // on success set the fetched user's data object
                userMutableLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Toast.makeText(getApplication().getApplicationContext(), getApplication().getApplicationContext().getString(R.string.serverMessage), Toast.LENGTH_LONG).show();
            }
        });
    }
}