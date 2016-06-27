package com.example.matt.bingeList.uitls.API;

import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.models.shows.TVShowSeasonResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Matt on 6/13/2016.
 */
public interface TVShowAPI {
    @GET("{showID}?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<TVShow> getTVShow(@Path("showID") String showID);

    @GET("{showID}/credits?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<Credits> getCredits(@Path("showID") String showID);

    @GET("{showID}/season/{seasonNumber}?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<TVShowSeasonResult> getSeasons(@Path("showID") String showID, @Path("seasonNumber") String seasonNumber);
}
