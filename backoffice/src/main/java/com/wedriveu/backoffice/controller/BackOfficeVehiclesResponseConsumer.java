package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_RESPONSE_VEHICLE_COUNTER;
import static com.wedriveu.shared.util.Constants.RabbitMQ;


/**
 * the class for recieving a vehicle list over rabbitMQ with vertx from the vehicle service
 *
 * @author Stefano Bernagozzi
 */
public class BackOfficeVehiclesResponseConsumer extends VerticleConsumer {
    private String backofficeId;
    private static final String ROUTING_KEY_BASE =
            String.format(ConstantsBackOffice.ROUTING_KEY_BASE_FORMAT,
                            Constants.RabbitMQ.Exchanges.ANALYTICS,
                            ANALYTICS_RESPONSE_VEHICLE_COUNTER);
    private boolean updates;

    /**
     * sets the backoffice id and if it is the update queue
     *
     * @param backofficeId the backoffice id
     * @param updates if it listens on the update queue or on his own queue
     */
    public BackOfficeVehiclesResponseConsumer(String backofficeId, boolean updates) {
        super(ROUTING_KEY_BASE + backofficeId + (updates ? ConstantsBackOffice.UPDATES_CONSTANT : ""));
        this.backofficeId = backofficeId;
        this.updates = updates;
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        if (updates) {
            startConsumerWithFuture(RabbitMQ.Exchanges.ANALYTICS,
                    ANALYTICS_RESPONSE_VEHICLE_COUNTER,
                    ConstantsBackOffice.EventBus.AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_NO_ID,
                    futureRetriever);
        } else {
            startConsumerWithFuture(RabbitMQ.Exchanges.ANALYTICS,
                    String.format(Constants.FORMAT_WITH_DOT, ANALYTICS_RESPONSE_VEHICLE_COUNTER , backofficeId),
                    ConstantsBackOffice.EventBus.AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_WITH_ID,
                    futureRetriever);
        }
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        vertx.eventBus().send(ConstantsBackOffice.EventBus.BACKOFFICE_CONTROLLER_VEHICLES, VertxJsonMapper.mapInBodyFrom(vehicleCounter));
    }
}
