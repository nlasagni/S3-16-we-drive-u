package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.util.Position;

import java.util.List;

/**
 * Represents the data the client sends when requesting the best vehicle to book as well as the list of available
 * vehicles at the time of the request.
 *
 * @author Marco Baldassarri on 02/08/2017.
 */
public class UserRequest {

    @JsonProperty
    private Position userPosition;

    @JsonProperty
    private Position destinationPosition;

    @JsonProperty
    private String username;

    @JsonProperty
    private List<Vehicle> vehicleList;

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
