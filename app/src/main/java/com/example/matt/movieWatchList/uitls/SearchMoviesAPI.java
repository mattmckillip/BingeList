package com.example.matt.movieWatchList.uitls;

import com.example.matt.movieWatchList.Models.POJO.QueryReturn;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Matt on 6/11/2016.
 */
public interface SearchMoviesAPI {
    @GET("?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<QueryReturn> searchKeywords(@Query("query") String keywords);
}
