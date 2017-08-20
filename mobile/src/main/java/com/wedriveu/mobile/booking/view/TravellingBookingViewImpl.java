package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wedriveu.mobile.R;

/**
 * @author Nicola Lasagni on 20/08/2017.
 */
public class TravellingBookingViewImpl extends AcceptedBookingViewImpl {

    public static String ID = TravellingBookingViewImpl.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_travelling, container, false);
    }

}
