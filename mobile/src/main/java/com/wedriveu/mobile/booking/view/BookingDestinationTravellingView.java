package com.wedriveu.mobile.booking.view;

import com.wedriveu.mobile.R;

/**
 * The view that shows the vehicle taking the user to its destination.
 *
 * @author Nicola Lasagni on 20/08/2017.
 */
public class BookingDestinationTravellingView extends AbstractBookingTravellingView {

    /**
     * The id of this view.
     */
    public static final String ID = BookingDestinationTravellingView.class.getSimpleName();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_booking_travelling_destination;
    }

}
