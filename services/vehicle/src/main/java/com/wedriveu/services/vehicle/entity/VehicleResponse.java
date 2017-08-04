package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * Created by Marco on 04/08/2017.
 */
public class VehicleResponse {



    @JsonProperty(CAR_LICENCE_PLATE)
    private String licencePlate;

    @JsonProperty(ELIGIBLE)
    private boolean eligible;

    @JsonProperty(USER_USERNAME)
    private String username;

    @JsonProperty(DISTANCE_TO_USER)
    private String distanceToUser;

    @JsonProperty(TRIP_DISTANCE)
    private double tripDistance;

    @JsonProperty(VEHICLE_SPEED)
    private double vehicleSpeed;



    public double getVehicleSpeed() {
        return vehicleSpeed;
    }

    public void setVehicleSpeed(double vehicleSpeed) {
        this.vehicleSpeed = vehicleSpeed;
    }

    public String getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(String distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public double getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(double tripDistance) {
        this.tripDistance = tripDistance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

}
