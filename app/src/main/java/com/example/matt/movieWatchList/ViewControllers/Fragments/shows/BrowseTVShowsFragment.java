/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.matt.movieWatchList.viewControllers.fragments.shows;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matt.movieWatchList.Models.POJO.shows.TVShowQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.shows.TVShowResult;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.BrowseTVShowsAPI;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;
import com.example.matt.movieWatchList.viewControllers.adapters.BrowseTVShowsAdapter;

import java.util.List;

import io.realm.RealmList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Provides UI for the view with Cards.
 */
public class BrowseTVShowsFragment extends Fragment {
    private RealmList<JSONShow> data;
    private RecyclerView recyclerView;
    private BrowseTVShowsAdapter adapter;
    private Integer showType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showType = getArguments().getInt("showType");
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(castLayoutManager);
        loadData();

        return recyclerView;
    }

    public void loadData() {
        //return recyclerView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/tv/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BrowseTVShowsAPI service = retrofit.create(BrowseTVShowsAPI.class);

        Call<TVShowQueryReturn> call;
        if (showType == BrowseMovieType.POPULAR) {
            call = service.getPopularTVShows();
        } else if (showType == BrowseMovieType.NOW_SHOWING) {
            call = service.getAiringTodayTVShows();
        } else if (showType == BrowseMovieType.TOP_RATED) {
            call = service.getTopRatedTVShows();
        } else {
            call = null;
        }
        if (call != null) {
            call.enqueue(new Callback<TVShowQueryReturn>() {
                @Override
                public void onResponse(retrofit.Response<TVShowQueryReturn> response, Retrofit retrofit) {
                    Log.d("Browsetv()", response.raw().toString());
                    List<TVShowResult> movieResults = response.body().getResults();
                    data = new RealmList<>();
                    for (TVShowResult show : movieResults){
                        JSONShow jsonShow = new JSONShow();
                        jsonShow.setName(show.getName());
                        jsonShow.setId(show.getId());
                        jsonShow.setOverview(show.getOverview());
                        jsonShow.setBackdropPath("https://image.tmdb.org/t/p/w342" + show.getBackdropPath());

                        data.add(jsonShow);
                    }
                    Log.d("BrowseMovies()", Integer.toString(data.size()));
                    adapter = new BrowseTVShowsAdapter(data, getActivity());
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("BrowseMovies()", "Callback Failure");
                }
            });
        }
        else {
            Log.d("call", "null");
        }
    }
}
