package com.example.matt.movieWatchList.uitls;

import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.Movie;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Matt on 6/7/2016.
 */
public interface MovieAPI {
    @GET("{movieID}?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<Movie> getMovie(@Path("movieID") String movieID);

    @GET("{movieID}/credits?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<Credits> getCredits(@Path("movieID") String movieID);
}
