package com.wedriveu.services.vehicle.entity;

/**
 * Created by Marco on 25/07/2017.
 */
public class EligibleVehicle {

    private Vehicle vehicle;
    private double distanceToUser;
    private double distanceToDestination;

    public EligibleVehicle(Vehicle vehicle, double distanceToUser, double distanceToDestination) {
        this.vehicle = vehicle;
        this.distanceToUser = distanceToUser;
        this.distanceToDestination = distanceToDestination;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(double distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public double getDistanceToDestination() {
        return distanceToDestination;
    }

    public void setDistanceToDestination(double distanceToDestination) {
        this.distanceToDestination = distanceToDestination;
    }
}
