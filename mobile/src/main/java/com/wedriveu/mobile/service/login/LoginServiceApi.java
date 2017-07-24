package com.wedriveu.mobile.service.login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Login service web api interface
 *
 * @author Nicola Lasagni
 * @since 18/07/2017
 */
public interface LoginServiceApi {

    /**
     * Retrofit login web api interface definition.
     * @param username The username to login with.
     * @param password The password to login with.
     * @return A Retrofit {@linkplain Call}.
     */
    @GET("user/login")
    Call<Void> login(@Query("username") String username, @Query("password") String password);

}
