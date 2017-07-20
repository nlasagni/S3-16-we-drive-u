package com.wedriveu.mobile.tripscheduling.view;

import com.google.android.gms.location.places.Place;

/**
 * <p>
 *     Describes the view methods of the Scuedling fragment
 * </p>
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingView {

    String TAG = SchedulingView.class.getSimpleName();

    /**
     * <p>
     *     Renders the view components to display to the user.
     * </p>
     */
    void renderView();

    /**
     * <p>
     *     Shows a dialog
     * </p>
     * @param message Message to show in the dialog
     */
    void renderError(String message);

    /**
     * <p>
     *     Shows the user the address selected by the Google Place Autocomplete
     * </p>
     * @param address the place chosen by the user during the interaction.
     */
    void showSelectedAddress(Place address);

}
