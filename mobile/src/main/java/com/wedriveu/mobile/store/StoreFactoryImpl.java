package com.wedriveu.mobile.store;

import android.app.Application;

/**
 * Created by nicolalasagni on 18/07/2017.
 */
public class StoreFactoryImpl implements StoreFactory {

    private Application mApplication;

    public StoreFactoryImpl(Application application) {
        mApplication = application;
    }

    @Override
    public UserStore createUserStore() {
        return new UserStoreImpl(mApplication);
    }

}
