package com.wedriveu.shared.rabbitmq.communication.config;

import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;

/**
 * The configuration needed to set up a {@linkplain RabbitMqCommunication}.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
public class RabbitMqCommunicationConfig {

    private String host;
    private String password;
    private ExceptionHandler exceptionHandler;

    /**
     * Instantiates a new RabbitMqCommunicationConfig.
     */
    private RabbitMqCommunicationConfig() {}

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets exception handler.
     *
     * @return the exception handler.
     */
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    private void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RabbitMqCommunicationConfig)) {
            return false;
        }
        RabbitMqCommunicationConfig otherConfig = (RabbitMqCommunicationConfig) other;
        return checkHost(otherConfig) && checkPassword(otherConfig) && checkExceptionHandler(otherConfig);
    }

    private boolean checkHost(RabbitMqCommunicationConfig otherConfig) {
        return host != null ? host.equals(otherConfig.host) : otherConfig.host == null;
    }

    private boolean checkPassword(RabbitMqCommunicationConfig otherConfig) {
        return password != null ? password.equals(otherConfig.password) : otherConfig.password == null;
    }

    private boolean checkExceptionHandler(RabbitMqCommunicationConfig otherConfig) {
        return exceptionHandler != null
                ? exceptionHandler.equals(otherConfig.exceptionHandler)
                : otherConfig.exceptionHandler == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (exceptionHandler != null ? exceptionHandler.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RabbitMqCommunicationConfig{" +
                "host='" + host + '\'' +
                ", password='" + password + '\'' +
                ", exceptionHandler=" + exceptionHandler +
                '}';
    }

    /**
     * The Builder of a {@linkplain RabbitMqCommunicationConfig}.
     */
    public static final class Builder {

        private RabbitMqCommunicationConfig config;

        public Builder() {
            config = new RabbitMqCommunicationConfig();
        }

        /**
         * Host builder.
         *
         * @param host the config host
         * @return the builder
         */
        public Builder host(String host) {
            config.setHost(host);
            return this;
        }

        /**
         * Password builder.
         *
         * @param password the config password
         * @return the builder
         */
        public Builder password(String password) {
            config.setPassword(password);
            return this;
        }

        /**
         * Exception handler builder.
         *
         * @param exceptionHandler the config exception handler
         * @return the builder
         */
        public Builder exceptionHandler(ExceptionHandler exceptionHandler) {
            config.setExceptionHandler(exceptionHandler);
            return this;
        }

        /**
         * Builds {@linkplain RabbitMqCommunicationConfig}.
         *
         * @return the {@linkplain RabbitMqCommunicationConfig}
         */
        public RabbitMqCommunicationConfig build() {
            return config;
        }
    }

}
