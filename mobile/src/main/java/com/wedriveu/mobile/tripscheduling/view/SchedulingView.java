package com.wedriveu.mobile.tripscheduling.view;

import com.google.android.gms.location.places.Place;

/**
 *
 * Describes the view methods of the Scheduling fragment
 *
 * @author Marco Baldassarri
 */
public interface SchedulingView {

    /**
     * The id of this view.
     */
    String ID = SchedulingView.class.getSimpleName();

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
     * Shows the user the pick up address selected by the Google Place Autocomplete
     *
     * @param address the place chosen by the user during the interaction.
     */
    void showPickUpAddress(Place address);

    /**
     *
     * Shows the user the destination address selected by the Google Place Autocomplete
     *
     * @param address the place chosen by the user during the interaction.
     */
    void showDestinationAddress(Place address);

}
