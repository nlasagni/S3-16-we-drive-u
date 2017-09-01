package com.wedriveu.mobile.app;

import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.store.StoreFactory;

/**
 * Represents an entity capable of providing the needed factories.
 *
 * @author Nicola Lasagni on 30/08/2017.
 */
public interface FactoryProvider {

    /**
     * Provides a {@linkplain StoreFactory}.
     *
     * @return the store factory
     */
    StoreFactory provideStoreFactory();

    /**
     * Provides a {@linkplain ServiceFactory}.
     *
     * @return the service factory
     */
    ServiceFactory provideServiceFactory();

}
