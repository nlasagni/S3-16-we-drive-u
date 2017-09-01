package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.model.BackOfficeModel;
import com.wedriveu.backoffice.view.BackOfficeView;
import io.vertx.core.Vertx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.wedriveu.backoffice.util.ConstantsBackOffice.EventBus.BACKOFFICE_BOOKING_LIST_REQUEST_CONTROLLER;

/**
 * The button event listener for the registration of the backoffice
 *
 * @author Stefano Bernagozzi
 */
public class ButtonEventListenerBooking implements ActionListener {
    Vertx vertx;

    public ButtonEventListenerBooking(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        vertx.eventBus().send(BACKOFFICE_BOOKING_LIST_REQUEST_CONTROLLER, null);
    }

}