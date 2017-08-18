package com.wedriveu.services.vehicle.entity;

<<<<<<< HEAD
import com.wedriveu.shared.rabbitmq.message.Vehicle;
=======
import com.wedriveu.services.shared.model.Vehicle;
>>>>>>> WDU_75_Booking_Service_RabbitMQ_Setup
import com.wedriveu.shared.util.Position;
import io.vertx.core.eventbus.Message;

import java.util.Date;
import java.util.List;

/**
<<<<<<< HEAD
 * This inteface models the <em>com.wedriveu.shared.rabbitmq.message.Vehicle' database domain</em>.
=======
 * This inteface models the <em>Vehicle' database domain</em>.
>>>>>>> WDU_75_Booking_Service_RabbitMQ_Setup
 *
 * @author Michele Donati
 * @author Marco Baldassarri
 */
public interface VehicleStore {

    /**
     * A new vehicle is being added to the json file. if the vehicle already exists (the licencePlace is already there)
     * it sends back a message containing a negative boolean, or positive otherwise.
     *
     * @param vehicle Indicates the vehicle the will be added to the db.
     */
    void addVehicle(Message vehicle);


    void getAllAvailableVehiclesInRange(Message message);

    /**
<<<<<<< HEAD
     * @param message Identifies the <em>com.wedriveu.shared.rabbitmq.message.Vehicle</em>'s <em>carLicencePlate</em> that must be retreived.
=======
     * @param message Identifies the <em>Vehicle</em>'s <em>carLicencePlate</em> that must be retreived.
>>>>>>> WDU_75_Booking_Service_RabbitMQ_Setup
     */
    void getVehicle(Message message);

    /**
     * @return Return the entire list of vehicles in the db.
     */
    List<Vehicle> getVehicleList();


    /**
     * Delete all the vehicles from the store.
     */
    void clearVehicles();

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
