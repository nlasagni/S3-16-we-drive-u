package com.wedriveu.mobile.booking.presenter;

/**
 * Created by nicolalasagni on 28/07/2017.
 */
public interface BookingPresenter {

    String ID = BookingPresenter.class.getSimpleName();

    void onAcceptButtonClick();

    void onDeclineButtonClick();

}
