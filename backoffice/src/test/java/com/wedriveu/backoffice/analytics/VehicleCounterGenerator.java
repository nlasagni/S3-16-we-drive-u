package com.wedriveu.backoffice.analytics;

import com.wedriveu.shared.rabbitmq.message.VehicleCounter;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleCounterGenerator {
    public static VehicleCounter getVehicleCounter() {
        VehicleCounter vehicleCounter = new VehicleCounter();
        vehicleCounter.increaseRecharging();
        vehicleCounter.increaseRecharging();
        vehicleCounter.increaseBroken();
        vehicleCounter.increaseAvailable();
        vehicleCounter.increaseAvailable();
        vehicleCounter.increaseNetworkIssues();
        return vehicleCounter;
    }
}
