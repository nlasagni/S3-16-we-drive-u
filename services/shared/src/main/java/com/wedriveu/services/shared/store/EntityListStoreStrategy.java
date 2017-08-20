package com.wedriveu.services.shared.store;

import java.util.List;

/**
 * @author Nicola Lasagni on 31/07/2017.
 */
public interface EntityListStoreStrategy<T> {

    /**
     * Gets a list of entities {@linkplain T} from the store.
     *
     * @return a list of entities {@linkplain T}.
     * @throws Exception If something goes wrong during the get operation.
     */
    List<T> getEntities() throws Exception;

    /**
     * Store a list of entities {@linkplain T} into the store.
     *
     * @throws Exception If something goes wrong during the store operation.
     */
    void storeEntities(List<T> entity) throws Exception;

    /**
     * Clears the store.
     *
     * @throws Exception If something goes wrong during the clear operation.
     */
    void clear() throws Exception;

}
