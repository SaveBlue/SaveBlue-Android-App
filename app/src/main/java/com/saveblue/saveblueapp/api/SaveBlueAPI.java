package com.saveblue.saveblueapp.api;

import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.AuthUser;
import com.saveblue.saveblueapp.models.JWT;
import com.saveblue.saveblueapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SaveBlueAPI {

    // Authentication API calls

    @POST("auth/login")
    Call<JWT>loginUser(
            @Body AuthUser authUser);


    // User API calls

    @GET("users/{id}")
    Call<User> getUserData(
            @Header("x-access-token") String jwt,
            @Path("id") String userID);

    // Account API calls

    @GET("accounts/{uid}")
    Call<List<Account>> getUsersAccounts(
            @Header("x-access-token") String jwt,
            @Path("uid") String userID);
}
