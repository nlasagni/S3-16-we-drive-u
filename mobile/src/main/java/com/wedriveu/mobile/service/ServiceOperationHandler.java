package com.wedriveu.mobile.service;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Represents an {@linkplain Handler} that delivers the services asynchronous operations results to
 * specific handlers.
 *
 * @param <WF> the type of the {@linkplain WeakReference} to which the results will be delivered
 * @param <T>  the type of the {@linkplain ServiceResult} to be managed
 * @author Nicola Lasagni on 29/08/2017.
 */
public abstract class ServiceOperationHandler<WF, T> extends Handler {

    private final WeakReference<WF> mWeakReference;

    /**
     * Instantiates a new ServiceOperationHandler.
     *
     * @param weakReference the {@linkplain WeakReference} to which the results will be delivered
     */
    public ServiceOperationHandler(WF weakReference) {
        mWeakReference = new WeakReference<>(weakReference);
    }

    @Override
    public void handleMessage(Message msg) {
        ServiceResult<T> result = (ServiceResult<T>) msg.obj;
        WF weakReference = mWeakReference.get();
        if (weakReference != null) {
            handleMessage(weakReference, result);
        }
    }

    /**
     * Handles the {@linkplain ServiceResult} received.
     *
     * @param weakReference the weak reference that will handle the message
     * @param result        the {@linkplain ServiceResult} to be managed
     */
    protected abstract void handleMessage(WF weakReference, ServiceResult<T> result);

}
