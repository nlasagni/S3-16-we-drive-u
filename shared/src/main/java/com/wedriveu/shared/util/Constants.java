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

        /**
         * The eventbus message key for passing a verticle deploymentId.
         */
        String DEPLOYMENT_ID = "deploymentId";
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
            /**
             * Exchange used for booking communications.
             */
            String BOOKING = "booking";
            /**
             * Exchange used for analytics communications.
             */
            String ANALYTICS = "analytics";
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

            /**
             * The routing key used by a vehicle to register itself to the vehicleService system.
             */
            String REGISTER_REQUEST = "vehicle.request.add";

            /**
             * The routing key used by the vehicleService to send back the response
             * to the vehicle after register request
             */
            String REGISTER_RESPONSE = "vehicle.response.add.%s";
            /**
             * The routing key used by vehicle-service to communicate a request of booking to a vehicle,
             * must be completed with the license plate, see {@linkplain String#format(String, Object...)}.
             */
            String BOOK_REQUEST = "vehicle.request.book.%s";
            /**
             * The routing key used by vehicles to communicate the response of a booking request from service,
             * must be completed with the license plate, see {@linkplain String#format(String, Object...)}.
             */
            String BOOK_RESPONSE = "vehicle.response.book.%s";
            /**
             * The routing key used by vehicles to communicate they arrived to destination.
             */
            String VEHICLE_ARRIVED = "vehicle.event.arrived";
            /**
             * The routing key used by vehicles to communicate their informations updates.
             */
            String VEHICLE_UPDATE = "vehicle.event.updated";
            /**
             * The routing key used by vehicle-service to communicate a drive command to a vehicle.
             */
            String VEHICLE_DRIVE_COMMAND = "vehicle.event.drive";
            /**
             * The routing key used to communicate to the booking-service a create booking request.
             */
            String CREATE_BOOKING_REQUEST = "booking.request.create";
            /**
             * The routing key used by booking-service to communicate if it has created the booking.
             */
            String CREATE_BOOKING_RESPONSE = "booking.response.create.%s";
            /**
             * The routing key used to communicate to the booking-service a change booking request.
             */
            String CHANGE_BOOKING_REQUEST = "booking.request.change";
            /**
             * The routing key used by booking-service to communicate if it has changed the booking.
             */
            String CHANGE_BOOKING_RESPONSE = "booking.response.change";
            /**
             * The routing key used to communicate to the booking-service a complete booking request.
             */
            String COMPLETE_BOOKING_REQUEST = "booking.request.complete";
            /**
             * The routing key used by booking-service to communicate if it has completed the booking.
             */
            String COMPLETE_BOOKING_RESPONSE = "booking.response.complete";
            /**
             * The routing key used to communicate to the booking-service a find bookings by date request.
             */
            String FIND_BOOKING_BY_DATE_REQUEST = "booking.request.bydate";
            /**
             * The routing key used by booking-service to communicate the bookings found.
             */
            String FIND_BOOKING_BY_DATE_RESPONSE = "booking.response.bydate";
            /**
             * The routing key used to communicate to the booking-service a find bookings by position request.
             */
            String FIND_BOOKING_POSITION_REQUEST = "booking.request.position";
            /**
             * The routing key used by booking-service to communicate the bookings found.
             */
            String FIND_BOOKING_POSITION_RESPONSE = "booking.response.position";
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
    }

    interface Position {
        /**
         * The predefined range of kilometers used to choose a vehicle nearby a specific user.
         */
        double RANGE = 20;

        double EARTH_RADIUS = 6372.795477598;
    }


    interface Vehicle {
        String LICENSE_PLATE = "licensePlate";

    }

    /**
     * The constant zero.
     */
    int ZERO = 0;

    /**
     * The constant USERNAME.
     */
    String USERNAME = "username";

    /**
     * Key used for event bus messages.
     */
    String VEHICLE = "vehicle";


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


    //analytics service
    String VEHICLE_REQUEST_ALL_MESSAGE = "Requesting All Vehicles From Analytics Service";
    String ANALYTICS_VEHICLE_LIST_RETRIEVER_START_MESSAGE = "Vehicle List Request Sent, Listen";
    String ANALYTICS_VEHICLE_LIST_RETRIEVER_VERTICLE_ADDRESS = "VehicleListRetrieverVerticle";
    String ANALYTICS_VEHICLE_LIST_REQUEST_VERTICLE_ADDRESS = "VehicleListRequestVerticle";
    String ROUTING_KEY_VEHICLE_REQUEST_ALL = "vehicle.request.all";
    String ROUTING_KEY_VEHICLE_RESPONSE_ALL = "vehicle.response.all";
    String ANALYTICS_CONTROLLER_VEHICLE_LIST_VERTICLE_ADDRESS = "AnalyticsVerticleController";
    String ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE = "Started all services, start requesting vehicle list";
    String EVENT_BUS_AVAILABLE_ADDRESS = "service.vehicle.eventbus";
    String ANALYTICS_TEST_VEHICLE_LIST_REQUEST_EVENTBUS = "test.analytics.vehicleListRequest";


}
