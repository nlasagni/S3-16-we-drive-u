package com.wedriveu.mobile.service;

import com.wedriveu.mobile.service.booking.BookingService;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.vehicle.VehicleService;

/**
 * The factory of all mobile app services.
 * 
 * @author Nicola Lasagni
 * @author Marco Baldassarri
 */
public interface ServiceFactory {

    /**
     * 
     * Creates a Login Service.
     * 
     * @return An instance of {@linkplain LoginService}
     */
    LoginService createLoginService();

    /**
     * 
     * Creates a Scheduling Service.
     * 
     * @return An instance of {@linkplain SchedulingService}
     */
    SchedulingService createSchedulingService();

    /**
     * Creates a Booking Service.
     *
     * @return An instance of {@linkplain BookingService}
     */
    BookingService createBookingService();

    /**
     * Creates a Vehicle Service.
     *
     * @return An instance of {@linkplain VehicleService}
     */
    VehicleService createVehicleService();

}
