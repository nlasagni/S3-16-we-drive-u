package com.wedriveu.services.vehicle.boundary.nearest.entity;

import com.wedriveu.services.vehicle.entity.UserRequest;
import com.wedriveu.shared.util.Position;

/**
 * Created by Marco on 02/08/2017.
 */
public class UserDataFactoryB implements UserDataFactory {

    //via sacchi 3, Cesena
    private static final double USER_LATITUDE = 44.139761;
    private static final double USER_LONGITUDE = 12.243219;
    //milano stazione
    private static final double DEST_LATITUDE = 45.485888;
    private static final double DEST_LONGITUDE = 9.204283;
    private static final String USER = "userB";


    @SuppressWarnings("Duplicates")
    @Override
    public UserRequest getUserData() {
        UserRequest userRequest = new UserRequest();
        userRequest.setDestinationPosition(new Position(DEST_LATITUDE, DEST_LONGITUDE));
        userRequest.setUserPosition(new Position(USER_LATITUDE, USER_LONGITUDE));
        userRequest.setUsername(USER);
        return userRequest;
    }

}
