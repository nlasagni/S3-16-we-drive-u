package com.wedriveu.mobile.tripscheduling.view;

import com.google.android.gms.location.places.Place;

/**
 *
 * Describes the view methods of the Scuedling fragment
 *
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingView {

    String TAG = SchedulingView.class.getSimpleName();

    /**
     *
     * Renders the view components to display to the user.
     *
     */
    void renderView();

    /**
     * Shows a dialog
     *
     * @param message Message to show in the dialog
     */
    void renderError(String message);

    /**
     *
     * Shows the user the address selected by the Google Place Autocomplete
     *
     * @param address the place chosen by the user during the interaction.
     */
    void showSelectedAddress(Place address);

}
