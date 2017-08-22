package com.wedriveu.mobile.booking.view;

import com.wedriveu.mobile.R;

/**
 * @author Nicola Lasagni on 20/08/2017.
 */
public class TravellingBookingViewImpl  extends AbstractTravellingView {

    public static String ID = TravellingBookingViewImpl.class.getSimpleName();

    @Override
    int getLayoutResource() {
        return R.layout.fragment_booking_travelling;
    }

}
