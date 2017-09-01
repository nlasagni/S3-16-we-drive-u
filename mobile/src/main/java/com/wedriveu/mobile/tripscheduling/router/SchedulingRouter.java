package com.wedriveu.mobile.tripscheduling.router;

import com.wedriveu.mobile.app.DialogProvider;

/**
 *
 * SchedulingRouter is the interface used by the
 * {@linkplain com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModel}
 * logic to switch to the next Fragment transaction.
 *
 * @author Marco Baldassarri
 * @author Nicola Lasagni
 */
public interface SchedulingRouter extends DialogProvider {

    /**
     * Shows the Booking confirmation to the user
     */
    void showBookingSummary();

}
