package com.wedriveu.vehicle.boundary;


import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.rabbitmq.message.VehicleReservationRequest;

/**
 * @author Michele Donati on 11/08/2017.
 */

/**
 * This interface models the verticle of the vehicle that is used to send the "Book" requests to the
 * vehicles.
 */
public interface VehicleVerticleBook {
    /**
     * This method permits to send a "Book" request from the service to vehicle.
     *
     * @param request This indicates the request object sended.
     * @return Returns a response object to the service.
     */

    BookVehicleResponse book(VehicleReservationRequest request);

}
