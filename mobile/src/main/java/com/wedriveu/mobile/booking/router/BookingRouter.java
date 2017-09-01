package com.wedriveu.mobile.booking.router;

import com.wedriveu.mobile.app.DialogProvider;

/**
 * The router that handles all the transactions between views.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public interface BookingRouter extends DialogProvider {

    /**
     * Navigates back to the trip scheduling.
     */
    void goBackToTripScheduling();

    /**
     * Shows the vehicle travelling to the user.
     */
    void showUserTravellingView();

    /**
     * Shows the view that allows the user to enter into its vehicle.
     */
    void showEnterVehicleView();

    /**
     * Shows the view of the vehicle, or the substitution one, taking the user to its destination.
     */
    void showDestinationTravellingView();

    /**
     * Shows the vehicle substitution waiting view.
     */
    void showSubstitutionView();

    /**
     * Shows the substitution vehicle travelling to the user.
     */
    void showSubstitutionTravellingView();

}
