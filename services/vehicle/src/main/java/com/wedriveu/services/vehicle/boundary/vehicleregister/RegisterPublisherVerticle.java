package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.RegisterToServiceResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.REGISTER_RESULT;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE;


/**
 * Vert.x RabbitMQ Publisher for vehicle register response. Replies the vehicle with the adding result to the database.
 *
 * @author Marco Baldassarri on 08/08/2017.
 */
public class RegisterPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.VehicleStore.REGISTER_VEHICLE_COMPLETED, this::sendRegisterResponse);
        super.start(startFuture);
    }

    private void sendRegisterResponse(Message message) {
        JsonObject json = (JsonObject) message.body();
        String licencePlate = json.getString(Messages.Trip.LICENSE_PLATE);
        boolean registerResult = json.getBoolean(REGISTER_RESULT);
        RegisterToServiceResponse response = new RegisterToServiceResponse();
        response.setRegisterOk(registerResult);
        JsonObject resultJson = VertxJsonMapper.mapInBodyFrom(response);
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(REGISTER_RESPONSE, licencePlate),
                resultJson, onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(RegisterPublisherVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
