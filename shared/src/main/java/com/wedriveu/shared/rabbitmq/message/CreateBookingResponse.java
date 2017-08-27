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
    private long userArrivalTime;
    private long destinationArrivalTime;

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
     * Gets the vehicle arrival time to user.
     *
     * @return the vehicle arrival time to user
     */
    public long getUserArrivalTime() {
        return userArrivalTime;
    }

    /**
     * Sets the vehicle arrival time to user.
     *
     * @param userArrivalTime the vehicle arrival time to user
     */
    public void setUserArrivalTime(long userArrivalTime) {
        this.userArrivalTime = userArrivalTime;
    }

    /**
     * Gets the vehicle arrival time to destination.
     *
     * @return the vehicle arrival time to destination
     */
    public long getDestinationArrivalTime() {
        return destinationArrivalTime;
    }

    /**
     * Sets the vehicle arrival time to destination.
     *
     * @param destinationArrivalTime the vehicle arrival time to destination
     */
    public void setDestinationArrivalTime(long destinationArrivalTime) {
        this.destinationArrivalTime = destinationArrivalTime;
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
                userArrivalTime == that.userArrivalTime &&
                destinationArrivalTime == that.destinationArrivalTime &&
                (errorMessage != null ? errorMessage.equals(that.errorMessage) : that.errorMessage == null) &&
                (licencePlate != null ? licencePlate.equals(that.licencePlate) : that.licencePlate == null);
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + (licencePlate != null ? licencePlate.hashCode() : 0);
        result = 31 * result + (int) (userArrivalTime ^ (userArrivalTime >>> 32));
        result = 31 * result + (int) (destinationArrivalTime ^ (destinationArrivalTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CreateBookingResponse{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", userArrivalTime=" + userArrivalTime +
                ", destinationArrivalTime=" + destinationArrivalTime +
                '}';
    }
}
