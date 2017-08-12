package com.wedriveu.services.analytics.boundary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.utilities.Position;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListObject {
    public ArrayList<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(ArrayList<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @JsonProperty ("vehicleList")
    private ArrayList<Vehicle> vehicleList;

    public VehicleListObject () {
        vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle("MACCHINA1",
                "broken",
                new Position(10.2, 13.2),
                new Date(2017, 11, 30, 12, 37, 43)));

    }
}
