package com.example.matt.bingeList.uitls.API;

import com.example.matt.bingeList.models.shows.TVShowQueryReturn;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by Matt on 6/12/2016.
 */
public interface BrowseTVShowsAPI {
    @GET("popular?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<TVShowQueryReturn> getPopularTVShows();

    @GET("airing_today?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<TVShowQueryReturn> getAiringTodayTVShows();

    @GET("on_the_air?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<TVShowQueryReturn> getAiringThisWeekTVShows();

    @GET("top_rated?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<TVShowQueryReturn> getTopRatedTVShows();
}
