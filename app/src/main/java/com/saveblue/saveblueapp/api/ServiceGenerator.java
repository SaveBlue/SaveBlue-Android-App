package com.saveblue.saveblueapp.api;

import com.saveblue.saveblueapp.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Retrofit.Builder sBuilder;
    private static OkHttpClient.Builder sHttpClient;
    private static Retrofit sRetrofit;

    static {
        init();
    }

    private static void init() {

        /// create Interceptor and add it to client

        HttpLoggingInterceptor interceptor  = new HttpLoggingInterceptor();
        interceptor .setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        sRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        return sRetrofit.create(serviceClass);
    }

}