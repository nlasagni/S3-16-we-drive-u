package com.wedriveu.services.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleList {

    private List<Vehicle> vehicleList;

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public AnalyticsVehicleList(@JsonProperty("vehicleList") List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalyticsVehicleList that = (AnalyticsVehicleList) o;

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
