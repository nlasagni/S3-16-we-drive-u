package com.wedriveu.mobile.login.view;

import com.wedriveu.mobile.login.viewmodel.LoginViewModel;

/**
 * Created by Marco on 12/07/2017.
 */
public interface LoginView {
    String LOGIN_VIEW_TAG = LoginViewModel.class.getSimpleName();
    void renderView();
    void renderError(String message);
}
