package com.wedriveu.mobile.service;

import com.wedriveu.mobile.service.login.LoginService;

/**
 *
 * <p>
 *     Service factory interface
 * </p>
 * @author Nicola Lasagni
 * @since 18/07/2017
 */
public interface ServiceFactory {

    LoginService createLoginService();

}
