package com.wedriveu.mobile.service.scheduling;

import com.wedriveu.mobile.service.scheduling.model.VehicleResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
     * @param userLatitude
     * @param userLongitude
     * @param destinationLatitude
     * @param destinationLongitude
     * @return A Retrofit {@linkplain Call}.
     */
    @GET("vehicle/nearest")
    Call<VehicleResponse> schedule(@Query("userLatitude") double userLatitude,
                                   @Query("userLongitude") double userLongitude,
                                   @Query("destinationLatitude") double destinationLatitude,
                                   @Query("destinationLongitude") double destinationLongitude);
}
