package com.wedriveu.mobile.service.scheduling;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

/**
 * <p>
 *     Scheduling service HTTP Rest interface. Handles the HTTP request to find the proper vehicle
 * </p>
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingServiceApi {

    /**
     * Retrofit schedule web api interface definition.
     * @param userPosition
     * @param destinationPosition
     * @return A Retrofit {@linkplain Call}.
     */
    @POST("vehicle/findnearest")
    Call<Void> schedule(@Query("userPosition") Map<Double, Double> userPosition, @Query("destinationPosition") Map<Double, Double> destinationPosition);
}
