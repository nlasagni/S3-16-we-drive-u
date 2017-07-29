package com.wedriveu.mobile.store;

import android.content.Context;

/**
 *
 * Store factory interface
 *
 * @author Nicola Lasagni
 * @author Marco Baldassarri
 * @since 18/07/2017
 */
public interface StoreFactory {

    /**
     * Creates the {@linkplain UserStore}
     *
     * @param context The context with which create the store.
     * @return The {@linkplain UserStore} created.
     */
    UserStore createUserStore(Context context);

    /**
     * Creates the {@linkplain VehicleStore}
     *
     * @param context The context with which create the store.
     * @return The {@linkplain VehicleStore} created.
     */
    VehicleStore createVehicleStore(Context context);

}
