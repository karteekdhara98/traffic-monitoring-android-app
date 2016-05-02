package com.example.kunal.mapsapp;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface HttpRequest {

    /*Retrofit get annotation with our URL
       And our method that will return us the list ob Book
    */
    @GET("/posts/1")
    public void getBooks(Callback<ResponseData> response);
}