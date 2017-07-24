package com.wedriveu.mobile.service.scheduling;

import com.wedriveu.mobile.service.scheduling.model.VehicleResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Scheduling service HTTP Rest interface. Handles the HTTP request to find the proper vehicle
 *
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingServiceApi {

    /**
     * Retrofit schedule web api interface definition.
     * @param userLatitude The user Latitude taken by the
     * @param userLongitude The user Longitude
     * @param destinationLatitude
     * @param destinationLongitude
     * @return A Retrofit {@linkplain Call}.
     */
    @GET("vehicle/nearest")
    Call<VehicleResponse> schedule(@Query("userLatitude") Double userLatitude,
                                   @Query("userLongitude") Double userLongitude,
                                   @Query("destinationLatitude") Double destinationLatitude,
                                   @Query("destinationLongitude") Double destinationLongitude);

}
