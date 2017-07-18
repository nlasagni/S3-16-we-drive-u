package com.wedriveu.mobile.tripscheduling.view;

/**
 * Created by Marco on 18/07/2017.
 */
public interface SchedulingView {

    String TAG = SchedulingView.class.getSimpleName();

    void renderView();
    void renderError(String message);

}
