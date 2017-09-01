package com.wedriveu.services.vehicle.entity;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.util.Position;

import java.util.Date;
import java.util.List;

/**
 * This inteface models the <em>Vehicle' database domain</em>.
 *
 * @author Michele Donati
 * @author Marco Baldassarri
 * @author Nicola Lasagni
 */
public interface VehicleStore {

    /**
     * A new vehicle is being added to the json file.
     * if the vehicle already exists (the licencePlace is already there)
     * it sends back a message containing a negative boolean, or positive otherwise.
     *
     * @param vehicle Indicates the vehicle the will be added to the db.
     * @return A boolean indicating the success or failure of this operation.
     */
    boolean addVehicle(Vehicle vehicle);

    /**
     * Gets a vehicle from the store.
     *
     * @param licensePlate The vehicle licence plate
     * @return The vehicle or {@code null} if no vehicle is found
     */
    Vehicle getVehicle(String licensePlate);

    /**
     * Searches all available vehicles which are inside the specified {@code minRange} and {@code maxRange}
     * of a {@code sourcePosition}.
     *
     * @param sourcePosition The source position
     * @param minRange The minimum range
     * @param maxRange The maximum range
     * @return The list of available vehicles or an empty list if no available vehicle is found.
     */
    List<Vehicle> getAllAvailableVehiclesInRange(Position sourcePosition, double minRange, double maxRange);

    /**
     * Gets all the vehicles in the store.
     *
     * @return Return the entire list of vehicles in the db.
     */
    List<Vehicle> getVehicleList();

    /**
     * Delete all the vehicles from the store.
     */
    void deleteAllVehicles();

    /**
     * Updates vehicle data.
     *
     * @param carLicencePlate Indicates the id of the vehicle to update.
     * @param state           Indicates the new state to update.
     * @param position        Indicates the new position to update.
     * @param lastUpdate      Indicates the date of this update.
     */
    void updateVehicleInVehicleList(String carLicencePlate, String state, Position position, Date lastUpdate);

}
