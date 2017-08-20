package com.wedriveu.mobile.service;

import android.app.Activity;
import com.wedriveu.mobile.service.booking.BookingService;
import com.wedriveu.mobile.service.booking.BookingServiceImpl;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.login.LoginServiceImpl;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.scheduling.SchedulingServiceImpl;
import com.wedriveu.mobile.service.vehicle.VehicleService;
import com.wedriveu.mobile.service.vehicle.VehicleServiceImpl;
import com.wedriveu.mobile.store.BookingStore;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.store.VehicleStore;

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
    public VehicleService createVehicleService(Activity activity, UserStore userStore, VehicleStore vehicleStore) {
        return new VehicleServiceImpl(activity, userStore, vehicleStore);
    }

}
