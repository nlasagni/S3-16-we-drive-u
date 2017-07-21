package com.wedriveu.services.vehicle.boundary;

import com.wedriveu.services.vehicle.callback.Callback;

import java.io.IOException;
/**
 * Created by stefano.bernagozzi on 17/07/2017.
 */
public interface CommunicationWithVehicles {
    public void requestBatteryPercentage(String licensePlate, Callback callback) throws IOException;

}
