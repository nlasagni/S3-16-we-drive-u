package com.wedriveu.mobile.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.login.view.LoginView;
import com.wedriveu.mobile.login.view.LoginViewImpl;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.login.viewmodel.LoginViewModelImpl;

public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            LoginViewImpl loginViewFragment = new LoginViewImpl();
            /*LoginViewModelImpl loginViewModel = new LoginViewModelImpl();*/
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            /*transaction.add(loginViewModel, LoginViewModel.LOGIN_VIEW_MODEL_TAG);*/
            transaction.replace(R.id.login_fragment_container, loginViewFragment, LoginView.LOGIN_VIEW_TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


    public Fragment getPresenter(String tag){
        Fragment fragment = null;
        if ((LoginViewModel.LOGIN_VIEW_MODEL_TAG.equals(tag))) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }
}
