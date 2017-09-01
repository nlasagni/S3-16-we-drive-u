package com.wedriveu.mobile.booking.viewmodel;

/**
 * The ViewModel that manages the whole booking section of this app.
 *
 * @author Nicola Lasagni on 28/07/2017.
 */
public interface BookingViewModel {

    /**
     * The id of this view model.
     */
    String ID = BookingViewModel.class.getSimpleName();

    /**
     * Handles the event triggered when the user accepts the vehicle chosen
     * by the WeDriveU service.
     */
    void onAcceptBookingButtonClick();

    /**
     * Handles the event triggered when the user declines the vehicle chosen
     * by the WeDriveU service.
     */
    void onDeclineBookingButtonClick();

    /**
     * Handles the event triggered when the user enters into the vehicle chosen
     * by the WeDriveU service.
     */
    void onEnterVehicleButtonClick();

}
