package com.wedriveu.services.vehicle.entity; /**
 * Created by Michele on 12/07/2017.
 */

/**
 * @author Michele Donati
 * This inteface models the <em>com.wedriveu.services.vehicle.entity.Vehicle' database domain</em>.
 */
public interface VehicleStore {

    /**
     * Maps a <em>com.wedriveu.services.vehicle.entity.Vehicle</em> object in a Json object.
     */
    public void mapEntityToJson();

    /**
     *
     * @param carLicencePlate Identifies the <em>com.wedriveu.services.vehicle.entity.Vehicle</em>'s <em>carLicencePlate</em> that must be retreived.
     * @return Returns the <em>com.wedriveu.services.vehicle.entity.Vehicle</em>, only if founded.
     */
    public Vehicle getVehicle(String carLicencePlate);

}
