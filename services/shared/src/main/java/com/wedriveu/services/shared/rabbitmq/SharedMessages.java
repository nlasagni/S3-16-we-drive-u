package com.wedriveu.services.shared.rabbitmq;

/**
 * @author Marco Baldassarri
 * @since 11/08/2017
 */
public interface SharedMessages {

    interface VehicleService {
        String BIND_EXCHANGE = "service.bind.exchange";
        String EXCHANGE_BINDED = "exchange.binded";
    }

}
