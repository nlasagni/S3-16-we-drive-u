package com.wedriveu.mobile.service;

import android.app.Activity;
import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.store.UserStore;

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

}
