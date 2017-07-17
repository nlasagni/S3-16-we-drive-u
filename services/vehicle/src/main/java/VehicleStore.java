/**
 * Created by Michele on 12/07/2017.
 */

/**
 * @author Michele Donati
 * This inteface models the <em>Vehicle' database domain</em>.
 */
public interface VehicleStore {

    /**
     * Maps a <em>Vehicle</em> object in a Json object.
     */
    public void mapVehiclesToJSon();

    /**
     *
     * @param carLicencePlate Identifies the <em>Vehicle</em>'s <em>carLicencePlate</em> that must be retreived.
     * @return Returns the <em>Vehicle</em>, only if founded.
     */
    public Vehicle getVehicle(String carLicencePlate);

}
