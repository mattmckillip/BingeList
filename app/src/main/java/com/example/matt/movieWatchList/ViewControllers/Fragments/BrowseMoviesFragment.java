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
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.MovieQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.MovieResult;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;
import com.example.matt.movieWatchList.uitls.BrowseMoviesAPI;
import com.example.matt.movieWatchList.viewControllers.activities.WatchListDetailActivity;
import com.example.matt.movieWatchList.viewControllers.adapters.BrowseMoviesAdapter;

import java.util.List;

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
public class BrowseMoviesFragment extends Fragment {
        private RealmList<JSONMovie> data;
        private RecyclerView recyclerView;
        private BrowseMoviesAdapter adapter;
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
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/movie/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            BrowseMoviesAPI service = retrofit.create(BrowseMoviesAPI.class);

            Call<MovieQueryReturn> call;
            if (movieType == BrowseMovieType.POPULAR) {
                call = service.getPopularMovies();
            } else if (movieType == BrowseMovieType.NOW_SHOWING) {
                call = service.getInTheatersMovies();
            } else if (movieType == BrowseMovieType.TOP_RATED) {
                call = service.getTopRatedMovies();
            } else {
                call = null;
            }
            if (call != null) {
                call.enqueue(new Callback<MovieQueryReturn>() {
                    @Override
                    public void onResponse(retrofit.Response<MovieQueryReturn> response, Retrofit retrofit) {
                        List<MovieResult> movieResults = response.body().getMovieResults();
                        data = new RealmList<>();
                        for (MovieResult movie : movieResults){
                            JSONMovie jsonMove = new JSONMovie();
                            jsonMove.setTitle(movie.getTitle());
                            jsonMove.setId(movie.getId());
                            jsonMove.setOverview(movie.getOverview());
                            jsonMove.setBackdropURL("https://image.tmdb.org/t/p/w342" + movie.getBackdropPath());
                            data.add(jsonMove);
                        }
                        adapter = new BrowseMoviesAdapter(data, getActivity());
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
