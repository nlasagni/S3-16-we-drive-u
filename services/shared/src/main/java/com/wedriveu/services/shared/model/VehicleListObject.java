package com.wedriveu.services.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListObject {

    private ArrayList<Vehicle> vehicleList;

    public ArrayList<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public VehicleListObject (@JsonProperty ("vehicleList") ArrayList<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleListObject that = (VehicleListObject) o;

        return getVehicleList() != null ? getVehicleList().equals(that.getVehicleList()) : that.getVehicleList() == null;
    }

    @Override
    public int hashCode() {
        return getVehicleList() != null ? getVehicleList().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "VehicleListObject{" +
                "vehicleList=" + vehicleList +
                '}';
    }
}
