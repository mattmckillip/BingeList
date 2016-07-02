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

package com.example.matt.bingeList.viewControllers.fragments.shows;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.models.shows.TVShowQueryReturn;
import com.example.matt.bingeList.models.shows.TVShowResult;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.BrowseTVShowsAPI;
import com.example.matt.bingeList.uitls.Enums.BrowseMovieType;
import com.example.matt.bingeList.viewControllers.adapters.BrowseTVShowsAdapter;

import java.util.List;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provides UI for the view with Cards.
 */
public class TVShowBrowseFragment extends Fragment {
    private static final String TAG = TVShowBrowseFragment.class.getSimpleName();
    private RealmList<TVShow> data;
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
        Log.d(TAG, "loadData()");

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
                public void onResponse(Call<TVShowQueryReturn> call, Response<TVShowQueryReturn> response) {
                    Log.d(TAG, "TVShowQueryReturn - Success");

                    List<TVShowResult> movieResults = response.body().getResults();
                    data = new RealmList<>();
                    for (TVShowResult show : movieResults) {
                        TVShow thisShow = new TVShow();
                        thisShow.setName(show.getName());
                        thisShow.setId(show.getId());
                        thisShow.setOverview(show.getOverview());
                        thisShow.setBackdropPath("https://image.tmdb.org/t/p/w342" + show.getBackdropPath());

                        data.add(thisShow);
                    }
                    adapter = new BrowseTVShowsAdapter(data, getActivity());
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<TVShowQueryReturn> call, Throwable t) {
                    Log.d(TAG, t.toString());
                    Log.d(TAG, "TVShowQueryReturn - Failure");
                }
            });
        } else {
            Log.d("call", "null");
        }
    }
}
