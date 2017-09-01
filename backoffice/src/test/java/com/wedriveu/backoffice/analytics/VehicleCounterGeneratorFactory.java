package com.wedriveu.backoffice.analytics;

import com.wedriveu.shared.rabbitmq.message.VehicleCounter;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleCounterGeneratorFactory {

    public VehicleCounterGeneratorFactory () {    }

    /**
     * a factory for creating a vehicle counter
     *
     * @param available the number of available vehicles
     * @param booked the number of booked vehicles
     * @param broken the number of broken vehicles
     * @param networkIssues the number of vehicles with network issues
     * @param recharging the number of recharging vehicles
     * @return a vehicle counter with the numbers of the vehicles in input
     */
    public static VehicleCounter getVehicleCounter(int available,
                                                   int booked,
                                                   int broken,
                                                   int networkIssues,
                                                   int recharging) {
        VehicleCounter vehicleCounter = new VehicleCounter();
        for (int i = 0; i < available; i++) {
            vehicleCounter.increaseAvailable();
        }
        for (int i = 0; i < booked; i++) {
            vehicleCounter.increaseBooked();
        }
        for (int i = 0; i < broken; i++) {
            vehicleCounter.increaseBroken();
        }
        for (int i = 0; i < networkIssues; i++) {
            vehicleCounter.increaseNetworkIssues();
        }
        for (int i = 0; i < recharging; i++) {
            vehicleCounter.increaseRecharging();
        }
        return vehicleCounter;
    }
}
