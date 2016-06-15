package com.example.matt.movieWatchList.uitls.API;

import com.example.matt.movieWatchList.Models.POJO.MultiSearchQueryReturn;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Matt on 6/15/2016.
 */
public interface MultiSearchAPI {
    @GET("?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MultiSearchQueryReturn> searchKeywords(@Query("query") String keywords);
}
