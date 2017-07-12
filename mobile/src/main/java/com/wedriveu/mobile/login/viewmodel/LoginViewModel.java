package com.wedriveu.mobile.login.viewmodel;

/**
 * Created by Marco on 12/07/2017.
 */
public interface LoginViewModel {

    String LOGIN_VIEW_MODEL_TAG = LoginViewModel.class.getSimpleName();
    void onLoginButtonClick(String username, String password);

}
