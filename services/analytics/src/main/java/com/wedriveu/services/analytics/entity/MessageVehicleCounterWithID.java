package com.wedriveu.services.analytics.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;


/**
 * @author Stefano Bernagozzi
 */
public class MessageVehicleCounterWithID {
    @JsonProperty("backofficeID")
    String backofficeID;
    @JsonProperty("vehicleCounter")
    VehicleCounter vehicleCounter;

    public MessageVehicleCounterWithID(    @JsonProperty("backofficeID")
                                                   String backofficeID,
                                           @JsonProperty("vehicleCounter")
                                                   VehicleCounter vehicleCounter){
        this.backofficeID = backofficeID;
        this.vehicleCounter = vehicleCounter;
    }

    public String getBackofficeID() {
        return backofficeID;
    }

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
