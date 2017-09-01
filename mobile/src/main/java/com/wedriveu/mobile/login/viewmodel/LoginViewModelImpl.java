package com.wedriveu.mobile.login.viewmodel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.app.FactoryProvider;
import com.wedriveu.mobile.login.router.LoginRouter;
import com.wedriveu.mobile.login.view.LoginView;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.store.StoreFactory;
import com.wedriveu.mobile.store.UserStore;

/**
 * The {@linkplain LoginViewModel} implementation.
 *
 * @author Marco on 12/07/2017.
 * @author Nicola Lasagni
 */
public class LoginViewModelImpl extends Fragment implements LoginViewModel {

    private String mViewId;
    private LoginService mLoginService;
    private UserStore mUserStore;
    private LoginRouter mRouter;
    private LoginServiceHandler<User> mLoginHandler;

    /**
     * New instance of a {@linkplain LoginViewModel}.
     *
     * @return the booking view model
     */
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
        mLoginHandler = new LoginHandler(this);
        StoreFactory storeFactory = ((FactoryProvider) getActivity()).provideStoreFactory();
        ServiceFactory serviceFactory = ((FactoryProvider) getActivity()).provideServiceFactory();
        mUserStore = storeFactory.createUserStore();
        mLoginService = serviceFactory.createLoginService();
    }

    @Override
    public void onLoginButtonClick(String username, String password) {
        mRouter.showProgressDialog();
        mLoginService.login(username, password, mLoginHandler);
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

    private static class LoginHandler extends LoginServiceHandler<User> {

        LoginHandler(LoginViewModelImpl weakReference) {
            super(weakReference);
        }

        @Override
        protected void handleMessage(LoginViewModelImpl weakReference, ServiceResult<User> result) {
            weakReference.onLoginFinished(result.getResult(), result.getErrorMessage());
        }
    }

}
