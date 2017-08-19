package com.wedriveu.mobile.service;

import android.app.Activity;
import com.wedriveu.mobile.service.booking.BookingService;
import com.wedriveu.mobile.service.booking.BookingServiceImpl;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.login.LoginServiceImpl;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.scheduling.SchedulingServiceImpl;
import com.wedriveu.mobile.service.vehicle.EnterVehicleService;
import com.wedriveu.mobile.service.vehicle.EnterVehicleServiceImpl;
import com.wedriveu.mobile.store.BookingStore;
import com.wedriveu.mobile.store.UserStore;

/**
 * @author Nicola Lasagni on 18/07/2017.
 */
public class ServiceFactoryImpl implements ServiceFactory {

    private static ServiceFactoryImpl instance = null;

    private ServiceFactoryImpl() { }

    public static ServiceFactory getInstance() {
        if(instance == null){
            instance = new ServiceFactoryImpl();
        }
        return instance;
    }
    @Override
    public LoginService createLoginService(Activity activity) {
        return new LoginServiceImpl(activity);
    }

    @Override
    public SchedulingService createSchedulingService(Activity activity, UserStore userStore) {
        return new SchedulingServiceImpl(activity, userStore);
    }

    @Override
    public BookingService createBookingService(Activity activity, UserStore userStore) {
        return new BookingServiceImpl(activity, userStore);
    }

    @Override
    public EnterVehicleService createEnterVehicleService(Activity activity, UserStore userStore, BookingStore bookingStore) {
        return new EnterVehicleServiceImpl(activity, userStore, bookingStore);
    }

}
