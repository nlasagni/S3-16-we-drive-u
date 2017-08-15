package com.wedriveu.services.vehicle.rabbitmq;

import com.wedriveu.services.vehicle.boundary.nearest.NearestConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.nearest.VehicleFinderVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.RegisterConsumerVerticle;

/**
 * @author Michele on 22/07/2017.
 * @author Nicola Lasagni on 31/07/2017
 * @author Marco Baldassarri on 1/08/2017
 */
public interface Constants {

    String REGISTER_RESULT = "vehicleRegisterResult";

    //Vehicle Service Queues
    String VEHICLE_SERVICE_QUEUE_NEAREST = "service.vehicle.queue.nearest";
    String VEHICLE_SERVICE_QUEUE_FINDER = "service.vehicle.queue.finder.%s";
    String VEHICLE_SERVICE_QUEUE_REGISTER = "service.vehicle.queue.register";
    String NEAREST_EVENT_BUS_ADDRESS = NearestConsumerVerticle.class.getCanonicalName();
    String EVENT_BUS_FINDER_ADDRESS = VehicleFinderVerticle.class.getCanonicalName();
    String EVENT_BUS_REGISTER_ADDRESS = RegisterConsumerVerticle.class.getCanonicalName();

}
