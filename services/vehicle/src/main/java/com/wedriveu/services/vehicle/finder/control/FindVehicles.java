package com.wedriveu.services.vehicle.finder.control;

import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.finder.callback.FindVehiclesCallback;

import java.io.IOException;
import java.util.List;

/**
 * Created by stefano.bernagozzi on 18/07/2017.
 *
 * @author Michele Donati, Stefano Bernagozzi.
 * @author Michele Donati, Stefano Bernagozzi.
 */

/**
 * @author Michele Donati, Stefano Bernagozzi.
 */

/**
 * This interface models the search of the eligibles vehicles with calculations from user
 */
public interface FindVehicles {
    /**
     * Get only vehicles that can take the user to the specified destination
     * @param userPosition the position of the user that need a car
     * @param destPosition the position where the user wants to go
     * @param allAvailable a list of all the available cars at the moment of the method call
     * @param callback the callback that returns the list of eligibles vehicles
     */
    void listAllEligibleVehicles(Position userPosition,
                                 Position destPosition,
                                 List<Vehicle> allAvailable,
                                 FindVehiclesCallback callback) throws IOException;

}
