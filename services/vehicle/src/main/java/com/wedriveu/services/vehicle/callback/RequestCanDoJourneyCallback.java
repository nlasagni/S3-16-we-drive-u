package com.wedriveu.services.vehicle.callback;

/**
 * Created by Michele on 18/07/2017.
 */

/**
 * @author Michele Donati, Stefano Bernagozzi.
 */

/**
 * This interface models the callback for the request of the journey to all the vehicles in the range of user.
 */
public interface RequestCanDoJourneyCallback {
    /**
     *
     * @param canDo Indicates that if the vehicles can/can't do the journey suggested from the vehicle service.
     */
    void onRequestCanDoJourney(boolean canDo);

}
