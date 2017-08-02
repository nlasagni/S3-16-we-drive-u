package com.wedriveu.services.vehicle.consumers.nearest;

import com.wedriveu.services.shared.rabbitmq.BasicConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.consumers.nearest.manager.ManagerImpl;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.entity.Vehicle;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by Marco on 30/07/2017.
 */
public class NearestConsumerImpl extends BasicConsumer implements NearestConsumer {
    ArrayList<Vehicle> available;


    public NearestConsumerImpl() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }


    /*        FindVehicles finder = new FindVehiclesImpl();
        finder.listAllEligibleVehicles( new Position(43.158873, 13.720088),
                new Position(42.960979, 13.874647),
                available,
                new EligibleVehiclesControlImpl());*/



    @Override
    public void startVehicleService() throws IOException, TimeoutException {
        //createVehicles();
        setupRequests();
    }

    private void setupRequests() throws IOException, TimeoutException {
        BasicConsumer consumer = new NearestConsumerImpl();
        consumer.start(onStart -> {
            consumer.declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    consumer.bindQueueToExchange(Constants.VEHICLE_SERVICE_EXCHANGE,
                            Constants.ROUTING_KEY_VEHICLE, onBind -> {
                        if (onBind.succeeded()) {
                            consumer.registerConsumer(Constants.VEHICLE_SERVICE_EVENT_BUS);
                            consumer.basicConsume(Constants.VEHICLE_SERVICE_EVENT_BUS);
                        }
                    });
                }
            });
        });
    }

    @Override
    public void stopVehicleService() {

    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            JsonObject json = (JsonObject) msg.body();
            searchAvailableVehicles(json);
        });
    }

    private void searchAvailableVehicles(JsonObject json) {
        vertx.deployVerticle(new ManagerImpl(), managerDeployed -> {
            eventBus.send(Messages.NearestConsumer.AVAILABLE, json);
        });
    }

}
