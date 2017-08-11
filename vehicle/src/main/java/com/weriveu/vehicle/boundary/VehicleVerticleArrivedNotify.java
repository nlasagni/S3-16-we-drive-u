package com.weriveu.vehicle.boundary;

import com.wedriveu.shared.entity.ArrivedNotify;

/**
 * @author Michele Donati on 11/08/2017.
 */

public interface VehicleVerticleArrivedNotify {

    void sendArrivedNotify(ArrivedNotify notify);
}
