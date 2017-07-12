package com.wedriveu.mobile.login.viewmodel;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.Application;
import com.wedriveu.mobile.entity.model.User;
import com.wedriveu.mobile.entity.store.LoginStore;
import com.wedriveu.mobile.entity.store.LoginStoreImpl;
import com.wedriveu.mobile.login.router.LoginRouter;
import com.wedriveu.mobile.login.service.LoginService;
import com.wedriveu.mobile.login.service.LoginServiceCallback;
import com.wedriveu.mobile.login.service.LoginServiceImpl;
import com.wedriveu.mobile.login.view.LoginView;

/**
 * Created by Marco on 12/07/2017.
 */
public class LoginViewModelImpl extends Fragment implements LoginViewModel, LoginServiceCallback {
    private LoginService mLoginService;
    private LoginRouter mRouter;
    private LoginStore mLoginStore;
    private LoginView mLoginView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginService = new LoginServiceImpl();
        mLoginStore = new LoginStoreImpl();
    }

    @Override
    public void onLoginButtonClick(String username, String password) {
        mLoginService.login(username, password, this);
    }


    /*
        The deprecated method has been chosen because the minTargetVersion is 19.
        Switch to >=23 in order to use the new onAttach method.
    */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRouter = (LoginRouter) activity;
    }


    @Override
    public void onLoginFinished(User user) {
        if(user == null){
            mLoginView = (LoginView) ((Application) getActivity()).getView(LoginView.LOGIN_VIEW_TAG);
            mLoginView.renderError(getString(R.string.login_error_message));
        } else {
            mLoginStore.storeUser(user);
            mRouter.showTripScheduling();
        }

    }
}
