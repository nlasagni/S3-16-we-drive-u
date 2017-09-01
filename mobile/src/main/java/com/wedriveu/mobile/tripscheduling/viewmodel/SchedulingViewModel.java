package com.wedriveu.mobile.tripscheduling.viewmodel;

/**
 *
 * Represents the View Model for the Scheduling part of the application, based on the pattern MVVM.
 *
 * @author Marco Baldassarri
 */
public interface SchedulingViewModel {

    /**
     * The id of this view model.
     */
    String ID = SchedulingViewModel.class.getSimpleName();

    /**
     * handles the button click to trigger the vehicle selection process
     */
    void onSearchVehicleButtonClick(boolean useUserPosition);

    /**
     * Shows the user the Google Intent used to fetch the address
     */
    void startPlaceAutocomplete(boolean forDestination);

}
