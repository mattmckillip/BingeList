package com.example.matt.bingeList.uitls.API;

import com.example.matt.bingeList.models.movies.MovieQueryReturn;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Matt on 6/11/2016.
 */
public interface SearchMoviesAPI {
    @GET("?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> searchKeywords(@Query("query") String keywords);
}
