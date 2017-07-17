import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Michele on 12/07/2017.
 */
public class Vehicle {
    private String carLicencePlate;
    private String state;
    private Double latitude;
    private Double longitude;
    private Date lastUpdate;

    public Vehicle(@JsonProperty("carLicencePlate")String carLicencePlate,
                   @JsonProperty("state")String state,
                   @JsonProperty("latitude")Double latitude,
                   @JsonProperty("longitude")Double longitude,
                   @JsonProperty("lastUpdate")Date lastUpdate) {
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
