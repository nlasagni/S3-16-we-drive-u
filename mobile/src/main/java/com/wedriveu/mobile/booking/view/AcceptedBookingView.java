package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wedriveu.mobile.R;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class AcceptedBookingView extends Fragment {

    public static final String ID = AcceptedBookingView.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_accepted, container, false);
        return view;
    }
}
