package com.wedriveu.services.shared.rabbitmq.nearest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes the response the vehicle return to the VehicleService once it asks the vehicle if it
 * can drive to destination.
 *
 * Created by Marco on 04/08/2017.
 */
public class VehicleResponseCanDrive {

    @JsonProperty
    private String licencePlate;

    @JsonProperty
    private boolean eligible;

    @JsonProperty
    private String username;

    @JsonProperty
    private double distanceToUser;

    @JsonProperty
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
