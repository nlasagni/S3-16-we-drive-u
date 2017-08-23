package com.wedriveu.backoffice.model;

import com.wedriveu.backoffice.view.MapViewerJavaFX;
import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import io.vertx.core.Future;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;


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

    public void generateMap(List<Booking> list) {
        Application.launch(MapViewerJavaFX.class, generateMapParameters(list));
    }

    private String generateMapParameters(List<Booking> bookings) {
        String parameters = "";
        for (Booking book: bookings) {
            parameters = parameters + "new google.maps.Marker({" +
                    "position: {lat: "
                    +book.getUserPosition().getLatitude() +
                    ", lng:" +
                    +book.getUserPosition().getLongitude() +
                    "}," +
                    "map: map," +
                    "icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png'" +
                    "});" +
                    "new google.maps.Marker({" +
                    "position: {lat: " +
                    book.getDestinationPosition().getLatitude() +
                    ", lng: " +
                    book.getUserPosition().getLongitude() +
                    "}," +
                    "map: map," +
                    "icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'" +
                    "});";
        }
        return parameters;
    }
}
