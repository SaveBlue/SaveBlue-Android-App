package com.saveblue.saveblueapp.ui.dashboard.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private MutableLiveData<User> userMutableLiveData;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    // returns the live data with an object containing user's data
    public LiveData<User> getUser(String id, String jwt) {
        if(userMutableLiveData == null){
            userMutableLiveData = new MutableLiveData<>();
            callApiUser(id, jwt);
        }

        return userMutableLiveData;
    }

    // async api call to get user's data
    private void callApiUser(String id, String jwt) {
        Call<User> callAsync = api.getUserData(jwt, id);

        callAsync.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // if request was denied
                if (!response.isSuccessful()) {
                    //Toast.makeText((), "Request Error", Toast.LENGTH_LONG).show();
                    System.out.println("Request Error");

                    //logout if jwt in not valid any more
                    Logout.logout(getApplication().getApplicationContext());

                    return;
                }

                // on success set the fetched user's data object
                userMutableLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "No Network Connectivity!", Toast.LENGTH_LONG).show();
                System.out.println("No Network Connectivity!");
            }
        });
    }
}