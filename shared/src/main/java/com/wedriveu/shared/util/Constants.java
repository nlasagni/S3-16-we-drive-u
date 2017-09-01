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
     * Headquarter of all Vehicles located near the Cesena train station.
     */
    com.wedriveu.shared.util.Position HEAD_QUARTER =
            new com.wedriveu.shared.util.Position(44.1454528, 12.2474513);
    /**
     * string for formatting two strings with a dot between them
     */
    String FORMAT_WITH_DOT = "%s.%s";

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

        /**
         * The eventbus address used by the vehicle to notify the update verticle to send an update message.
         */
        String EVENT_BUS_ADDRESS_UPDATE = "notify.update.publisher.%s";

        /**
         * The eventbus address used by the vehicle to notify the arrived message verticle to send an "arrived notify".
         */
        String EVENT_BUS_ADDRESS_NOTIFY = "notify.arrived.publisher.%s";

        /**
         * The eventbus address used by vehicle to notify the vehicle's user communication verticle publisher.
         */
        String EVENT_BUS_ADDRESS_FOR_USER = "notify.user.publisher.%s";
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
             * The routing key used by the Analytics Service to retrieve
             * the entire list of vehicles from the VehicleService.
             */
            String ANALYTICS_VEHICLE_REQUEST_ALL = "vehicle.request.all";
            /**
             * The routing key used by the VehicleService to send the
             * list of Vehicles to the Analytics Service, given the requester backOffice ID.
             */
            String ANALYTICS_VEHICLES_RESPONSE_ALL = "vehicle.response.all";

            /**
             * The routing key used by booking-service to communicate a request of booking.
             */
            String VEHICLE_SERVICE_BOOK_REQUEST = "vehicle.request.book";
            /**
             * The routing key used vehicle-service to communicate the response of a booking request to booking-service.
             */
            String VEHICLE_SERVICE_BOOK_RESPONSE = "vehicle.response.book";

            /**
             * The routing key used by vehicle-service to communicate a request of booking to a vehicle,
             * must be completed with the license plate, see {@linkplain String#format(String, Object...)}.
             */
            String BOOK_VEHICLE_REQUEST = "vehicle.request.book.%s";
            /**
             * The routing key used by vehicles to communicate the response of a booking request from service,
             * must be completed with the license plate, see {@linkplain String#format(String, Object...)}.
             */
            String BOOK_VEHICLE_RESPONSE = "vehicle.response.book.%s";
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
            String VEHICLE_DRIVE_COMMAND = "vehicle.event.drive.%s";
            /**
             * The routing key used by vehicle to request the user to get inside.
             */
            String VEHICLE_REQUEST_ENTER_USER = "vehicle.request.enter.user.%s";
            /**
             * The routing key used by the user to respond to the enter request of the vehicle.
             */
            String VEHICLE_RESPONSE_ENTER_USER = "vehicle.response.enter.user.%s";
            /**
             * The routing key used by the vehicle service to communicate the substitution of
             * a broken vehicle.
             */
            String VEHICLE_SUBSTITUTION = "vehicle.event.substitution.%s";
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
             * The routing key used by booking-service to communicate if it has completed the booking to the client.
             */
            String COMPLETE_BOOKING_RESPONSE_USER = "booking.response.complete.%s";
            /**
             * The routing key used to communicate to the booking-service an abort booking request.
             */
            String ABORT_BOOKING_REQUEST = "booking.request.abort";
            /**
             * The routing key used to communicate to the booking-service a find bookings by position request.
             */
            String FIND_BOOKING_POSITION_REQUEST = "booking.request.position";
            /**
             * The routing key used by booking-service to communicate the bookings found.
             */
            String FIND_BOOKING_POSITION_RESPONSE = "booking.response.position";
            /**
             *  The routing key used by backoffice to request a vehicle counter.
             */
            String ANALYTICS_REQUEST_VEHICLE_COUNTER = "analytics.request.vehicle";
            /**
             *  The routing key used by backoffice to receive a vehicle counter.
             */
            String ANALYTICS_RESPONSE_VEHICLE_COUNTER = "analytics.response.vehicles";
            /**
             *  The routing key used by booking service to receive a booking list request.
             */
            String BOOKING_REQUEST_BOOKING_LIST = "booking.request.all";
            /**
             *  The routing key used by booking service to receive a booking list request.
             */
            String BOOKING_RESPONSE_BOOKING_LIST = "booking.response.all.%s";
        }
    }

    interface Position {
        /**
         * The predefined minimum range of kilometers used to choose a vehicle nearby a specific user.
         */
        double DEFAULT_MIN_RANGE = 0.1;
        /**
         * The predefined maximum range of kilometers used to choose a vehicle nearby a specific user.
         */
        double DEFAULT_MAX_RANGE = 50.0;
        /**
         * The predefined maximum range of kilometers used to choose a substitution vehicle nearby a specific user.
         */
        double DEFAULT_SUBSTITUTION_RANGE = 80.0;

        double EARTH_RADIUS = 6372.795477598;
    }


    interface Vehicle {
        /**
         * This status means that the {@linkplain Vehicle} can be booked by a user.
         */
        String STATUS_AVAILABLE = "available";
        /**
         * This status means that the {@linkplain Vehicle} has been booked by a user.
         */
        String STATUS_BOOKED = "booked";
        /**
         * This status means that the {@linkplain Vehicle} is going to recharge itself.
         */
        String STATUS_RECHARGING = "recharging";
        /**
         * This status means that the {@linkplain Vehicle} is broken or stolen.
         */
        String STATUS_BROKEN_STOLEN = "broken_stolen";
        /**
         * This status means that the {@linkplain Vehicle} has network issues, so it will come back to the headquarted.
         */
        String STATUS_NETWORK_ISSUES = "net_issues";
    }

    interface MessagesAnalytics {

        /**
         * The message sent from the Analytics service to the Vehicle service to request all vehicles.
         */
        String VEHICLE_REQUEST_ALL_MESSAGE = "Requesting All Vehicles From Analytics Service";
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

}
