package com.saveblue.saveblueapp.api;

import com.saveblue.saveblueapp.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface SaveBlueAPI {

    @GET("users/{id}")
    Call<User> getUserData(
            @Header("x-access-token") String jwt,
            @Path("id") String userID);
}
