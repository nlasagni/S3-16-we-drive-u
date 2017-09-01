package com.wedriveu.services.analytics.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;


/**
 * A message class for the exchange inside the analytics service
 *
 * @author Stefano Bernagozzi
 */
public class MessageVehicleCounterWithID {
    @JsonProperty("backofficeID")
    String backofficeID;
    @JsonProperty("vehicleCounter")
    VehicleCounter vehicleCounter;

    /**
     * a message sent inside the Analytics service for responding to a vehicle counter request
     *
     * @param backofficeID the backoffice id of the applicant
     * @param vehicleCounter the updated vehicle counter
     */
    public MessageVehicleCounterWithID(@JsonProperty("backofficeID")
                                               String backofficeID,
                                       @JsonProperty("vehicleCounter")
                                               VehicleCounter vehicleCounter) {
        this.backofficeID = backofficeID;
        this.vehicleCounter = vehicleCounter;
    }

    /**
     * gets the backoffice id
     *
     * @return a string representing the backoffice id
     */
    public String getBackofficeID() {
        return backofficeID;
    }

    /**
     * gets the Vehicle counter
     *
     * @return the updated vehicle counter
     */
    public VehicleCounter getVehicleCounter() {
        return vehicleCounter;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageVehicleCounterWithID that = (MessageVehicleCounterWithID) o;

        if (getBackofficeID() != null ? !getBackofficeID().equals(that.getBackofficeID()) : that.getBackofficeID() != null)
            return false;
        return getVehicleCounter() != null ? getVehicleCounter().equals(that.getVehicleCounter()) : that.getVehicleCounter() == null;
    }

    @Override
    public int hashCode() {
        int result = getBackofficeID() != null ? getBackofficeID().hashCode() : 0;
        result = 31 * result + (getVehicleCounter() != null ? getVehicleCounter().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageVehicleCounterWithID{" +
                "backofficeID='" + backofficeID + '\'' +
                ", vehicleCounter=" + vehicleCounter +
                '}';
    }
}
