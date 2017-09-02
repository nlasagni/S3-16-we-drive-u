package com.wedriveu.mobile.service;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Represents an {@linkplain Handler} that delivers the services asynchronous operations results to
 * specific handlers.
 *
 * @param <WR> the type of the {@linkplain WeakReference} to which the results will be delivered
 * @param <T>  the type of the {@linkplain ServiceResult} to be managed
 * @author Nicola Lasagni on 29/08/2017.
 */
public abstract class ServiceOperationHandler<WR, T> extends Handler {

    private WeakReference<WR> mWeakReference;

    /**
     * Instantiates a new ServiceOperationHandler.
     *
     * @param weakReference the {@linkplain WeakReference} to which the results will be delivered
     */
    public ServiceOperationHandler(WR weakReference) {
        mWeakReference = new WeakReference<>(weakReference);
    }

    @Override
    public void handleMessage(Message msg) {
        ServiceResult<T> result = (ServiceResult<T>) msg.obj;
        WR weakReference = mWeakReference.get();
        if (weakReference != null) {
            handleMessage(weakReference, result);
        }
    }

    /**
     * Refreshes the handler reference.
     *
     * @param weakReference the reference to be refreshed
     */
    public void refreshReference(WR weakReference) {
        mWeakReference.clear();
        mWeakReference = new WeakReference<>(weakReference);
    }

    /**
     * Handles the {@linkplain ServiceResult} received.
     *
     * @param weakReference the weak reference that will handle the message
     * @param result        the {@linkplain ServiceResult} to be managed
     */
    protected abstract void handleMessage(WR weakReference, ServiceResult<T> result);

}
