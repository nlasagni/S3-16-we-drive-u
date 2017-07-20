package com.wedriveu.mobile.tripscheduling.viewmodel;

import android.view.View;

/**
 * Created by Marco on 18/07/2017.
 */
public interface SchedulingViewModel {
    String TAG = SchedulingViewModel.class.getSimpleName();
    void onSearchVehicleButtonClick(String address);
    void startPlaceAutocomplete();

}
