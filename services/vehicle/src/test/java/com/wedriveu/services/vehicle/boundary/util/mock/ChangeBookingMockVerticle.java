package com.wedriveu.services.vehicle.boundary.util.mock;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.ChangeBookingResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;

/**
 * A {@link VerticleConsumer} used for test purposes that receives
 * {@link Constants.RabbitMQ.RoutingKey#CHANGE_BOOKING_REQUEST} messages and reply with
 * a {@link ChangeBookingResponse}.
 *
 * @author Nicola Lasagni on 22/08/2017.
 */
public class ChangeBookingMockVerticle extends VerticleConsumer {

    private static final String EVENT_BUS_ADDRESS =
            ChangeBookingMockVerticle.class.getCanonicalName() + ".eventBus";
    private static final String QUEUE =
            ChangeBookingMockVerticle.class.getCanonicalName() + ".queue";

    private String username;
    private String vehicleLicensePlate;

    /**
     * Instantiates a new Change booking mock verticle.
     *
     * @param username            the username used to create the response
     * @param vehicleLicensePlate the vehicle license plate used to create the response
     */
    public ChangeBookingMockVerticle(String username, String vehicleLicensePlate) {
        super(QUEUE + username + vehicleLicensePlate);
        this.username = username;
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.CHANGE_BOOKING_REQUEST,
                EVENT_BUS_ADDRESS,
                startFuture);
    }

    @Override
    public void registerConsumer(String eventBusAddress) {
        eventBus.consumer(eventBusAddress, msg -> {
            client.basicPublish(Constants.RabbitMQ.Exchanges.BOOKING,
                    Constants.RabbitMQ.RoutingKey.CHANGE_BOOKING_RESPONSE,
                    VertxJsonMapper.mapInBodyFrom(createResponse()),
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(this.getClass().getSimpleName(), onPublish.cause());
                        }
                    });
        });
    }

    private ChangeBookingResponse createResponse() {
        ChangeBookingResponse response = new ChangeBookingResponse();
        response.setSuccessful(true);
        response.setLicencePlate(vehicleLicensePlate);
        response.setUsername(username);
        return response;
    }

}
