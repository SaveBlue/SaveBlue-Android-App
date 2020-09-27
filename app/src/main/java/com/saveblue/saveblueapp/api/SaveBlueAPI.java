package com.saveblue.saveblueapp.api;

import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Expense;
import com.saveblue.saveblueapp.models.Income;
import com.saveblue.saveblueapp.models.LoginUser;
import com.saveblue.saveblueapp.models.JWT;
import com.saveblue.saveblueapp.models.RegisterUser;
import com.saveblue.saveblueapp.models.User;

import java.util.List;

import okhttp3.ResponseBody;
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
            @Body LoginUser loginUser);

    @POST("auth/register")
    Call<ResponseBody> registerUser(
            @Body RegisterUser registerUser);


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

    @POST("accounts/{uid}")
    Call<List<Account>> addNewAccount(
            @Header("x-access-token") String jwt,
            @Path("uid") String userID,
            @Body Account account);

    // Income API calls

    @POST("incomes/")
    Call<ResponseBody> addIncome(
            @Header("x-access-token") String jwt,
            @Body Income income);


    // Expense API calls
    @GET("expenses/find/{aid}")
    Call<List<Expense>> getAccountsExpenses(
            @Header("x-access-token") String jwt,
            @Path("aid") String accountID);


}
