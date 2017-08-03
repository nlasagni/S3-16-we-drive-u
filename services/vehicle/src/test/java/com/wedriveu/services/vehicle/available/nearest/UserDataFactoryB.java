package com.wedriveu.services.vehicle.available.nearest;

/**
 * Created by Marco on 02/08/2017.
 */
public class UserDataFactoryB implements UserDataFactory {

    //via sacchi 3, Cesena
    private static final String USER_LATITUDE = "44.139761";
    private static final String USER_LONGITUDE = "12.243219";

    //milano stazione
    private static final String DEST_LATITUDE = "45.485888";
    private static final String DEST_LONGITUDE = "9.204283";
    private static final String USER = "userB";

    @SuppressWarnings("Duplicates")
    @Override
    public UserData getUserData() {
        UserData userData = new UserData();
        userData.setUserLatitude(USER_LATITUDE);
        userData.setUserLongitude(USER_LONGITUDE);
        userData.setDestLatitude(DEST_LATITUDE);
        userData.setDestLongitude(DEST_LONGITUDE);
        userData.setUsername(USER);
        return userData;
    }

}
