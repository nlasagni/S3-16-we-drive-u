package com.wedriveu.mobile.booking.view;

import com.wedriveu.mobile.R;

/**
 * The base booking travelling view that shows a vehicle travelling to the user.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingUserTravellingView extends AbstractBookingTravellingView {

    /**
     * The id of this view.
     */
    public static final String ID = BookingUserTravellingView.class.getSimpleName();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_booking_travelling_accepted;
    }

}
