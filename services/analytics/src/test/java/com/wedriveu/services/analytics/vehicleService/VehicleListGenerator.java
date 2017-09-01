package com.wedriveu.services.analytics.vehicleService;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListGenerator {
    public static  List<Vehicle> getVehicleList() {
        List<Vehicle> vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle("MACCHINA1",
                Constants.Vehicle.STATUS_BROKEN_STOLEN,
                new Position(10.2, 13.2),
                new Date(2017, 11, 30, 12, 37, 43)));
        vehicleList.add(new Vehicle("MACCHINA2",
                Constants.Vehicle.STATUS_AVAILABLE,
                new Position(11.2, 14.2),
                new Date(2017, 10, 28, 11, 43, 12)));
        vehicleList.add(new Vehicle("MACCHINA3",
                Constants.Vehicle.STATUS_BOOKED,
                new Position(15.2, 13.2),
                new Date(2017, 9, 26, 10, 56, 46)));
        vehicleList.add(new Vehicle("MACCHINA4",
                Constants.Vehicle.STATUS_RECHARGING,
                new Position(13.2, 16.2),
                new Date(2017, 8, 24, 9, 37, 22)));
        return vehicleList;
    }

    public  static VehicleCounter getVehicleCounter() {
        VehicleCounter vehicleCounter = new VehicleCounter();
        vehicleCounter.increaseBooked();
        vehicleCounter.increaseAvailable();
        vehicleCounter.increaseBroken();
        vehicleCounter.increaseRecharging();
        return vehicleCounter;
    }
}
