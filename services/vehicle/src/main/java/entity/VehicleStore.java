package entity; /**
 * Created by Michele on 12/07/2017.
 */

import entity.Vehicle;

/**
 * @author Michele Donati
 * This inteface models the <em>entity.Vehicle' database domain</em>.
 */
public interface VehicleStore {

    /**
     * Maps a <em>entity.Vehicle</em> object in a Json object.
     */
    public void mapEntityToJson();

    /**
     *
     * @param carLicencePlate Identifies the <em>entity.Vehicle</em>'s <em>carLicencePlate</em> that must be retreived.
     * @return Returns the <em>entity.Vehicle</em>, only if founded.
     */
    public Vehicle getVehicle(String carLicencePlate);

}
