package com.wedriveu.mobile.service;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

import static com.wedriveu.mobile.util.Constants.HTTP_CALL_TIMEOUT;
import static com.wedriveu.mobile.util.Constants.SERVICE_BASE_URL;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(HTTP_CALL_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_CALL_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(HTTP_CALL_TIMEOUT, TimeUnit.SECONDS)
                .build();
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVICE_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}