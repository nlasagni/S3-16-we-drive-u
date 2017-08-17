package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.START_DRIVING;


/**
 * Vert.x RabbitMQ Publisher for Analytics Service response.
 * Once all the Vehicles are retrieved, this publisher sends back to the
 * Analytics Service the entire list of Vehicles.
 *
 * @author Marco Baldassarri on 15/08/2017.
 */
public class AnalyticsDrivingPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.BookingControl.PUBLISH_RESULT, this::notifyDrivingStarted);
        super.start(startFuture);
    }

    private void notifyDrivingStarted(Message message) {
        BookVehicleResponse vehicleResponse = (BookVehicleResponse) message.body();
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                START_DRIVING,
                VertxJsonMapper.mapInBodyFrom(vehicleResponse), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(AnalyticsDrivingPublisherVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
