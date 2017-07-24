package com.wedriveu.mobile.store;

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
     * @return The {@linkplain UserStore} created.
     */
    UserStore createUserStore();

}
