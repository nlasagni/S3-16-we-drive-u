package com.wedriveu.shared.rabbitmq.message;

/**
 * A change booking response to the the vehicle service that has to substitute a vehicle for the user.
 *
 * @author Nicola Lasagni on 18/08/2017.
 */
public class ChangeBookingResponse {

    private boolean successful;
    private String username;
    private String licencePlate;

    /**
     * Is successful boolean.
     *
     * @return A boolean indicating the success of this operation
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Sets successful.
     *
     * @param successful the successful of this response result
     */
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
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
        ChangeBookingResponse response = (ChangeBookingResponse) o;
        return successful == response.successful &&
                (username != null ? username.equals(response.username) : response.username == null) &&
                (licencePlate != null ? licencePlate.equals(response.licencePlate) : response.licencePlate == null);
    }

    @Override
    public int hashCode() {
        int result = (successful ? 1 : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (licencePlate != null ? licencePlate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChangeBookingResponse{" +
                "successful=" + successful +
                ", username='" + username + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                '}';
    }
}
