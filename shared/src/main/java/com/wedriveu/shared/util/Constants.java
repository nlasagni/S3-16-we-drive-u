package com.wedriveu.shared.util;

/**
 * The global system Constants.
 *
 * @author Michele on 22/07/2017.
 * @author Nicola Lasagni on 31/07/2017
 * @author Marco Baldassarri on 1/08/2017
 */
public interface Constants {

    /**
     * Constants related to the Vertx Event bus.
     */
    interface EventBus {
        /**
         * The json key with which retrieve the content of a message.
         */
        String BODY = "body";
    }

    /**
     * Constants related to RabbitMQ.
     */
    interface RabbitMQ {
        /**
         * Constants related to the remote RabbitMQ broker.
         */
        interface Broker {
            /**
             * The HOST of the RabbitMQ broker.
             */
            String HOST = "uniboguys.duckdns.org";
            /**
             * The PASSWORD of the RabbitMQ broker.
             */
            String PASSWORD = "FmzevdBBmpcdvPHLDJQR";
            /**
             * The PORT of the RabbitMQ broker.
             */
            int PORT = 5672;
        }

        /**
         * Constants related to the RabbitMQ configuration keys.
         */
        interface ConfigKey {
            /**
             * The configuration key HOST.
             */
            String HOST = "host";
            /**
             * The configuration key PASSWORD.
             */
            String PASSWORD = "password";
            /**
             * The configuration key PORT.
             */
            String PORT = "port";
        }

        /**
         * Constants related to the RabbitMQ queues.
         */
        interface Queue {
            /**
             * The user queue name, must be completed with the username,
             * see {@linkplain String#format(String, Object...)}.
             */
            String USER = "user.%s";
        }

        /**
         * Constants related to the RabbitMQ exchanges.
         */
        interface Exchanges {
            /**
             * The exchange Type.
             */
            interface Type {
                String DIRECT = "direct";
            }

            /**
             * Empty string for direct queue-to-queue RabbitMQ communication.
             */
            String NO_EXCHANGE = "";
            /**
             * Exchange used for user communications.
             */
            String USER = "user";
            /**
             * Exchange used for vehicle communications.
             */
            String VEHICLE = "vehicle";
        }

        /**
         * Constants related to the RabbitMQ routing keys.
         */
        interface RoutingKey {
            /**
             * The routing key for login request performed by clients communications.
             */
            String LOGIN = "user.request.login";
            /**
             * The routing key for vehicle request performed by clients communications.
             */
            String VEHICLE_REQUEST = "vehicle.request.nearest";
            /**
             * The routing key to communicate to the client the vehicle selected,
             * must be completed with the username, see {@linkplain String#format(String, Object...)}.
             */
            String VEHICLE_RESPONSE = "vehicle.response.nearest.%s";
            /**
             * The routing key for vehicle-service-to-vehicles communications,
             * must be completed with the vehicle license plate, see {@linkplain String#format(String, Object...)}.
             */
            String CAN_DRIVE_REQUEST = "vehicle.request.candrive.%s";
            /**
             * The routing key used by vehicles to communicate if they can do a trip for a specific user,
             * must be completed with the username, see {@linkplain String#format(String, Object...)}.
             */
            String CAN_DRIVE_RESPONSE = "vehicle.response.candrive.%s";
        }
    }

    /**
     * The vehicles database file path.
     */
    String VEHICLES_DATABASE_PATH = "services/vehicle/src/test/resources/vehicles.json";

    /**
     * The constant USERNAME.
     */
    String USERNAME = "username";

    /**
     * Default message encoding.
     */
    String UTF = "UTF-8";

    /**
     * The predefined range of kilometers used to choose a vehicle nearby a specific user.
     */
    double RANGE = 20;

    /**
     * The predefined range of kilometers used to simulate the vehicle recharging movement
     * to a recharge station.
     */
    double MAXIMUM_DISTANCE_TO_RECHARGE = 20;
    /**
     * The predefined number of kilometers used to simulate the vehicle battery draining of 1%.
     */
    double ESTIMATED_KILOMETERS_PER_PERCENTAGE = 10;

    String VEHICLE_TO_SERVICE = "ToService";

// Vehicle Jackson parameters
    String CAR_LICENCE_PLATE = "licencePlate";
    String STATE = "state";
    String POSITION = "position";
    String LAST_UPDATE = "lastUpdate";

// Services RabbitMQ
    String SERVICE_QUEUE_BASE_NAME = "service";

// Vehicle Service RabbitMQ
    String EVENT_BUS_AVAILABLE_ADDRESS = "service.vehicle.eventbus";
    String EVENT_BUS_FINDER_ADDRESS = "finder.vehicle.eventbus";
    String CONSUMER_VEHICLE_SERVICE = "vehicle";
    String USER_POSITION = "userPosition";
    String DESTINATION_POSITION = "destinationPosition";
    String STATUS_AVAILABLE = "available";
    String ELIGIBLE_VEHICLE_LIST = "eligibleVehicleList";
    String TRIP_DISTANCE = "tripDistance";

    /**
     * The constant zero.
     */
    int ZERO = 0;

}
