package com.wedriveu.backoffice.model;

import com.wedriveu.backoffice.view.MapViewerJavaFX;
import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import io.vertx.core.Future;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;


/**
 * The model of the backoffice that stores the vehicle counter and the backoffice id
 *
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

    /**
     * sets the backoffice id to the string in input and completes the future to allow others to retrieve the backoffice id
     *
     * @param backofficeID a string representing the backoffice id
     */
    public void setBackofficeID(String backofficeID) {
        this.backofficeID = backofficeID;
        registered.complete();
    }

    /**
     * gets the future that completes when the backoffice id is set
     *
     * @return a future that waits for backoffice id set to complete
     */
    public Future getFuture() {
        return registered;
    }

    /**
     * updates the vehicle counter stored in the model
     *
     * @param vehicleCounter the new vehicle counter
     */
    public void updateCounter(VehicleCounter vehicleCounter) {
        this.vehicleCounter = vehicleCounter;
    }
}
