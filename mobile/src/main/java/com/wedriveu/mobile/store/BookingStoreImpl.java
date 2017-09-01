package com.wedriveu.mobile.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.mobile.model.Booking;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingStoreImpl implements BookingStore {

    private static final String TAG = BookingStoreImpl.class.getSimpleName();
    private static final String BOOKING_PREFERENCE_NAME = "_bookingPreferences";
    private static final String BOOKING_REFERENCE = "booking";

    private SharedPreferences mSharedPreferences;
    private ObjectMapper mObjectMapper;

    public BookingStoreImpl(Context context) {
        mSharedPreferences = context.getSharedPreferences(BOOKING_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mObjectMapper = new ObjectMapper();
    }

    @Override
    public Booking getBooking() {
        Booking booking = null;
        try {
            String bookingJson = mSharedPreferences.getString(BOOKING_REFERENCE, JSONObject.NULL.toString());
            booking = mObjectMapper.readValue(bookingJson, Booking.class);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while getting booking!", e);
        }
        return booking;
    }

    @Override
    public void storeBooking(Booking booking) {
        try {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            String bookingJson = mObjectMapper.writeValueAsString(booking);
            editor.putString(BOOKING_REFERENCE, bookingJson);
            editor.apply();
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Error occurred while storing booking!", e);
        }
    }

    @Override
    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }
}
