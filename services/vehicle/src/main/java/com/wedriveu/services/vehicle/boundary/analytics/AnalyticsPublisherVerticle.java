package com.wedriveu.services.vehicle.boundary.analytics;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLES_RESPONSE_ALL;


/**
 * Vert.x RabbitMQ Publisher for Analytics Service response.
 * Once all the Vehicles are retrieved, this publisher sends back to the
 * Analytics Service the entire list of Vehicles.
 *
 * @author Marco Baldassarri on 15/08/2017.
 */
public class AnalyticsPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.VehicleStore.GET_VEHICLE_LIST_COMPLETED, this::sendListToAnalytics);
        super.start(startFuture);
    }

    private void sendListToAnalytics(Message message) {
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                ANALYTICS_VEHICLES_RESPONSE_ALL,
                ((JsonObject) message.body()), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(AnalyticsPublisherVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
