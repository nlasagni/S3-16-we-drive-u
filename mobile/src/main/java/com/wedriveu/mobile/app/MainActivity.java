package com.wedriveu.mobile.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.login.router.LoginRouter;
import com.wedriveu.mobile.login.view.LoginView;
import com.wedriveu.mobile.login.view.LoginViewImpl;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.login.viewmodel.LoginViewModelImpl;
import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.service.ServiceFactoryImpl;
import com.wedriveu.mobile.store.StoreFactory;
import com.wedriveu.mobile.store.StoreFactoryImpl;

public class MainActivity extends AppCompatActivity implements LoginRouter, FactoryManager, ComponentFinder {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            LoginViewImpl loginViewFragment = LoginViewImpl.newInstance(LoginViewModel.TAG);
            LoginViewModelImpl loginViewModel = LoginViewModelImpl.newInstance(LoginView.TAG);

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(loginViewModel, LoginViewModel.TAG);

            transaction.replace(R.id.login_fragment_container, loginViewFragment, LoginView.TAG);
            transaction.commit();
        }
    }

    @Override
    public Fragment getView(String tag){
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public Fragment getViewModel(String tag) {
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public void showTripScheduling() {
        //TODO Login was successful, show the scheduling here.
        Log.i(MainActivity.class.getSimpleName(), "scheduling fragment");
    }

    @Override
    public StoreFactory createStoreFactory() {
        return new StoreFactoryImpl(getApplication());
    }

    @Override
    public ServiceFactory createServiceFactory() {
        return new ServiceFactoryImpl();
    }
}
