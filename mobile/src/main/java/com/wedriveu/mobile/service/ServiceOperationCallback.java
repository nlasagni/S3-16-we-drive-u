package com.wedriveu.mobile.service;

/**
 * Callback for services asynchronous operations
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
public interface ServiceOperationCallback<T> {

    /**
     * Called from a service when it has ended its operation.
     *
     * @param result The result of the service operation.
     * @param errorMessage Error message that indicates what went wrong during the service operation.
     */
    void onServiceOperationFinished(T result, String errorMessage);

}
