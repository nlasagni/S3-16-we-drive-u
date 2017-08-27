package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.model.Vehicle;

import java.util.List;

/**
 * Contains all the data needed to perform a broken {@linkplain Vehicle} substitution.
 *
 * @author Michele on 19/08/2017.
 * @author Nicola on 26/08/2017.
 */
public class SubstitutionRequest {

    private SubstitutionCheck substitutionCheck;
    private Vehicle substitutionVehicle;
    private VehicleResponseCanDrive substitutionVehicleResponseCanDrive;
    private List<Vehicle> vehicleList;

    /**
     * Instantiates a new Substitution request.
     *
     * @param substitutionCheck                   the substitution check
     * @param substitutionVehicle                 the substitution vehicle
     * @param substitutionVehicleResponseCanDrive the substitution vehicle response can drive
     * @param vehicleList                         the available substitution vehicles
     */
    public SubstitutionRequest(@JsonProperty("substitutionCheck") SubstitutionCheck substitutionCheck,
                               @JsonProperty("substitutionVehicle") Vehicle substitutionVehicle,
                               @JsonProperty("substitutionVehicleResponseCanDrive") VehicleResponseCanDrive substitutionVehicleResponseCanDrive,
                               @JsonProperty("vehicleList") List<Vehicle> vehicleList) {
        this.substitutionCheck = substitutionCheck;
        this.substitutionVehicle = substitutionVehicle;
        this.substitutionVehicleResponseCanDrive = substitutionVehicleResponseCanDrive;
        this.vehicleList = vehicleList;
    }

    /**
     * Gets substitution check.
     *
     * @return the substitution check
     */
    public SubstitutionCheck getSubstitutionCheck() {
        return substitutionCheck;
    }

    /**
     * Gets the available substitution vehicles.
     *
     * @return the the available substitution vehicles
     */
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    /**
     * Gets substitution vehicle.
     *
     * @return the substitution vehicle
     */
    public Vehicle getSubstitutionVehicle() {
        return substitutionVehicle;
    }

    /**
     * Gets substitution vehicle response can drive.
     *
     * @return the substitution vehicle response can drive
     */
    public VehicleResponseCanDrive getSubstitutionVehicleResponseCanDrive() {
        return substitutionVehicleResponseCanDrive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubstitutionRequest)) {
            return false;
        }
        SubstitutionRequest request = (SubstitutionRequest) o;
        return equalsSubstitutionCheck(request) &&
                equalsSubstitutionVehicle(request) &&
                equalsSubstitutionVehicleResponseCanDrive(request);
    }

    private boolean equalsSubstitutionCheck(SubstitutionRequest request) {
        return substitutionCheck != null
                ? substitutionCheck.equals(request.substitutionCheck)
                : request.substitutionCheck == null;
    }

    private boolean equalsSubstitutionVehicle(SubstitutionRequest request) {
        return substitutionVehicle != null
                ? substitutionVehicle.equals(request.substitutionVehicle)
                : request.substitutionVehicle == null;
    }

    private boolean equalsSubstitutionVehicleResponseCanDrive(SubstitutionRequest request) {
        return substitutionVehicleResponseCanDrive != null
                ? substitutionVehicleResponseCanDrive.equals(request.substitutionVehicleResponseCanDrive)
                : request.substitutionVehicleResponseCanDrive == null;
    }

    @Override
    public int hashCode() {
        int result = substitutionCheck != null ? substitutionCheck.hashCode() : 0;
        result = 31 * result + (substitutionVehicle != null ? substitutionVehicle.hashCode() : 0);
        result = 31 * result + (substitutionVehicleResponseCanDrive != null ? substitutionVehicleResponseCanDrive.hashCode() : 0);
        result = 31 * result + (vehicleList != null ? vehicleList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubstitutionRequest{" +
                "substitutionCheck=" + substitutionCheck +
                ", substitutionVehicle=" + substitutionVehicle +
                ", substitutionVehicleResponseCanDrive=" + substitutionVehicleResponseCanDrive +
                ", vehicleList=" + vehicleList +
                '}';
    }
}
