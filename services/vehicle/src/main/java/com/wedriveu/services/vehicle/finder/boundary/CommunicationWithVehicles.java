package com.wedriveu.services.vehicle.finder.boundary;


import com.wedriveu.services.vehicle.finder.callback.RequestCanDoJourneyCallback;

import java.io.IOException;
/**
 * Created by stefano.bernagozzi on 17/07/2017.
 *
 * @author Michele Donati, Stefano Bernagozzi.
 */

/**
 * @author Michele Donati, Stefano Bernagozzi.
 */

/**
 * This interface models the communication mechanism between the vehicle service and all the vehicles.
 */
public interface CommunicationWithVehicles {

    /**
     *
     * @param licensePlate Indicates the identifier of the vehicle, used for establish the connection.
     * @param kilometersToDo Indicates the kilometers to do gived to the vehicles for the journey calculation.
     * @param listAllEligiblesCallback Indicates the callback for the asynchronous communication.
     * @throws IOException
     */
    void requestCanDoJourney(String licensePlate,
                             double kilometersToDo,
                             RequestCanDoJourneyCallback listAllEligiblesCallback) throws IOException;

}
