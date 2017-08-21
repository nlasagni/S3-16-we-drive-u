package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.CanDriveRequest;
import com.wedriveu.shared.rabbitmq.message.CanDriveResponse;

/**
 * @author Michele Donati on 09/08/2017.
 */

/**
 * This interface models the verticle of the vehicle is used to send the "can drive?" requests to the vehicles.
 */
public interface VehicleVerticleCanDrive {
    /**
     * This method permits to send the "can drive" request to the vehicle from the service.
     *
     * @param canDriveRequest This indicates the request object sended to vehicle.
     * @return Returns the the response object to the service.
     */
    CanDriveResponse canDrive(CanDriveRequest canDriveRequest);

}
