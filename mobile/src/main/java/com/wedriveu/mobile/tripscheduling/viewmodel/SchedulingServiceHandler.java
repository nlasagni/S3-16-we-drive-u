package com.wedriveu.mobile.tripscheduling.viewmodel;

import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.mobile.service.ServiceResult;

/**
 * * A {@linkplain ServiceOperationHandler} that manages a {@linkplain SchedulingViewModelImpl} asynchronous
 * call.
 *
 * @param <T> the type of the {@linkplain ServiceResult}
 * @author Nicola Lasagni on 29/08/2017.
 */
abstract class SchedulingServiceHandler<T> extends ServiceOperationHandler<SchedulingViewModelImpl, T> {

    /**
     * Instantiates a new SchedulingServiceHandler.
     *
     * @param weakReference the weak reference to which the asynchronous results will be delivered.
     */
    SchedulingServiceHandler(SchedulingViewModelImpl weakReference) {
        super(weakReference);
    }
}
