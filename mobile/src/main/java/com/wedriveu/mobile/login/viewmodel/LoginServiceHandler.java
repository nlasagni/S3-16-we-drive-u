package com.wedriveu.mobile.login.viewmodel;

import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.mobile.service.ServiceResult;

/**
 * A {@linkplain ServiceOperationHandler} that manages a {@linkplain LoginViewModelImpl} asynchronous
 * call.
 *
 * @param <T> the type of the {@linkplain ServiceResult}
 * @author Nicola Lasagni on 29/08/2017.
 */
abstract class LoginServiceHandler<T> extends ServiceOperationHandler<LoginViewModelImpl, T> {

    /**
     * Instantiates a new LoginServiceHandler.
     *
     * @param weakReference the weak reference to which the asynchronous results will be delivered.
     */
    LoginServiceHandler(LoginViewModelImpl weakReference) {
        super(weakReference);
    }

}
