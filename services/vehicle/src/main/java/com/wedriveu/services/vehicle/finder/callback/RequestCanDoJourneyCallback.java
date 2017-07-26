package com.wedriveu.services.vehicle.finder.callback;
/**
 *
 * This interface models the callback for the request of the journey to all the vehicles in the range of user.
 *
 * @author Michele Donati, Stefano Bernagozzi.
 *
 */
public interface RequestCanDoJourneyCallback {
    /**
     *
     * @param canDo Indicates that if the vehicles can/can't do the journey suggested from the vehicle service.
     */
    void onRequestCanDoJourney(boolean canDo);

}
