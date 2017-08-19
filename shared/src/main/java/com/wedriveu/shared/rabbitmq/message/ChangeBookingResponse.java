package com.wedriveu.shared.rabbitmq.message;

/**
 * A change booking response to the the vehicle service that has to substitute a vehicle for the user.
 *
 * @author Nicola Lasagni on 18/08/2017.
 */
public class ChangeBookingResponse {

    private boolean success;
    private String licencePlate;

    /**
     * Is success boolean.
     *
     * @return A boolean indicating the success of this operation
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets success.
     *
     * @param success the success of this response result
     */
    public void setSuccess(boolean success) {
        this.success = success;
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
     * Sets vehicle licence plate.
     *
     * @param licencePlate the licence plate
     */
    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChangeBookingResponse)) {
            return false;
        }
        ChangeBookingResponse that = (ChangeBookingResponse) o;
        return success == that.success &&
                (licencePlate != null ? licencePlate.equals(that.licencePlate) : that.licencePlate == null);
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (licencePlate != null ? licencePlate.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "ChangeBookingResponse{" +
                "success=" + success +
                ", licencePlate='" + licencePlate + '\'' +
                '}';
    }
}
