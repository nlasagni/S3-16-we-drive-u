package com.wedriveu.services.vehicle.boundary.util.mock;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.FindBookingPositionsResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import io.vertx.core.Future;

/**
 * A {@link VerticleConsumer} used for test purposes that receives
 * {@link Constants.RabbitMQ.RoutingKey#FIND_BOOKING_POSITION_REQUEST} messages and reply with
 * a {@link FindBookingPositionsResponse}.
 *
 * @author Nicola Lasagni on 22/08/2017.
 */
public class FindBookingPositionsMockVerticle extends VerticleConsumer {

    private static final String EVENT_BUS_ADDRESS =
            FindBookingPositionsMockVerticle.class.getCanonicalName() + ".eventBus";
    private static final String QUEUE =
            FindBookingPositionsMockVerticle.class.getCanonicalName() + ".queue";
    private static final double USER_LATITUDE = 44.138519;
    private static final double USER_LONGITUDE = 12.241193;
    private static final double DESTINATION_LATITUDE = 44.222603;
    private static final double DESTINATION_LONGITUDE = 12.038920;
    private static final Position USER_POSITION =
            new Position(USER_LATITUDE, USER_LONGITUDE);
    private static final Position DESTINATION_POSITION =
            new Position(DESTINATION_LATITUDE, DESTINATION_LONGITUDE);

    private String username;
    private String vehicleLicensePlate;

    /**
     * Instantiates a new FindBookingPositionsMockVerticle.
     *
     * @param username            the username used to create the response
     * @param vehicleLicensePlate the vehicle license plate used to create the response
     */
    public FindBookingPositionsMockVerticle(String username, String vehicleLicensePlate) {
        super(QUEUE + username + vehicleLicensePlate);
        this.username = username;
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.FIND_BOOKING_POSITION_REQUEST,
                EVENT_BUS_ADDRESS,
                startFuture);
    }

    @Override
    public void registerConsumer(String eventBusAddress) {
        eventBus.consumer(eventBusAddress, msg -> {
            client.basicPublish(Constants.RabbitMQ.Exchanges.BOOKING,
                    Constants.RabbitMQ.RoutingKey.FIND_BOOKING_POSITION_RESPONSE,
                    VertxJsonMapper.mapInBodyFrom(createResponse()),
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(this.getClass().getSimpleName(), onPublish.cause());
                        }
                    });
        });
    }

    private FindBookingPositionsResponse createResponse() {
        FindBookingPositionsResponse response = new FindBookingPositionsResponse();
        response.setSuccessful(true);
        response.setLicensePlate(vehicleLicensePlate);
        response.setUsername(username);
        response.setUserPosition(USER_POSITION);
        response.setDestinationPosition(DESTINATION_POSITION);
        return response;
    }

}
