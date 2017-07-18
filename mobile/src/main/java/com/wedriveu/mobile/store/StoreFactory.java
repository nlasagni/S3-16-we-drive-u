package com.wedriveu.mobile.store;

/**
 *
 * <p>
 *     Store factory interface
 * </p>
 * @author Nicola Lasagni
 * marco
 * @since 18/07/2017
 */
public interface StoreFactory {

    /**
     * Creates the {@linkplain UserStore}
     * @return The {@linkplain UserStore} created.
     */
    UserStore createUserStore();

}
