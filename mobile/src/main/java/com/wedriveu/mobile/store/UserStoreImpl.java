package com.wedriveu.mobile.store;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.mobile.model.User;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Marco on 12/07/2017.
 */
public class LoginStoreImpl implements LoginStore {

    private static final String TAG = LoginStoreImpl.class.getSimpleName();
    private static final String USER_PREFERENCE_NAME = "_userPreferences";
    private static final String USER_PREFERENCE = "user";

    private SharedPreferences mSharedPreferences;
    private ObjectMapper mObjectMapper;

    public LoginStoreImpl(Application application) {
        mSharedPreferences = application.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mObjectMapper = new ObjectMapper();
    }

    @Override
    public User getUser() {
        User user = null;
        try {
            String feedbackJson = mSharedPreferences.getString(USER_PREFERENCE, JSONObject.NULL.toString());
            user = mObjectMapper.readValue(feedbackJson, User.class);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while getting user!", e);
        }
        return user;
    }

    @Override
    public void storeUser(User user) {
        this.user = user;
    }

}
