package com.wedriveu.mobile.store;

/**
 *
 * Store factory interface
 *
 * @author Nicola Lasagni
 * @author Marco Baldassarri
 */
public interface StoreFactory {

    /**
     * Creates the {@linkplain UserStore}
     *
     * @return The {@linkplain UserStore} created.
     */
    UserStore createUserStore();

    /**
     * Creates the {@linkplain VehicleStore}
     *
     * @return The {@linkplain VehicleStore} created.
     */
    VehicleStore createVehicleStore();

    /**
     * Creates the {@linkplain BookingStore}
     * @return The {@linkplain BookingStore} created.
     */
    BookingStore createBookingStore();

}
