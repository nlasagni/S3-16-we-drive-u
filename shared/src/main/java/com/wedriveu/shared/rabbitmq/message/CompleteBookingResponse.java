package com.wedriveu.shared.rabbitmq.message;

/**
 * A complete booking response to the the vehicle service.
 *
 * @author Nicola Lasagni on 18/08/2017.
 */
public class CompleteBookingResponse {

    private boolean success;

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
     * @param success the success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompleteBookingResponse)) return false;
        CompleteBookingResponse that = (CompleteBookingResponse) o;
        return success == that.success;
    }

    @Override
    public int hashCode() {
        return (success ? 1 : 0);
    }

    @Override
    public String toString() {
        return "CompleteBookingResponse{" +
                "success=" + success +
                '}';
    }

}
