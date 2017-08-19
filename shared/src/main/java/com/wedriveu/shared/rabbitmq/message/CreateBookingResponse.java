package com.wedriveu.shared.rabbitmq.message;

/**
 * A booking response to a client that wants to travel from its position
 * to a destination position with a vehicle previously selected by the system.
 *
 * @author Nicola Lasagni on 18/08/2017.
 */
public class CreateBookingResponse {

    private boolean success;
    private String errorMessage;
    private String licencePlate;
    private String driveTimeToUser;
    private String driveTimeToDestination;

    /**
     * Is success boolean.
     *
     * @return the boolean
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets success.
     *
     * @param success the success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * The error message for the operation.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message for the operation.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets vehicle licence plate.
     *
     * @return the licence plate
     */
    public String getLicencePlate() {
        return licencePlate;
    }


    /**
     * Sets licence plate.
     *
     * @param licencePlate the licence plate
     */
    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    /**
     * Gets drive time to user.
     *
     * @return the drive time to user
     */
    public String getDriveTimeToUser() {
        return driveTimeToUser;
    }

    /**
     * Sets drive time to user.
     *
     * @param driveTimeToUser the drive time to user
     */
    public void setDriveTimeToUser(String driveTimeToUser) {
        this.driveTimeToUser = driveTimeToUser;
    }

    /**
     * Gets drive time to destination.
     *
     * @return the drive time to destination
     */
    public String getDriveTimeToDestination() {
        return driveTimeToDestination;
    }

    /**
     * Sets drive time to destination.
     *
     * @param driveTimeToDestination the drive time to destination
     */
    public void setDriveTimeToDestination(String driveTimeToDestination) {
        this.driveTimeToDestination = driveTimeToDestination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateBookingResponse)) {
            return false;
        }
        CreateBookingResponse that = (CreateBookingResponse) o;
        return success == that.success &&
                (errorMessage != null ? errorMessage.equals(that.errorMessage) : that.errorMessage == null) &&
                (licencePlate != null ? licencePlate.equals(that.licencePlate) : that.licencePlate == null) &&
                (driveTimeToUser != null
                        ? driveTimeToUser.equals(that.driveTimeToUser)
                        : that.driveTimeToUser == null) &&
                (driveTimeToDestination != null
                        ? driveTimeToDestination.equals(that.driveTimeToDestination)
                        : that.driveTimeToDestination == null);
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + (licencePlate != null ? licencePlate.hashCode() : 0);
        result = 31 * result + (driveTimeToUser != null ? driveTimeToUser.hashCode() : 0);
        result = 31 * result + (driveTimeToDestination != null ? driveTimeToDestination.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "CreateBookingResponse{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", driveTimeToUser='" + driveTimeToUser + '\'' +
                ", driveTimeToDestination='" + driveTimeToDestination + '\'' +
                '}';
    }
}
