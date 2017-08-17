package com.wedriveu.services.vehicle.entity;

import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.util.Position;

/**
 * @author Marco on 17/08/2017
 *
 * This class represents a "book" response wrapper to properly facilitate communication between internal verticles
 * inside the VehicleService once the Vehicle sends the response back.
 *
 */

public class BookVehicleResponseWrapper {


    private BookVehicleResponse response;
    private Position userPosition;
    private Position destinationPosition;

    public BookVehicleResponse getResponse() {
        return response;
    }

    public void setResponse(BookVehicleResponse response) {
        this.response = response;
    }

    public Position getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(Position userPosition) {
        this.userPosition = userPosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

    public void setDestinationPosition(Position destinationPosition) {
        this.destinationPosition = destinationPosition;
    }
}
