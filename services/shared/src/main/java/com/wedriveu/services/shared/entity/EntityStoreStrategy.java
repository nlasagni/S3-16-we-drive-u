package com.wedriveu.services.shared.entity;

/**
 * @author Nicola Lasagni on 31/07/2017.
 */
public interface EntityStoreStrategy<T> {

    /**
     * Gets an instance of {@linkplain T} from the store.
     * @return an instance of {@linkplain T}.
     */
    T get();

    /**
     * Store an instance of {@linkplain T} into the store.
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean store(T entity);

}
