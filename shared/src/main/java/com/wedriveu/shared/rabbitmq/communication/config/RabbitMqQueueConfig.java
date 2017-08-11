package com.wedriveu.shared.rabbitmq.communication.config;

/**
 * The configuration needed to add a new queue to RabbitMQ.
 *
 * @author Nicola Lasagni on 10/08/2017.
 */
public class RabbitMqQueueConfig {

    private String queueName;
    private boolean durable;
    private boolean exclusive;
    private boolean autoDelete;

    private RabbitMqQueueConfig() {}

    /**
     * Gets queue name.
     *
     * @return the {@code queueName} value
     */
    public String getQueueName() {
        return queueName;
    }

    private void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * Indicates if the queue is durable or not.
     *
     * @return the {@code durable} value
     */
    public boolean isDurable() {
        return durable;
    }

    private void setDurable(boolean durable) {
        this.durable = durable;
    }

    /**
     * Indicates if the queue is exclusive or not.
     *
     * @return the {@code exclusive} value
     */
    public boolean isExclusive() {
        return exclusive;
    }

    private void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    /**
     * Indicates if the queue can be deleted automatically or not.
     *
     * @return the {@code autoDelete} value
     */
    public boolean isAutoDelete() {
        return autoDelete;
    }

    private void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RabbitMqQueueConfig)) {
            return false;
        }
        RabbitMqQueueConfig otherConfig = (RabbitMqQueueConfig) other;
        return durable == otherConfig.durable &&
                exclusive == otherConfig.exclusive &&
                autoDelete == otherConfig.autoDelete &&
                (queueName != null ? queueName.equals(otherConfig.queueName) : otherConfig.queueName == null);
    }

    @Override
    public int hashCode() {
        int result = queueName != null ? queueName.hashCode() : 0;
        result = 31 * result + (durable ? 1 : 0);
        result = 31 * result + (exclusive ? 1 : 0);
        result = 31 * result + (autoDelete ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RabbitMqQueueConfig{" +
                "queueName='" + queueName + '\'' +
                ", durable=" + durable +
                ", exclusive=" + exclusive +
                ", autoDelete=" + autoDelete +
                '}';
    }

    /**
     * The type Builder.
     */
    public static final class Builder {

        private RabbitMqQueueConfig config;

        /**
         * Instantiates a new Builder.
         */
        public Builder() {
            config = new RabbitMqQueueConfig();
            config.setExclusive(true);
            config.setAutoDelete(true);
        }

        /**
         * Queue name builder.
         *
         * @param queueName the queue name
         * @return the builder
         */
        public Builder queueName(String queueName) {
            config.setQueueName(queueName);
            return this;
        }

        /**
         * Durable builder.
         *
         * @param durable the durable parameter
         * @return the builder
         */
        public Builder durable(boolean durable) {
            config.setDurable(durable);
            return this;
        }

        /**
         * Exclusive builder.
         *
         * @param exclusive the exclusive parameter
         * @return the builder
         */
        public Builder exclusive(boolean exclusive) {
            config.setExclusive(exclusive);
            return this;
        }

        /**
         * Auto delete builder.
         *
         * @param autoDelete the auto delete parameter
         * @return the builder
         */
        public Builder autoDelete(boolean autoDelete) {
            config.setAutoDelete(autoDelete);
            return this;
        }

        /**
         * Builds a {@linkplain RabbitMqQueueConfig}.
         *
         * @return the {@linkplain RabbitMqQueueConfig} with the parameters provided
         */
        public RabbitMqQueueConfig build() {
            return config;
        }

    }

}
