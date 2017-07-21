package com.wedriveu.mobile.tripscheduling.viewmodel;

/**
 *
 * <p>
 *     Represents the View Model for the Scheduling part of the application, based on the pattern MVVM.
 * </p>
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingViewModel {
    String TAG = SchedulingViewModel.class.getSimpleName();

    /**
     * handles the button click to trigger the vehicle selection process
     */
    void onSearchVehicleButtonClick();

    /**
     * Shows the user the Google Intent used to fetch the address
     */
    void startPlaceAutocomplete();

}
