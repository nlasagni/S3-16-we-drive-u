package com.wedriveu.services.vehicle.callback;

import com.wedriveu.services.vehicle.entity.Vehicle;

import java.util.List;

public interface WaitForAllCallback {
    void onRequestWaitForAllComplete(List<Vehicle> allVehicles);
}
