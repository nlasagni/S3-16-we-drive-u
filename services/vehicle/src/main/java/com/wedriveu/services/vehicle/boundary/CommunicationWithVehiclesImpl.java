package com.wedriveu.services.vehicle.boundary;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.callback.RequestCanDoJourneyCallback;

import java.io.IOException;

/**
 * Created by Stefano Bernagozzi on 17/07/2017.
 */
public class CommunicationWithVehiclesImpl implements CommunicationWithVehicles {
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public CommunicationWithVehiclesImpl() throws IOException {
        factory = new ConnectionFactory();
        factory.setHost(Constants.SERVER_HOST);
        factory.setPassword(Constants.SERVER_PASSWORD);
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public void requestCanDoJourney(final String licensePlate,
                                    double kilometersToDo,
                                    RequestCanDoJourneyCallback listAllEligiblesCallback) throws IOException {
        Log.log(licensePlate);
        channel.queueDeclare(licensePlate, false, false, false, null);
        channel.queueDeclare(licensePlate + Constants.VEHICLE_TO_SERVICE,
                false,
                false,
                false,
                null);
        channel.basicQos(1);
        channel.basicPublish("", licensePlate, null, String.valueOf(kilometersToDo).getBytes());
        Log.log(" [x] Sent '" +
                Constants.REQUEST_CAN_DO_JOURNEY +
                "'" +
                " with " +
                kilometersToDo +
                " kilometers to be done");

        Consumer service = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String response = new String(body, "UTF-8");
                listAllEligiblesCallback.onRequestCanDoJourney(Boolean.parseBoolean(response));
            }
        };
        channel.basicConsume(licensePlate + Constants.VEHICLE_TO_SERVICE, true, service);
    }

}
