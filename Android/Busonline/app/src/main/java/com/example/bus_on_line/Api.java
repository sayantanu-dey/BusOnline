package com.example.bus_on_line;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL="http://ec2-3-19-58-5.us-east-2.compute.amazonaws.com:8080";

    @GET("/")
    Call<DataModel> getData();


}
