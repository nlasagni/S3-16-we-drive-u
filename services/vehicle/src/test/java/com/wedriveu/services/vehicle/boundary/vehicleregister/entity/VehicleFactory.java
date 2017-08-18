package com.wedriveu.services.vehicle.boundary.vehicleregister.entity;


<<<<<<< HEAD
import com.wedriveu.shared.rabbitmq.message.Vehicle;
=======
import com.wedriveu.services.shared.model.Vehicle;
>>>>>>> WDU_75_Booking_Service_RabbitMQ_Setup

/**
 * Simple factory pattern to get different Vehicle implementations.
 * @author Marco Baldassarri on 02/08/2017.
 */
public interface VehicleFactory {

    Vehicle getVehicle();

}
