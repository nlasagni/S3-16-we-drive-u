package com.wedriveu.services.vehicle.election.entity;

import com.wedriveu.services.vehicle.entity.Vehicle;

/**
 * Vehicle wrapper used to send the distance to the ElibibleVehicleControl without changing the Vehicle model
 *
 * Created by Marco on 25/07/2017.
 */
public class EligibleVehicle {

    private Vehicle vehicle;
    private double distanceToUser;

    public EligibleVehicle(Vehicle vehicle, double distanceToUser) {
        this.vehicle = vehicle;
        this.distanceToUser = distanceToUser;
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

}
