package com.weriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;

/**
 * @author Michele Donati on 11/08/2017.
 */

/**
 * This interface models the verticle of the vehicle that is used to send the "Arrived to destination" notifies to the
 * service.
 */
public interface VehicleVerticleArrivedNotify {
    /**
     * This method permits to send the notify "Arrived to destination" to the service.
     * @param notify This indicates the notify object to send to the service.
     */
    void sendArrivedNotify(ArrivedNotify notify);
}
