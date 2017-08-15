package com.wedriveu.mobile.login.viewmodel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.login.router.LoginRouter;
import com.wedriveu.mobile.login.view.LoginView;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.service.ServiceFactoryImpl;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.store.StoreFactoryImpl;
import com.wedriveu.mobile.store.UserStore;

/**
 * Created by Marco on 12/07/2017.
 */
public class LoginViewModelImpl extends Fragment implements LoginViewModel {

    private String mViewId;
    private LoginService mLoginService;
    private UserStore mUserStore;
    private LoginRouter mRouter;

    public static LoginViewModelImpl newInstance(String viewId) {
        LoginViewModelImpl fragment = new LoginViewModelImpl();
        fragment.setRetainInstance(true);
        fragment.setViewId(viewId);
        return fragment;
    }

    private void setViewId(String viewId) {
        mViewId = viewId;
    }

    // This deprecated method has been chosen because the minTargetVersion is 19.
    // Switch to minTargetVersion >= 23 in order to use the new onAttach method.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRouter = (LoginRouter) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUserStore = StoreFactoryImpl.getInstance().createUserStore(getContext());
        mLoginService = ServiceFactoryImpl.getInstance().createLoginService(getActivity());
    }

    @Override
    public void onLoginButtonClick(String username, String password) {
        mRouter.showProgressDialog();
        mLoginService.login(username, password, new ServiceOperationCallback<User>() {
            @Override
            public void onServiceOperationFinished(ServiceResult<User> result) {
                onLoginFinished(result.getResult(), result.getErrorMessage());
            }
        });
    }

    private void onLoginFinished(User user, String errorMessage) {
        mRouter.dismissProgressDialog();
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            if (!TextUtils.isEmpty(errorMessage)) {
                LoginView view = (LoginView) ((ComponentFinder) getActivity()).getView(mViewId);
                view.renderError(errorMessage);
            } else {
                mUserStore.storeUser(user);
                mRouter.showTripScheduling();
            }
        }
    }

}
