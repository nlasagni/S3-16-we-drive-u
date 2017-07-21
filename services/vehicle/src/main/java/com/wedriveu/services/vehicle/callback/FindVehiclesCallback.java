package com.wedriveu.services.vehicle.callback;

import com.wedriveu.services.vehicle.entity.Vehicle;

import java.util.List;

public interface FindVehiclesCallback {
    void listAllEligiblesVehiclesCallback(List<Vehicle> eligibles);
}
