package com.wedriveu.mobile.booking.viewmodel;

import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.mobile.service.ServiceResult;

/**
 * A {@linkplain ServiceOperationHandler} that manages a {@linkplain BookingViewModelImpl} asynchronous
 * call.
 *
 * @param <T> the type of the {@linkplain ServiceResult}
 * @author Nicola Lasagni on 29/08/2017.
 */
abstract class BookingServiceHandler<T> extends ServiceOperationHandler<BookingViewModelImpl, T> {

    /**
     * Instantiates a new BookingServiceHandler.
     *
     * @param weakReference the weak reference to which the asynchronous results will be delivered.
     */
    BookingServiceHandler(BookingViewModelImpl weakReference) {
        super(weakReference);
    }

}
