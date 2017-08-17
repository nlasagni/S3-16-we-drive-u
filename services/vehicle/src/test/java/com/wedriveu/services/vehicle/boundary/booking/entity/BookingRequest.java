package com.wedriveu.services.vehicle.boundary.booking.entity;


import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;
import com.wedriveu.shared.util.Position;

/**
 * @author Marco on 02/08/2017.
 */
public class BookingRequest implements BookingRequestFactory {

    private static final String USERNAME = "User1";
    private static final String LICENCE_PLATE = "AAA";
    private static final double LATITUDE_USER = 41.903319;
    private static final double LONGITUDE_USER = 12.496441;
    private static final double LATITUDE_DESTINATION = 44.139761;
    private static final double LONGITUDE_DESTINATION = 12.243219;
    private static final Position POSITION_USER = new Position(LATITUDE_USER, LONGITUDE_USER);
    private static final Position POSITION_DESTINATION = new Position(LATITUDE_DESTINATION, LONGITUDE_DESTINATION);


    @Override
    public BookVehicleRequest getBookingVehicleRequest() {
        BookVehicleRequest request = new BookVehicleRequest();
        request.setUsername(USERNAME);
        request.setLicencePlate(LICENCE_PLATE);
        request.setUserPosition(POSITION_USER);
        request.setDestinationPosition(POSITION_DESTINATION);
        return request;
    }
}
