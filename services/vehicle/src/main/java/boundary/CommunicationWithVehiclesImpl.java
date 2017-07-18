package boundary;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

import java.io.IOException;

/**
 * Created by Stefano Bernagozzi on 17/07/2017.
 */
public class CommunicationWithVehiclesImpl implements CommunicationWithVehicles {
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public CommunicationWithVehiclesImpl() {
        factory = new ConnectionFactory();
        factory.setPassword("FmzevdBBmpcdvPHLDJQR");
        factory.setHost("46.101.44.210");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public double requestBatteryPercentage(String licensePlate) {
        double batteryPercentage = -1;
        channel.queueDeclare(licensePlate, false, false, false, null);
        String message = "Hello World!";
        channel.basicPublish("", licensePlate, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(licensePlate, true, consumer);
        return batteryPercentage;
    }
}
