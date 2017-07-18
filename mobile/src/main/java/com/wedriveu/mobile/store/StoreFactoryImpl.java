package com.wedriveu.mobile.store;

import android.app.Application;
import android.content.Context;

/**
 * Created by nicolalasagni on 18/07/2017.
 * marco baldassarri
 */
public class StoreFactoryImpl implements StoreFactory {

    private Context mContext;

    private static StoreFactoryImpl instance = null;

    private StoreFactoryImpl() {
    }

    private StoreFactoryImpl(Context context) {
        mContext = context;
    }

    public static StoreFactoryImpl getInstance(Context context) {
        if(instance == null) {
            instance = new StoreFactoryImpl(context);
        }
        return instance;
    }

    @Override
    public UserStore createUserStore() {
        return new UserStoreImpl(mContext);
    }

}
