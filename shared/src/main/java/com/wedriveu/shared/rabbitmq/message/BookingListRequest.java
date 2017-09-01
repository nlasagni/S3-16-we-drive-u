package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Stefano Bernagozzi
 */
public class BookingListRequest {

    private String backofficeId;

    /**
     * gets the backoffice Id of the sender
     *
     * @return a string containing the backoffice id
     */
    public String getBackofficeId() {
        return backofficeId;
    }

    /**
     * sets the backoffice id
     *
     * @param backofficeId a string that represents the backoffice id
     */
    public void setBackofficeId(String backofficeId) {
        this.backofficeId = backofficeId;
    }
}
