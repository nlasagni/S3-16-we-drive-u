package com.wedriveu.mobile.booking.view;

import com.wedriveu.mobile.R;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class AcceptedBookingViewImpl extends AbstractTravellingView {

    public static String ID = AcceptedBookingViewImpl.class.getSimpleName();

    @Override
    int getLayoutResource() {
        return R.layout.fragment_booking_accepted;
    }
}
