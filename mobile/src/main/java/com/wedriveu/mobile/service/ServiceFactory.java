package com.wedriveu.mobile.service;

import android.app.Activity;
import com.wedriveu.mobile.service.booking.BookingService;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.vehicle.VehicleService;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.store.VehicleStore;

/**
 *
 * Service factory interface
 * 
 * @author Nicola Lasagni
 * @author Marco Baldassarri
 * @since 18/07/2017
 */
public interface ServiceFactory {

    /**
     * 
     * Creates a Login Service.
     * 
     * @return An instance of {@linkplain LoginService}
     */
    LoginService createLoginService(Activity activity);

    /**
     * 
     * Creates a Scheduling Service.
     * 
     * @return An instance of {@linkplain SchedulingService}
     */
    SchedulingService createSchedulingService(Activity activity, UserStore userStore);

    /**
     * Creates a Booking Service.
     *
     * @param activity The activity needed by the service.
     * @param userStore The store needed to retrieve a {@linkplain com.wedriveu.mobile.model.User}
     * @return An instance of {@linkplain BookingService}
     */
    BookingService createBookingService(Activity activity, UserStore userStore);

    /**
     * Creates a Vehicle Service.
     *
     * @param activity The activity needed by the service.
     * @param userStore The store needed to retrieve a {@linkplain com.wedriveu.mobile.model.User}
     * @param vehicleStore The store needed to retrieve a {@linkplain com.wedriveu.mobile.model.Vehicle}
     * @return An instance of {@linkplain VehicleService}
     */
    VehicleService createVehicleService(Activity activity, UserStore userStore, VehicleStore vehicleStore);

}
