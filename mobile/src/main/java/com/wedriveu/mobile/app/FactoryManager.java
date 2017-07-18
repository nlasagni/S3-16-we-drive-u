package com.wedriveu.mobile.app;

import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.store.StoreFactory;

/**
 *
 * <p>
 *     Factory manager interface
 * </p>
 * @author Nicola Lasagni
 * @since 18/07/2017
 */
public interface FactoryManager {

    /**
     * Creates the stores factory.
     * @return The {@linkplain StoreFactory} created.
     */
    StoreFactory createStoreFactory();

    /**
     * Creates the services factory.
     * @return The {@linkplain ServiceFactory} created.
     */
    ServiceFactory createServiceFactory();

}
