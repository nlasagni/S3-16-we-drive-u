package com.wedriveu.services.vehicle.boundary.util.mock;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;

/**
 * A {@link VerticleConsumer} used for test purposes that receives
 * {@link Constants.RabbitMQ.RoutingKey#BOOK_VEHICLE_REQUEST} messages and reply with
 * a {@link BookVehicleResponse}.
 *
 * @author Nicola Lasagni on 22/08/2017.
 */
public class VehicleBookingMockVerticle extends VerticleConsumer {

    private static final String EVENT_BUS_ADDRESS =
            VehicleBookingMockVerticle.class.getCanonicalName() + ".eventBus";
    private static final String QUEUE =
            VehicleBookingMockVerticle.class.getCanonicalName() + ".queue";
    private static final double SPEED = 50.0;

    private String username;
    private String vehicleLicensePlate;

    /**
     * Instantiates a new VehicleBookingMockVerticle.
     *
     * @param username            the username used to create the response and to publish it
     * @param vehicleLicensePlate the vehicle license plate used to receive messages and to create the response
     */
    public VehicleBookingMockVerticle(String username, String vehicleLicensePlate) {
        super(QUEUE + username + vehicleLicensePlate);
        this.username = username;
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.BOOK_VEHICLE_REQUEST, vehicleLicensePlate),
                EVENT_BUS_ADDRESS,
                startFuture);
    }

    @Override
    public void registerConsumer(String eventBusAddress) {
        eventBus.consumer(eventBusAddress, msg -> {
            client.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                    String.format(Constants.RabbitMQ.RoutingKey.BOOK_VEHICLE_RESPONSE, username),
                    VertxJsonMapper.mapInBodyFrom(createResponse()),
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(this.getClass().getSimpleName(), onPublish.cause());
                        }
                    });
        });
    }

    private BookVehicleResponse createResponse() {
        BookVehicleResponse response = new BookVehicleResponse();
        response.setBooked(true);
        response.setLicensePlate(vehicleLicensePlate);
        response.setSpeed(SPEED);
        return response;
    }

}
