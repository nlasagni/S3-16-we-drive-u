package com.wedriveu.mobile.service;

import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.scheduling.SchedulingService;

/**
 *
 * <p>
 *     Service factory interface
 * </p>
 * @author Nicola Lasagni
 * marco
 * @since 18/07/2017
 */
public interface ServiceFactory {

    LoginService createLoginService();
    SchedulingService createSchedulingService();

}
