package com.wedriveu.services.vehicle.entity;

import com.wedriveu.services.shared.utilities.Position;
import io.vertx.core.eventbus.Message;

import java.util.Date;
import java.util.List;

/**
 * @author Michele Donati
 * @author Marco Baldassarri
 * This inteface models the <em>com.wedriveu.services.vehicle.entity.Vehicle' database domain</em>.
 */
public interface VehicleStore {

    /**
     * Maps a <em>com.wedriveu.services.vehicle.entity.Vehicle</em> object in a Json object.
     */
    void createVehiclesFile();

    /**
     * @param vehicle Indicates the vehicle the will be added to the db.
     */
    void addVehicle(Vehicle vehicle);


    void getAllAvailableVehicles(Message message);

    /**
     * @param message Identifies the <em>com.wedriveu.services.vehicle.entity.Vehicle</em>'s <em>carLicencePlate</em> that must be retreived.
     */
    void getVehicle(Message message);

    /**
     * @return Return the entire list of vehicles in the db.
     */
    List<Vehicle> getVehicleList();

    /**
     * @param carLicencePlate Indicates the id of the vehicle to update.
     * @param state           Indicates the new state to update.
     * @param position        Indicates the new position to update.
     * @param lastUpdate      Indicates the date of this update.
     */
    void updateVehicleInVehicleList(String carLicencePlate, String state, Position position, Date lastUpdate);

    /**
     * @param carLicencePlate Indicates the id of the vehicle to delete.
     */
    void deleteVehicleFromDb(String carLicencePlate);

    /**
     * @param carLicencePlateToDelete Indicates the id of the vehicle to replace.
     * @param replacementVehicle      Indicates the new vehicle for the replacement.
     */
    void replaceVehicleInDb(String carLicencePlateToDelete, Vehicle replacementVehicle);

    /**
     * @param carLicensePlate Indicates the id of the vehicle that i want to know if it exists.
     * @return Return True if the vehicle exists in the db, False otherwise.
     */
    boolean theVehicleIsInTheDb(String carLicensePlate);
}
