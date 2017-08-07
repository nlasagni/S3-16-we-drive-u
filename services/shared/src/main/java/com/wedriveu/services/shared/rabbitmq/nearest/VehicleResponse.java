package com.wedriveu.services.shared.rabbitmq.nearest;

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

    @JsonProperty(USERNAME)
    private String username;

    @JsonProperty(DISTANCE_TO_USER)
    private double distanceToUser;

    @JsonProperty(VEHICLE_SPEED)
    private double vehicleSpeed;


    public double getVehicleSpeed() {
        return vehicleSpeed;
    }

    public void setVehicleSpeed(double vehicleSpeed) {
        this.vehicleSpeed = vehicleSpeed;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(double distanceToUser) {
        this.distanceToUser = distanceToUser;
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

    @Override
    public String toString() {
        return "VehicleResponse{" +
                "licencePlate='" + licencePlate + '\'' +
                ", eligible=" + eligible +
                ", username='" + username + '\'' +
                ", distanceToUser=" + distanceToUser +
                ", vehicleSpeed=" + vehicleSpeed +
                '}';
    }

}
