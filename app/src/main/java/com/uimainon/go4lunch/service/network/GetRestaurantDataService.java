package com.uimainon.go4lunch.service.network;

import com.uimainon.go4lunch.service.apiElements.NearbySearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRestaurantDataService {
    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("/maps/api/place/nearbysearch/json")
    Call<NearbySearch>  getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius, @Query("key") String key);

}
