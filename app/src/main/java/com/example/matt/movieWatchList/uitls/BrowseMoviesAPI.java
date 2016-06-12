package com.example.matt.movieWatchList.uitls;

/**
 * Created by Matt on 6/7/2016.
 */

import com.example.matt.movieWatchList.Models.POJO.MovieQueryReturn;

import retrofit.Call;
import retrofit.http.GET;

public interface BrowseMoviesAPI {
    @GET("popular?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getPopularMovies();

    @GET("now_playing?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getInTheatersMovies();

    @GET("top_rated?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getTopRatedMovies();
}
