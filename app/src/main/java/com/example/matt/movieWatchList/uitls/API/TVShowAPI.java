package com.example.matt.movieWatchList.uitls.API;

import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.movies.Movie;
import com.example.matt.movieWatchList.Models.POJO.shows.TVShow;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Matt on 6/13/2016.
 */
public interface TVShowAPI  {
        @GET("{showID}?api_key=788bf2d4d9f5db03979efed58cbf6713")
        Call<TVShow> getTVShow(@Path("showID") String showID);

        @GET("{showID}/credits?api_key=788bf2d4d9f5db03979efed58cbf6713")
        Call<Credits> getCredits(@Path("showID") String showID);
    }
