package com.wedriveu.services.vehicle.callback;

import com.wedriveu.services.vehicle.entity.Vehicle;

import java.util.List;
/**
 * @author Michele Donati, Stefano Bernagozzi
 */

/**
 * This interface models the callback for the eligibles vehicles election.
 */
public interface FindVehiclesCallback {
    /**
     *
     * @param eligibles Indicates the list of the elegibles vehicles.
     */
    void listAllEligiblesVehiclesCallback(List<Vehicle> eligibles);

}
