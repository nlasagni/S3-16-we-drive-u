package com.wedriveu.vehicle;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.wedriveu.services.shared.utilities.Util;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * Created by Michele on 18/07/2017.
 */
public class ServerVehicleRabbitMQ {
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private String licensePlate;

    public ServerVehicleRabbitMQ(String licensePlate) throws IOException {
        this.licensePlate = licensePlate;
        factory = new ConnectionFactory();
        factory.setHost("uniboguys.duckdns.org");
        factory.setPassword("FmzevdBBmpcdvPHLDJQR");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(this.licensePlate, false, false, false, null);
        channel.queueDeclare(this.licensePlate + Util.VEHICLE_TO_SERVICE, false, false, false, null);
        channel.basicQos(1);
    }

    public void server() throws IOException {
        System.out.println(" [x] Awaiting RPC requests");
        Consumer vehicle = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {

                String response = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + response + "'");
                channel.basicPublish("", licensePlate + Util.VEHICLE_TO_SERVICE, null, "100.0".getBytes());
            }
        };
        channel.basicConsume(licensePlate, true, vehicle);
    }

}
