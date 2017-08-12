package com.wedriveu.services.vehicle.boundary.vehicleregister.entity;

import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.entity.Vehicle;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by Marco on 02/08/2017.
 */
public class VehicleFactoryMini implements VehicleFactory {

    private static final String LICENCE_PLATE = "AAA";
    private static final String STATUS = "available";
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
        vehicle.setCarLicencePlate(LICENCE_PLATE);
        vehicle.setStatus(STATUS);
        vehicle.setPosition(POSITION);
        vehicle.setLastUpdate(LAST_UPDATE);
        vehicle.setName(NAME);
        vehicle.setDescription(DESCRIPTION);
        try {
            URL url = new URL(IMAGE_URL);
            vehicle.setImageUrl(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return vehicle;
    }
}
