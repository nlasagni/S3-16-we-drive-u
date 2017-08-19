package com.wedriveu.mobile.booking.viewmodel;

/**
 * Created by nicolalasagni on 28/07/2017.
 */
public interface BookingViewModel {

    String ID = BookingViewModel.class.getSimpleName();

    void onAcceptButtonClick();

    void onDeclineButtonClick();

    void onEnterVehicleButtonClick();

}
