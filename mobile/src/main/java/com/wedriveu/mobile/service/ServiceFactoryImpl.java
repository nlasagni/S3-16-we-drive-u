package com.wedriveu.mobile.service;

import android.app.Activity;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.login.LoginServiceImpl;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.scheduling.SchedulingServiceImpl;

/**
 * Created by nicolalasagni on 18/07/2017.
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
    public SchedulingService createSchedulingService() {
        return new SchedulingServiceImpl();
    }

}
