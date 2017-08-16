package com.wedriveu.services.shared.entity;

import java.util.List;

/**
 * This object describes the entire list of Vehicles the VehicleService sends back to the AnalyticsService.
 * The list is referring to the routingKey 'vehicle.response.all'.
 *
 * Created by Marco on 15/08/2017.
 */
public class AnalyticsVehicleList {
    List<Vehicle> vehiclesList;

    public List<Vehicle> getVehiclesList() {
        return vehiclesList;
    }

    public void setVehiclesList(List<Vehicle> vehiclesList) {
        this.vehiclesList = vehiclesList;
    }
}
