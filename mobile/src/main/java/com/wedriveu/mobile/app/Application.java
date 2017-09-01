package com.wedriveu.mobile.app;

import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.service.booking.BookingService;
import com.wedriveu.mobile.service.booking.BookingServiceImpl;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.login.LoginServiceImpl;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.scheduling.SchedulingServiceImpl;
import com.wedriveu.mobile.service.vehicle.VehicleService;
import com.wedriveu.mobile.service.vehicle.VehicleServiceImpl;
import com.wedriveu.mobile.store.*;

/**
 * The singleton application for this mobile app.
 * It provides {@link StoreFactory} and {@linkplain ServiceFactory} methods to
 * instantiate the singleton stores and services needed.
 *
 * @author Nicola Lasagni on 30/08/2017.
 */
public class Application extends android.app.Application implements StoreFactory, ServiceFactory {

    private UserStore mUserStore;
    private BookingStore mBookingStore;
    private VehicleStore mVehicleStore;
    private LoginService mLoginService;
    private SchedulingService mSchedulingService;
    private BookingService mBookingService;
    private VehicleService mVehicleService;


    @Override
    public void onCreate() {
        super.onCreate();
        mUserStore = new UserStoreImpl(this);
        mBookingStore = new BookingStoreImpl(this);
        mVehicleStore = new VehicleStoreImpl(this);
        mLoginService = new LoginServiceImpl();
        mBookingService = new BookingServiceImpl();
        mSchedulingService = new SchedulingServiceImpl();
        mVehicleService = new VehicleServiceImpl(mUserStore, mVehicleStore);
    }

    @Override
    public UserStore createUserStore() {
        return mUserStore;
    }

    @Override
    public VehicleStore createVehicleStore() {
        return mVehicleStore;
    }

    @Override
    public BookingStore createBookingStore() {
        return mBookingStore;
    }

    @Override
    public LoginService createLoginService() {
        return mLoginService;
    }

    @Override
    public SchedulingService createSchedulingService() {
        return mSchedulingService;
    }

    @Override
    public BookingService createBookingService() {
        return mBookingService;
    }

    @Override
    public VehicleService createVehicleService() {
        return mVehicleService;
    }

}
