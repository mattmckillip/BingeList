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

package com.example.matt.movieWatchList.viewControllers.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.Cast;
import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.Crew;
import com.example.matt.movieWatchList.Models.POJO.Movie;
import com.example.matt.movieWatchList.Models.POJO.TVShowQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.TVShowResult;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.BrowseTVShowsAPI;
import com.example.matt.movieWatchList.viewControllers.activities.TmdbActivity;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;
import com.example.matt.movieWatchList.uitls.MovieAPI;
import com.example.matt.movieWatchList.viewControllers.adapters.BrowseMoviesAdapter;
import com.example.matt.movieWatchList.viewControllers.adapters.BrowseTVShowsAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Provides UI for the view with Cards.
 */
public class BrowseTVShowsFragment extends Fragment {
    private RealmList<JSONMovie> data;
    private RecyclerView recyclerView;
    private BrowseTVShowsAdapter adapter;
    private Integer movieType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieType = getArguments().getInt("movieType");
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
        if (movieType == BrowseMovieType.POPULAR) {
            call = service.getPopularTVShows();
        } else if (movieType == BrowseMovieType.NOW_SHOWING) {
            call = service.getAiringTodayTVShows();
        } else if (movieType == BrowseMovieType.TOP_RATED) {
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
                    for (TVShowResult movie : movieResults){
                        JSONMovie jsonMove = new JSONMovie();
                        jsonMove.setTitle(movie.getName());
                        jsonMove.setId(movie.getId());
                        jsonMove.setOverview(movie.getOverview());
                        jsonMove.setBackdropURL("https://image.tmdb.org/t/p/w342" + movie.getBackdropPath());

                        data.add(jsonMove);
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
