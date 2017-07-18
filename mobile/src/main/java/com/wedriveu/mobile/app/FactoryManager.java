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

    StoreFactory createStoreFactory();

    ServiceFactory createServiceFactory();

}
