package com.weriveu.vehicle.boundary;

import com.wedriveu.shared.util.Position;

import java.util.concurrent.Semaphore;

/**
 * @author Michele Donati on 17/08/2017.
 */

public interface VehicleVerticleForUser {

    void enterInVehicle(Position destinationPosition);

}