package com.wedriveu.mobile.tripscheduling.router;

import com.wedriveu.mobile.app.DialogProvider;
import com.wedriveu.mobile.model.Vehicle;

/**
 * <p>
 *     SchedulingRouter is the interface used by the {@linkplain com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModel} logic to switch to the next Fragment transaction.
 * </p>
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingRouter extends DialogProvider {

    /**
     * Shows the Booking fragment with the data returned by the {@linkplain com.wedriveu.mobile.service.scheduling.SchedulingService}
     * * @param vehicle The vehicle object
     */
    void showBooking(Vehicle vehicle);

}
