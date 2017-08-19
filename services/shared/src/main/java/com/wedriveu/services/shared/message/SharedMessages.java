package com.wedriveu.services.shared.message;

/**
 * Shared Vert.x Verticle messages used by all the Services set.
 *
 * @author Marco Baldassarri
 * @since 11/08/2017
 */
public interface SharedMessages {

    /**
     * Verticle exchange messages addresses for the VehicleService. They are being used at the VehicleService
     * startup in order to bind the proper exchange before deploying the application Verticles.
     */
    interface VehicleService {
        String BIND_EXCHANGE = "service.bind.exchange";
        String EXCHANGE_BINDED = "exchange.binded";
    }

}
