package com.wedriveu.mobile.booking.router;

import com.wedriveu.mobile.app.DialogProvider;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public interface BookingRouter extends DialogProvider {

    /**
     *
     */
    void goBackToTripScheduling();

    /**
     *
     */
    void showBookingAcceptedView();

}
