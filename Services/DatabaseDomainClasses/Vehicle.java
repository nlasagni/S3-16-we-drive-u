package DatabaseDomainClasses;

import java.util.Date;

/**
 * Created by unibo on 07/07/2017.
 */
public class Vehicle {
    private String carLicencePlate;
    /* the state can be:
        broken
        available
        busy
        ...
     */
    private String state; //to check
    private Double latitude;
    private Double longitude;
    private Date lastUpdate;

    public Vehicle(String carLicencePlate, String state, Double latitude, Double longitude, Date lastUpdate) {
        this.carLicencePlate = carLicencePlate;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdate = lastUpdate;
    }

    public String getCarLicencePlate() {
        return carLicencePlate;
    }

    public void setCarLicencePlate(String carLicencePlate) {
        this.carLicencePlate = carLicencePlate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
