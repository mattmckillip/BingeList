package com.example.matt.bingeList.uitls.API;

import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.movies.MovieQueryReturn;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Matt on 6/7/2016.
 */
public interface MovieAPI {
    @GET("{movieID}?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<Movie> getMovie(@Path("movieID") String movieID);

    @GET("{movieID}/credits?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<Credits> getCredits(@Path("movieID") String movieID);

    @GET("{movieID}/similar?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getSimilarMovies(@Path("movieID") String movieID);

    @GET("popular?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getPopularMovies();

    @GET("now_playing?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getInTheatersMovies();

    @GET("top_rated?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getTopRatedMovies();

    @GET("popular?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getPopularMoviesPage(@Query("page") String page);

    @GET("now_playing?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getInTheatersMoviesPage(@Query("page") String page);

    @GET("top_rated?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<MovieQueryReturn> getTopRatedMoviesPage(@Query("page") String page);
}
