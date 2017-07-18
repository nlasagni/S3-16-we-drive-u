package com.wedriveu.mobile.login.service;

import com.wedriveu.mobile.model.User;

/**
 * Created by Marco on 12/07/2017.
 */
public class LoginServiceImpl implements LoginService {

    @Override
    public void login(String username, String password, LoginServiceCallback callback) {
        //TODO Complete with retrofit http call
        if(username.isEmpty() || password.isEmpty()) {
            callback.onLoginFinished(null, "Error");
        }
        if(username.equals("pinco") && password.equals("pallo")) {
            User user = new User(username, password);
            callback.onLoginFinished(user, null);
        }
    }

}
