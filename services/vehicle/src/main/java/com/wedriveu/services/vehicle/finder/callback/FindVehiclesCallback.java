package com.wedriveu.services.vehicle.finder.callback;

import com.wedriveu.services.vehicle.entity.EligibleVehicle;

import java.util.List;
/**
 * @author Michele Donati, Stefano Bernagozzi
 */

/**
 * This interface models the callback for the eligibles vehicles election.
 */
public interface FindVehiclesCallback {
    /**
     * @param eligibles Indicates the list of the eligible vehicles.
     **/
    void listAllEligiblesVehiclesCallback(List<EligibleVehicle> eligibles);

}
