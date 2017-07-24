package com.wedriveu.mobile.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.login.router.LoginRouter;
import com.wedriveu.mobile.login.view.LoginView;
import com.wedriveu.mobile.login.view.LoginViewImpl;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.login.viewmodel.LoginViewModelImpl;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.tripscheduling.router.SchedulingRouter;
import com.wedriveu.mobile.tripscheduling.view.SchedulingView;
import com.wedriveu.mobile.tripscheduling.view.SchedulingViewImpl;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModel;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModelImpl;
import com.wedriveu.mobile.util.location.LocationService;
import com.wedriveu.mobile.util.location.LocationServiceImpl;


public class MainActivity extends AppCompatActivity implements LoginRouter, SchedulingRouter, ComponentFinder {

    private FragmentManager mFragmentManager;
    private LocationService mLocationService;
    private SchedulingViewImpl mSchedulingViewFragment;

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
            transaction.replace(R.id.fragment_container, loginViewFragment, LoginView.TAG);
            transaction.commit();
            mLocationService = LocationServiceImpl.getInstance(this);
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
        SchedulingViewModelImpl schedulingViewModel = SchedulingViewModelImpl.newInstance();
        mSchedulingViewFragment = SchedulingViewImpl.newInstance();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(schedulingViewModel, SchedulingViewModel.TAG);
        transaction.replace(R.id.fragment_container, mSchedulingViewFragment, SchedulingView.TAG);
        transaction.commit();
    }

    @Override
    public void showProgressDialog() {
        View progressDialog = findViewById(R.id.progress_dialog);
        progressDialog.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        View progressDialog = findViewById(R.id.progress_dialog);
        progressDialog.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationService.checkSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLocationService.onActivityResult(requestCode, resultCode, data);
        mSchedulingViewFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationService.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationService.disableLocationService();
    }

    @Override
    public void showBooking(Vehicle vehicle) {
        //TODO show booking fragment
        Log.i("SHOW_BOOKING", vehicle.toString());
    }

}
