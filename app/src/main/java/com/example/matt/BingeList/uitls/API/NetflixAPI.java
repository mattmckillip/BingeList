package com.example.matt.bingeList.uitls.API;

import com.example.matt.bingeList.models.NetflixRouletteResponse;
import com.example.matt.bingeList.models.movies.MovieQueryReturn;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Matt on 7/25/2016.
 */
public interface NetflixAPI {

    public String baseURL = "https://netflixroulette.net/api/v2/";
    @GET("usa/imdb/")
    Call<NetflixRouletteResponse> checkNetflix(@Query("imdbId") String imdbID);

}
