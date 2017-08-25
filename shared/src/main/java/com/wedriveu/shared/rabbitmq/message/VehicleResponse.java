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

    /**
     * @return the license plate of the vehicle
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * @param licensePlate the license plate of the vehicle
     */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /**
     * @return the name of the vehicle
     */
    public String getVehicleName() {
        return vehicleName;
    }

    /**
     * @param vehicleName the name of the vehicle
     */
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    /**
     * @return the description of the vehicle
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description of the vehicle
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the URL of the picture of the vehicle
     */
    public String getPictureURL() {
        return pictureURL;
    }

    /**
     * @param pictureURL the URL of the picture of the vehicle
     */
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    /**
     * @return the time when the vehicle has to arrive to the user
     */
    public long getArriveAtUserTime() {
        return arriveAtUserTime;
    }

    /**
     * @param arriveAtUserTime the time when the vehicle has to arrive to the user
     */
    public void setArriveAtUserTime(long arriveAtUserTime) {
        this.arriveAtUserTime = arriveAtUserTime;
    }

    /**
     * @return the time when the vehicle has to arrive to destination
     */
    public long getArriveAtDestinationTime() {
        return arriveAtDestinationTime;
    }

    /**
     * @param arriveAtDestinationTime the time when the vehicle has to arrive to destination
     */
    public void setArriveAtDestinationTime(long arriveAtDestinationTime) {
        this.arriveAtDestinationTime = arriveAtDestinationTime;
    }

    /**
     * @return a string that contains if there are no eligible vehicles
     */
    public String getNotEligibleVehicleFound() {
        return notEligibleVehicleFound;
    }

    /**
     * @param notEligibleVehicleFound a string that contains if there are no eligible vehicles
     */
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