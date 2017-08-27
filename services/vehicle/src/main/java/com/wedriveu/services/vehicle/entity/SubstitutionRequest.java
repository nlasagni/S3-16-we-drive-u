package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.model.Vehicle;

import java.util.List;

/**
 * @author Michele on 19/08/2017.
 * @author Nicola on 26/08/2017.
 */
public class SubstitutionRequest {

    private SubstitutionCheck substitutionCheck;
    private Vehicle substitutionVehicle;
    private VehicleResponseCanDrive substitutionVehicleResponseCanDrive;
    private List<Vehicle> vehicleList;

    public SubstitutionRequest(@JsonProperty("substitutionCheck") SubstitutionCheck substitutionCheck,
                               @JsonProperty("substitutionVehicle") Vehicle substitutionVehicle,
                               @JsonProperty("substitutionVehicleResponseCanDrive") VehicleResponseCanDrive substitutionVehicleResponseCanDrive,
                               @JsonProperty("vehicleList") List<Vehicle> vehicleList) {
        this.substitutionCheck = substitutionCheck;
        this.substitutionVehicle = substitutionVehicle;
        this.substitutionVehicleResponseCanDrive = substitutionVehicleResponseCanDrive;
        this.vehicleList = vehicleList;
    }

    public SubstitutionCheck getSubstitutionCheck() {
        return substitutionCheck;
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public Vehicle getSubstitutionVehicle() {
        return substitutionVehicle;
    }

    public VehicleResponseCanDrive getSubstitutionVehicleResponseCanDrive() {
        return substitutionVehicleResponseCanDrive;
    }
}
