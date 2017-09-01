package com.wedriveu.mobile.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.wedriveu.mobile.booking.view.*;
import com.wedriveu.mobile.booking.viewmodel.BookingViewModel;
import com.wedriveu.mobile.booking.viewmodel.BookingViewModelImpl;
import com.wedriveu.mobile.login.router.LoginRouter;
import com.wedriveu.mobile.login.view.LoginView;
import com.wedriveu.mobile.login.view.LoginViewImpl;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.login.viewmodel.LoginViewModelImpl;
import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.store.StoreFactory;
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
 * The entry point {@link AppCompatActivity} of this app.
 * It handles all the routing operations to navigate from an app module to another.
 * Also, it exposes its capability of finding android components through the
 * {@linkplain ComponentFinder} interface and of providing factories through the
 * {@linkplain FactoryProvider} interface.
 *
 * @author Marco Balsassarri
 * @author Nicola Lasagni
 */
public class MainActivity extends AppCompatActivity implements LoginRouter,
                                                               SchedulingRouter,
                                                               BookingRouter,
                                                               ComponentFinder,
                                                               FactoryProvider {

    private FragmentManager mFragmentManager;
    private LocationService mLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        mLocationService = LocationServiceImpl.getInstance(this);
        UserStore userStore = provideStoreFactory().createUserStore();
        if (savedInstanceState == null) {
            if (userStore.getUser() != null) {
                showTripScheduling();
            } else {
                showLogin();
            }
        }
    }

    private void showLogin() {
        LoginViewImpl loginViewFragment = LoginViewImpl.newInstance(LoginViewModel.ID);
        LoginViewModelImpl loginViewModel = LoginViewModelImpl.newInstance(LoginView.ID);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(loginViewModel, LoginViewModel.ID);
        transaction.replace(R.id.fragment_container, loginViewFragment, LoginView.ID);
        transaction.commit();
    }

    @Override
    public Fragment getView(String tag) {
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
        Fragment loginViewModel = getViewModel(LoginViewModel.ID);
        if (loginViewModel != null) {
            transaction.remove(loginViewModel);
        }
        transaction.add(schedulingViewModel, SchedulingViewModel.ID);
        transaction.replace(R.id.fragment_container, SchedulingViewImpl.newInstance(), SchedulingView.ID);
        transaction.commit();
    }

    @Override
    public StoreFactory provideStoreFactory() {
        return (Application) getApplication();
    }

    @Override
    public ServiceFactory provideServiceFactory() {
        return (Application) getApplication();
    }

    @Override
    public void showPopOverDialog(String message) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.common_warning)
                .setMessage(message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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
    public void showBookingSummary() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment schedulingViewModel = getViewModel(SchedulingViewModel.ID);
        if (schedulingViewModel != null) {
            transaction.remove(schedulingViewModel);
        }
        transaction.add(BookingViewModelImpl.newInstance(), BookingViewModel.ID);
        transaction.replace(R.id.fragment_container, new BookingSummaryView(), BookingSummaryView.ID);
        transaction.commit();
    }

    @Override
    public void goBackToTripScheduling() {
        removeAllFragments();
        showTripScheduling();
    }

    @Override
    public void showUserTravellingView() {
        showBookingView(new BookingUserTravellingView(), BookingUserTravellingView.ID);
    }

    @Override
    public void showEnterVehicleView() {
        showBookingView(new BookingEnterVehicleView(), BookingEnterVehicleView.ID);
    }

    @Override
    public void showDestinationTravellingView() {
        showBookingView(new BookingDestinationTravellingView(), BookingDestinationTravellingView.ID);
    }

    @Override
    public void showSubstitutionView() {
        showBookingView(new BookingSubstitutionView(), BookingSubstitutionView.ID);
    }

    @Override
    public void showSubstitutionTravellingView() {
        showBookingView(new BookingUserSubstitutionTravellingView(), BookingUserSubstitutionTravellingView.ID);
    }

    private void showBookingView(Fragment bookingView, String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, bookingView, tag);
        transaction.commit();
    }

    private boolean isBookingSessionActive() {
        return getViewModel(BookingViewModel.ID) != null;
    }

    @Override
    public void onBackPressed() {
        if (isBookingSessionActive()) {
            showPopOverDialog(getString(R.string.booking_session_active));
        } else {
            super.onBackPressed();
        }
    }

    private void removeAllFragments() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        String[] tags = {
                SchedulingView.ID,
                SchedulingViewModel.ID,
                BookingSummaryView.ID,
                BookingViewModel.ID,
                BookingUserTravellingView.ID,
                BookingEnterVehicleView.ID,
                BookingDestinationTravellingView.ID,
                BookingSubstitutionView.ID,
                BookingUserSubstitutionTravellingView.ID
        };
        for (String tag : tags) {
            Fragment fragment = getViewModel(tag);
            if (fragment != null) {
                transaction.remove(fragment);
            }
        }
        transaction.commit();
        mFragmentManager.executePendingTransactions();
    }

}
