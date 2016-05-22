package com.example.dk.mapsviabt;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.GET;
import retrofit.Callback;
import retrofit.http.Query;
/**
 * Created by Karteek Dhara on 5/20/2016.
 */

//The url for all these is same currently but will be changed if necessary.
public interface HttpRequest {
    @GET("/api/?format=json")
    void getMarkers(Callback<ServerData> cb);
    @GET("/api/?format=json")
    void getData(@Query("initial_point")List<Double> initialPoint,
                 @Query("final_point")List<Double> finalPoint, Callback<ServerData> cb);


}
