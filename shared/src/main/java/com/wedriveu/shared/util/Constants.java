package com.wedriveu.shared.util;

/**
 * @author Michele on 22/07/2017.
 * @author Nicola Lasagni on 31/07/2017
 * @author Marco Baldassarri on 1/08/2017
 */
public interface Constants {

    interface EventBus {
        String BODY = "body";
    }

    interface RabbitMQ {
        interface Broker {
            String HOST = "uniboguys.duckdns.org";
            String PASSWORD = "FmzevdBBmpcdvPHLDJQR";
            int PORT = 5672;
        }
        interface ConfigKey {
            String HOST = "host";
            String PASSWORD = "password";
            String PORT = "port";
        }
        interface Exchanges {
            interface Type {
                String DIRECT = "direct";
            }
            String NO_EXCHANGE = "";
            String USER = "user";
            String VEHICLE = "vehicle";
        }
        interface RoutingKey {
            String LOGIN = "user.request.login";
            String VEHICLE_REQUEST = "vehicle.request.nearest";
            String VEHICLE_RESPONSE = "vehicle.response.nearest.%s";
            String CAN_DRIVE_REQUEST = "vehicle.request.candrive.%s";
            String CAN_DRIVE_RESPONSE = "vehicle.response.candrive.%s";
        }
    }

    String VEHICLES_DATABASE_PATH = "services/vehicle/src/test/resources/vehicles.json";

    String USERNAME = "username";

    String UTF = "UTF-8";

    double RANGE = 20;
    double MAXIMUM_DISTANCE_TO_RECHARGE = 20;
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

    int ZERO = 0;

}
