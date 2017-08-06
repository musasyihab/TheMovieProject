package com.musasyihab.themovieproject.network;

import android.support.compat.BuildConfig;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.musasyihab.themovieproject.util.Constants;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by musasyihab on 8/2/17.
 */

public class ApiUtils {

    public static Restapi setupRetrofit() {
        Log.e(Constants.APP_NAME, "setupRetrofit");
        return new Retrofit.Builder()
                .client(setupOkHttpClient())
                .baseUrl(Constants.API_PATH)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create()))
                .build()
                .create(Restapi.class);
    }

    private static OkHttpClient setupOkHttpClient() {
        Log.e(Constants.APP_NAME, "setupOkHttpClient");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(Constants.APP_NAME, message);
            }
        });
        //if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //}

        return new OkHttpClient.Builder().addInterceptor(interceptor).
                readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS).writeTimeout(Constants.TIMEOUT, TimeUnit.SECONDS).build();
    }
}
