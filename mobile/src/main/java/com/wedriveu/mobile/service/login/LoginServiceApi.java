package com.wedriveu.mobile.service.login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by nicolalasagni on 18/07/2017.
 */
public interface LoginServiceApi {

    @GET("user/login")
    Call<Void> login(@Query("username") String username, @Query("password") String password);

}
