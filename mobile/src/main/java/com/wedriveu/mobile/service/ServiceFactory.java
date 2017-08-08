package com.wedriveu.mobile.service;

import android.app.Activity;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.scheduling.SchedulingService;

/**
 *
 * 
 *     Service factory interface
 * 
 * @author Nicola Lasagni
 * @author Marco Baldassarri
 * @since 18/07/2017
 */
public interface ServiceFactory {

    /**
     * 
     *     Creates a Login Service
     * 
     * @return the login service
     */
    LoginService createLoginService(Activity activity);

    /**
     * 
     *     Creates a Scheduling Service
     * 
     * @return
     */
    SchedulingService createSchedulingService();

}
