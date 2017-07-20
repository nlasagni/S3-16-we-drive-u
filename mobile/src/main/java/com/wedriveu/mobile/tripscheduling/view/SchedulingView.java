package com.wedriveu.mobile.tripscheduling.view;

import com.google.android.gms.location.places.Place;

/**
 * Created by Marco on 18/07/2017.
 */
public interface SchedulingView {

    String TAG = SchedulingView.class.getSimpleName();

    void renderView();
    void renderError(String message);
    void showSelectedAddress(Place address);

}
