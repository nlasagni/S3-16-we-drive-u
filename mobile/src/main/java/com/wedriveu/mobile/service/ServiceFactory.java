package com.wedriveu.mobile.service;

import com.wedriveu.mobile.service.login.LoginService;

/**
 * Created by nicolalasagni on 17/07/2017.
 */
public interface ServiceFactory {

    LoginService createLoginService();

}
