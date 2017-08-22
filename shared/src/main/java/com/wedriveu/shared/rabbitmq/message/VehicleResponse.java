package com.wedriveu.shared.rabbitmq.message;

/**
 *
 *  Describes the Vehicle as returned from the VehicleService.
 *  Some of the fields won't be used for the Android Application
 *
 *  @author Marco Baldassarri
 *  @author Nicola Lasagni
 */
public class VehicleResponse {

    private String licensePlate;
    private String vehicleName;
    private String description;
    private String pictureURL;
    private long arriveAtUserTime;
    private long arriveAtDestinationTime;
    private String notEligibleVehicleFound;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public long getArriveAtUserTime() {
        return arriveAtUserTime;
    }

    public void setArriveAtUserTime(long arriveAtUserTime) {
        this.arriveAtUserTime = arriveAtUserTime;
    }

    public long getArriveAtDestinationTime() {
        return arriveAtDestinationTime;
    }

    public void setArriveAtDestinationTime(long arriveAtDestinationTime) {
        this.arriveAtDestinationTime = arriveAtDestinationTime;
    }

    public String getNotEligibleVehicleFound() {
        return notEligibleVehicleFound;
    }

    public void setNotEligibleVehicleFound(String notEligibleVehicleFound) {
        this.notEligibleVehicleFound = notEligibleVehicleFound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VehicleResponse)) {
            return false;
        }
        VehicleResponse that = (VehicleResponse) o;
        return arriveAtUserTime == that.arriveAtUserTime &&
                arriveAtDestinationTime == that.arriveAtDestinationTime &&
                (licensePlate != null ? licensePlate.equals(that.licensePlate) : that.licensePlate == null) &&
                (vehicleName != null ? vehicleName.equals(that.vehicleName) : that.vehicleName == null) &&
                (description != null ? description.equals(that.description) : that.description == null) &&
                (pictureURL != null ? pictureURL.equals(that.pictureURL) : that.pictureURL == null) &&
                (notEligibleVehicleFound != null
                        ? notEligibleVehicleFound.equals(that.notEligibleVehicleFound)
                        : that.notEligibleVehicleFound == null);
    }

    @Override
    public int hashCode() {
        int result = licensePlate != null ? licensePlate.hashCode() : 0;
        result = 31 * result + (vehicleName != null ? vehicleName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (pictureURL != null ? pictureURL.hashCode() : 0);
        result = 31 * result + (int) (arriveAtUserTime ^ (arriveAtUserTime >>> 32));
        result = 31 * result + (int) (arriveAtDestinationTime ^ (arriveAtDestinationTime >>> 32));
        result = 31 * result + (notEligibleVehicleFound != null ? notEligibleVehicleFound.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleResponse{" +
                "licensePlate='" + licensePlate + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", description='" + description + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", arriveAtUserTime=" + arriveAtUserTime +
                ", arriveAtDestinationTime=" + arriveAtDestinationTime +
                ", notEligibleVehicleFound='" + notEligibleVehicleFound + '\'' +
                '}';
    }
}