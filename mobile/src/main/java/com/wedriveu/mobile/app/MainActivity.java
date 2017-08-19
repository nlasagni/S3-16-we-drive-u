package com.wedriveu.mobile.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.booking.router.BookingRouter;
import com.wedriveu.mobile.booking.view.AcceptedBookingView;
import com.wedriveu.mobile.booking.viewmodel.BookingViewModel;
import com.wedriveu.mobile.booking.viewmodel.BookingViewModelImpl;
import com.wedriveu.mobile.booking.view.BookingView;
import com.wedriveu.mobile.booking.view.BookingViewImpl;
import com.wedriveu.mobile.login.router.LoginRouter;
import com.wedriveu.mobile.login.view.LoginView;
import com.wedriveu.mobile.login.view.LoginViewImpl;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.login.viewmodel.LoginViewModelImpl;
import com.wedriveu.mobile.store.StoreFactoryImpl;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.tripscheduling.router.SchedulingRouter;
import com.wedriveu.mobile.tripscheduling.view.SchedulingView;
import com.wedriveu.mobile.tripscheduling.view.SchedulingViewImpl;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModel;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModelImpl;
import com.wedriveu.mobile.util.Constants;
import com.wedriveu.mobile.util.location.LocationService;
import com.wedriveu.mobile.util.location.LocationServiceImpl;

/**
 *
 * @author Marco Balsassarri
 * @author Nicola Lasagni
 * @since 4/07/2017
 *
 */
public class MainActivity extends AppCompatActivity implements LoginRouter,
                                                               SchedulingRouter,
                                                               BookingRouter,
                                                               ComponentFinder {

    private FragmentManager mFragmentManager;
    private LocationService mLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        mLocationService = LocationServiceImpl.getInstance(this);
        UserStore userStore = StoreFactoryImpl.getInstance().createUserStore(this);
        if (savedInstanceState == null) {
            if (userStore.getUser() != null) {
                showTripScheduling();
            } else {
                showLogin();
            }
        }
    }

    private void showLogin() {
        LoginViewImpl loginViewFragment = LoginViewImpl.newInstance(LoginViewModel.TAG);
        LoginViewModelImpl loginViewModel = LoginViewModelImpl.newInstance(LoginView.TAG);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(loginViewModel, LoginViewModel.TAG);
        transaction.replace(R.id.fragment_container, loginViewFragment, LoginView.TAG);
        transaction.commit();
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
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment loginViewModel = getViewModel(LoginViewModel.TAG);
        if (loginViewModel != null) {
            transaction.remove(loginViewModel);
        }
        transaction.add(schedulingViewModel, SchedulingViewModel.ID);
        transaction.replace(R.id.fragment_container, SchedulingViewImpl.newInstance(), SchedulingView.ID);
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
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Fragment viewModel = getViewModel(SchedulingViewModel.ID);
            viewModel.onActivityResult(requestCode, resultCode, data);
        } else {
            mLocationService.onActivityResult(requestCode, resultCode, data);
        }
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
    public void showBooking() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment schedulingViewModel = getViewModel(SchedulingViewModel.ID);
        if (schedulingViewModel != null) {
            transaction.remove(schedulingViewModel);
        }
        transaction.add(BookingViewModelImpl.newInstance(), BookingViewModel.ID);
        transaction.replace(R.id.fragment_container, new BookingViewImpl(), BookingView.ID);
        transaction.commit();
    }

    @Override
    public void goBackToTripScheduling() {
        removeAllFragments();
        showTripScheduling();
    }

    @Override
    public void showBookingAcceptedView() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new AcceptedBookingView(), AcceptedBookingView.ID);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {}

    private void removeAllFragments() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        String[] tags = {
                SchedulingView.ID,
                SchedulingViewModel.ID,
                BookingView.ID,
                BookingViewModel.ID,
                AcceptedBookingView.ID
        };
        for (String tag : tags) {
            Fragment fragment = getViewModel(SchedulingViewModel.ID);
            if (fragment != null) {
                transaction.remove(fragment);
            }
        }
        transaction.commit();
        mFragmentManager.executePendingTransactions();
    }

}
