package com.wedriveu.mobile.store;

import android.content.Context;

/**
 * Created by nicolalasagni on 18/07/2017.
 */
public class StoreFactoryImpl implements StoreFactory {

    private static StoreFactoryImpl instance = null;

    private StoreFactoryImpl() {}

    public static StoreFactoryImpl getInstance() {
        if(instance == null) {
            instance = new StoreFactoryImpl();
        }
        return instance;
    }

    @Override
    public UserStore createUserStore(Context context) {
        return new UserStoreImpl(context);
    }

    @Override
    public VehicleStore createVehicleStore(Context context) {
        return new VehicleStoreImpl(context);
    }

}
