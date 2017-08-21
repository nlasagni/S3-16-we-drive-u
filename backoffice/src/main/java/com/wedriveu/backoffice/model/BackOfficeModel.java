package com.wedriveu.backoffice.model;

import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import io.vertx.core.Future;


/**
 * @author Stefano Bernagozzi
 */
public class BackOfficeModel {
    private String backofficeID;
    private Future registered;
    VehicleCounter vehicleCounter;

    public BackOfficeModel() {
        registered = Future.future();
    }

    public String getBackofficeID() {
        return backofficeID;
    }

    public void setBackofficeID(String backofficeID) {
        this.backofficeID = backofficeID;
        registered.complete();
    }

    public Future getFuture() {
        return registered;
    }

    public void updateCounter(VehicleCounter vehicleCounter) {
        this.vehicleCounter = vehicleCounter;
    }
}
