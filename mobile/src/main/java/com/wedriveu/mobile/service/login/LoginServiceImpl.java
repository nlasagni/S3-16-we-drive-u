package com.wedriveu.mobile.service.login;

/**
 * Created by Marco on 12/07/2017.
 */
public class LoginServiceImpl implements LoginService {

    @Override
    public void login(String username, String password, LoginServiceCallback callback) {
        //TODO Complete with retrofit http request
        callback.onLoginFinished(null, "Error");
    }

}
