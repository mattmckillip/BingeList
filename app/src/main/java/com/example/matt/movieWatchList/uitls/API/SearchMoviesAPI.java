package com.example.matt.movieWatchList.uitls.API;

import com.example.matt.movieWatchList.Models.POJO.movies.MovieQueryReturn;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Matt on 6/11/2016.
 */
public interface SearchMoviesAPI {
    @GET("?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> searchKeywords(@Query("query") String keywords);
}
