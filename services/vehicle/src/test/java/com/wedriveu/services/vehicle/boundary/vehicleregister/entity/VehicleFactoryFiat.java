package com.wedriveu.services.vehicle.boundary.vehicleregister.entity;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;

import java.util.Date;

/**
 * @author Marco on 02/08/2017.
 */
public class VehicleFactoryFiat implements VehicleFactory {

    private static final String LICENCE_PLATE = "BBB";
    //rome
    private static final double LATITUDE = 41.903319;
    private static final double LONGITUDE = 12.496441;
    private static final Position POSITION = new Position(LATITUDE, LONGITUDE);
    private static final Date LAST_UPDATE = new Date();
    private static final String NAME = "Fiat 500";
    private static final String DESCRIPTION = "Italians Do it Better";
    private static final String IMAGE_URL = "https://tinyurl.com/y7kbefj9";

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
