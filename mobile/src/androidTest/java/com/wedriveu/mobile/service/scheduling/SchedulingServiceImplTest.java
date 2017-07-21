package com.wedriveu.mobile.service.scheduling;

import com.wedriveu.mobile.model.UserLocation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SchedulingServiceImplTest {
    private UserLocation userLocation;

    @Before
    public void init() {
        userLocation = new UserLocation();
    }

    @Test
    public void findNearestVehicle() throws Exception {
        assertTrue(userLocation != null);
        assertTrue(userLocation.getUserLatitude() != null);
        assertTrue(userLocation.getUserLongitude() != null);
        assertTrue(userLocation.getDestinationLatitude() != null);
        assertTrue(userLocation.getDestinationLongitude() != null);
    }

}