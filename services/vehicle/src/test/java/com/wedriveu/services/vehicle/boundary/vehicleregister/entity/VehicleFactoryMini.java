package com.wedriveu.services.vehicle.boundary.vehicleregister.entity;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;

import java.util.Date;

/**
 * @author Marco on 02/08/2017.
 */
public class VehicleFactoryMini implements VehicleFactory {

    private static final String LICENCE_PLATE = "1dc3c39c-b6e2-491c-9e9e-bd520767685a";
    //via sacchi 3, Cesena
    private static final double LATITUDE = 44.139761;
    private static final double LONGITUDE = 12.243219;
    private static final Position POSITION = new Position(LATITUDE, LONGITUDE);
    private static final Date LAST_UPDATE = new Date();
    private static final String NAME = "Mini Cooper D";
    private static final String DESCRIPTION = "Stick beauty with power";
    private static final String IMAGE_URL = "https://tinyurl.com/juxtlem";

    @SuppressWarnings("Duplicates")
    @Override
    public Vehicle getVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(LICENCE_PLATE);
        vehicle.setStatus(Constants.Vehicle.STATUS_AVAILABLE);
        vehicle.setPosition(POSITION);
        vehicle.setLastUpdate(LAST_UPDATE);
        vehicle.setName(NAME);
        vehicle.setDescription(DESCRIPTION);
        vehicle.setImageUrl(IMAGE_URL);
        return vehicle;
    }
}
