package com.wedriveu.mobile.service;

import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.scheduling.SchedulingService;

/**
 *
 * <p>
 *     Service factory interface
 * </p>
 * @author Nicola Lasagni
 * @since 18/07/2017
 */
public interface ServiceFactory {

    /**
     * <p>
     *     Creates a Login Service
     * </p>
     * @return the login service
     */
    LoginService createLoginService();

    /**
     * <p>
     *     Creates a Scheduling Service
     * </p>
     * @return
     */
    SchedulingService createSchedulingService();

}
