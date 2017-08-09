package com.wedriveu.services.vehicle.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.entity.Vehicle;
import java.util.List;
import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * Represents the data the client sends when requesting the best vehicle to book as well as the list of available
 * vehicles at the time of the request.
 *
 * @author Marco Baldassarri
 * @since 02/08/2017
 */
public class UserRequest {

    @JsonProperty(USER_POSITION)
    private Position userPosition;

    @JsonProperty(DESTINATION_POSITION)
    private Position destinationPosition;

    @JsonProperty(USERNAME)
    private String username;

    @JsonProperty(ELIGIBLE_VEHICLE_LIST)
    private List<Vehicle> vehicleList;

    @JsonProperty(TRIP_DISTANCE)
    private double tripDistance;

    public double getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(double tripDistance) {
        this.tripDistance = tripDistance;
    }

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
