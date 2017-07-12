package com.wedriveu.mobile.login.view;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.MainActivity;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.login.viewmodel.LoginViewModelImpl;

public class LoginViewImpl extends Fragment implements LoginView {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_view, container, false);
    }


    @Override
    public void renderView() {

    }

    @Override
    public void renderError(String message) {

    }

}
