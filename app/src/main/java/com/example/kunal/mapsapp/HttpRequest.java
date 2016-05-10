package com.example.kunal.mapsapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HttpRequest {

    /*Retrofit get annotation with our URL
       And our method that will return us the list ob Book
    */
    @GET("/api")
    public Call<ResponseData> getData();

}