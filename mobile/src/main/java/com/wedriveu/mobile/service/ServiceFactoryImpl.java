package com.wedriveu.mobile.service;

import com.wedriveu.mobile.service.login.LoginService;
import com.wedriveu.mobile.service.login.LoginServiceImpl;

/**
 * Created by nicolalasagni on 18/07/2017.
 */
public class ServiceFactoryImpl implements ServiceFactory {

    @Override
    public LoginService createLoginService() {
        return new LoginServiceImpl();
    }

}
