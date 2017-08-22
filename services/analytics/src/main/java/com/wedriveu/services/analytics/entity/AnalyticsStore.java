package com.wedriveu.services.analytics.entity;

import com.wedriveu.services.shared.model.AnalyticsVehicle;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;

import java.util.Optional;

/**
 * @author Stefano Bernagozzi
 *         This inteface models the <em>AnalyticsVehicle' database store</em>.
 */


public interface AnalyticsStore {
    /**
     * get the vehicle by its license plate
     *
     * @param licensePlate the string that represents the license plate of the <em>AnalyticsVehicle</em> that wants to be returned
     * @return a <em>AnalyticsVehicle</em> that contains the license plate and the status of the vehicle requested
     */
    Optional<AnalyticsVehicle> getVehicleByLicensePlate(String licensePlate);

    /**
     * adds a <em>AnalyticsVehicle</em> to StoreEntities
     *
     * @param licensePlate the license plate of the <em>AnalyticsVehicle</em> that wants to be added
     * @param status       the current status of the <em>AnalyticsVehicle</em> that wants to be added
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean addVehicle(String licensePlate, String status);

    /**
     * update the status of a <em>AnalyticsVehicle</em>
     *
     * @param licensePlate the license plate of the <em>AnalyticsVehicle</em> that wants to be updated
     * @param status       the current status of the <em>AnalyticsVehicle</em> that wants to be updated
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean updateVehicle(String licensePlate, String status);

    /**
     * clear the database
     */
    void clear();

    /**
     * get the number of vehicles divided per status
     *
     * @return a class VehicleCounter that contains the number of vehicles per status
     */
    VehicleCounter getVehicleCounter();
}


