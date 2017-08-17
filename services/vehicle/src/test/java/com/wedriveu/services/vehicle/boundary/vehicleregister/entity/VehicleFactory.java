package com.wedriveu.services.vehicle.boundary.vehicleregister.entity;


import com.wedriveu.services.shared.entity.Vehicle;

/**
 * Simple factory pattern to get different Vehicle implementations.
 *
 * @author Marco Baldassarri on 02/08/2017.
 */
public interface VehicleFactory {

    Vehicle getVehicle();

}
