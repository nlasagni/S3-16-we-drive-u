package com.wedriveu.shared.rabbitmq.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * A {@linkplain RabbitMqCommunication} used to manage all main RabbitMQ interactions.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
public class RabbitMqCommunication {

    private Connection connection;
    private Channel channel;

    /**
     * Instantiates a new {@linkplain RabbitMqCommunication}.
     *
     * @param connection the connection
     * @param channel    the channel
     */
    RabbitMqCommunication(Connection connection, Channel channel) {
        this.connection = connection;
        this.channel = channel;
    }

    /**
     * Gets connection.
     *
     * @return the connection
     */
    Connection getConnection() {
        return connection;
    }

    /**
     * Gets channel.
     *
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RabbitMqCommunication)) {
            return false;
        }
        RabbitMqCommunication otherComm = (RabbitMqCommunication) other;
        return checkConnection(otherComm) && checkChannel(otherComm);
    }

    private boolean checkConnection(RabbitMqCommunication otherComm) {
        return connection != null ? connection.equals(otherComm.connection) : otherComm.connection == null;
    }

    private boolean checkChannel(RabbitMqCommunication otherComm) {
        return channel != null ? channel.equals(otherComm.channel) : otherComm.channel == null;
    }

    @Override
    public int hashCode() {
        int result = connection != null ? connection.hashCode() : 0;
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RabbitMqCommunication{" +
                "connection=" + connection +
                ", channel=" + channel +
                '}';
    }

}
