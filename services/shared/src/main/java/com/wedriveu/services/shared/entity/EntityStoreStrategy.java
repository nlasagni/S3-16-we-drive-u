package com.wedriveu.services.shared.entity;

/**
 * Created by nicolalasagni on 31/07/2017.
 */
public interface EntityStoreStrategy<T> {

    T get();

    void store(T entity);

}
