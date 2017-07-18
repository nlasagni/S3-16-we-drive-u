package com.wedriveu.mobile.service.login;

import android.content.Context;
import android.util.Log;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Marco on 12/07/2017.
 */
public class LoginServiceImpl implements LoginService {

    private static final String TAG = LoginService.class.getSimpleName();

    private LoginServiceApi mLoginServiceApi;

    public LoginServiceImpl() {
        String baseUrl = Constants.SERVICE_BASE_URL;
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(baseUrl);
        Retrofit retrofit = retrofitBuilder.build();
        mLoginServiceApi = retrofit.create(LoginServiceApi.class);
    }

    @Override
    public void login(final String username, final String password, final LoginServiceCallback callback) {
        Call<Void> loginCall = mLoginServiceApi.login(username, password);
        loginCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onLoginFinished(new User(username, password), null);
                } else {
                    callback.onLoginFinished(null, response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Failure on login operation!", t);
                callback.onLoginFinished(null, t.getLocalizedMessage());
            }
        });
    }

}
