package com.wedriveu.mobile.service.booking;

import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.shared.rabbitmq.message.CreateBookingResponse;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public interface BookingService {

    void acceptBooking(Booking booking, ServiceOperationCallback<CreateBookingResponse> callback);

}
