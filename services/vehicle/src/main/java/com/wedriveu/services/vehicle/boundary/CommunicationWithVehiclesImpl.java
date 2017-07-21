package com.wedriveu.services.vehicle.boundary;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.wedriveu.services.shared.utilities.Util;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.wedriveu.services.vehicle.callback.ListAllEligiblesCallback;

import java.io.IOException;

/**
 * Created by Stefano Bernagozzi on 17/07/2017.
 */
public class CommunicationWithVehiclesImpl implements CommunicationWithVehicles {
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private double percentage;


    public CommunicationWithVehiclesImpl() throws IOException {
        factory = new ConnectionFactory();
        factory.setHost("uniboguys.duckdns.org");
        factory.setPassword("FmzevdBBmpcdvPHLDJQR");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public void requestBatteryPercentage(final String licensePlate, ListAllEligiblesCallback listAllEligiblesCallback) throws IOException {
        System.out.println(licensePlate);
        channel.queueDeclare(licensePlate, false, false, false, null);
        channel.queueDeclare(licensePlate + Util.VEHICLE_TO_SERVICE,
                false,
                false,
                false,
                null);
        channel.basicQos(1);
        channel.basicPublish("", licensePlate, null, Util.REQUEST_BATTERY_PERCENTAGE.getBytes());
        Util.log(" [x] Sent '" + Util.REQUEST_BATTERY_PERCENTAGE + "'");

        Consumer service = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String response = new String(body, "UTF-8");
                Util.log(" [x] Received '" + response + "'");
                percentage = Double.parseDouble(response);
                listAllEligiblesCallback.onRequestBatteryPercentage(percentage);

            }

        };
        channel.basicConsume(licensePlate + Util.VEHICLE_TO_SERVICE, true, service);
    }

}
