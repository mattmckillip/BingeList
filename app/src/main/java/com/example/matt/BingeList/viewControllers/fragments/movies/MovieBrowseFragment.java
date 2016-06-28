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

package com.example.matt.bingeList.viewControllers.fragments.movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.movies.MovieQueryReturn;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.BrowseMovieType;
import com.example.matt.bingeList.uitls.EndlessRecyclerOnScrollListener;
import com.example.matt.bingeList.viewControllers.adapters.MovieBrowseAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Provides UI for the view with Cards.
 */
public class MovieBrowseFragment extends Fragment {
    private static final String TAG = MovieBrowseFragment.class.getSimpleName();
    private RealmList<Movie> data;
    private RecyclerView recyclerView;
    private MovieBrowseAdapter mBrowseMoviesAdapter;
    private Integer movieType;
    private Integer mPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieType = getArguments().getInt("movieType");
        Log.d(TAG, "onCreateView() - movieType: " + Integer.toString(movieType));

        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        recyclerView.setAdapter(mBrowseMoviesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        loadData();
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                Log.d(TAG, "loadData()");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://api.themoviedb.org/3/movie/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MovieAPI service = retrofit.create(MovieAPI.class);

                Call<MovieQueryReturn> call;
                if (movieType == BrowseMovieType.POPULAR) {
                    call = service.getPopularMoviesPage(Integer.toString(mPage));
                } else if (movieType == BrowseMovieType.NOW_SHOWING) {
                    call = service.getInTheatersMoviesPage(Integer.toString(mPage));
                } else if (movieType == BrowseMovieType.TOP_RATED) {
                    call = service.getTopRatedMoviesPage(Integer.toString(mPage));
                } else {
                    call = null;
                }
                if (call != null) {
                    mPage++;
                    call.enqueue(new Callback<MovieQueryReturn>() {
                        @Override
                        public void onResponse(Call<MovieQueryReturn> call, Response<MovieQueryReturn> response) {
                            Log.d(TAG, response.raw().toString());

                            if (response.isSuccessful()) {
                                List<MovieResult> movieResults = response.body().getMovieResults();
                                data = new RealmList<>();
                                for (MovieResult movieResult : movieResults) {
                                    Movie movie = new Movie();
                                    movie.setTitle(movieResult.getTitle());
                                    movie.setId(movieResult.getId());
                                    movie.setOverview(movieResult.getOverview());
                                    movie.setBackdropPath("https://image.tmdb.org/t/p/" + getContext().getString(R.string.image_size_w500) + movieResult.getBackdropPath());
                                    data.add(movie);
                                }
                                mBrowseMoviesAdapter.addMoreMovies(data);
                            }
                        }

                        @Override
                        public void onFailure(Call<MovieQueryReturn> call, Throwable t) {
                            Log.d(TAG, "MovieQueryReturn - Failure");
                        }
                    });
                } else {
                    Log.d(TAG, "Null call");
                }

            }
        });

        return recyclerView;
    }

    public void loadData() {
        Log.d(TAG, "loadData()");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

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
                public void onResponse(Call<MovieQueryReturn> call, Response<MovieQueryReturn> response) {
                    Log.d(TAG, "MovieQueryReturn - Success");

                    List<MovieResult> movieResults = response.body().getMovieResults();
                    data = new RealmList<>();
                    for (MovieResult movieResult : movieResults) {
                        Movie movie = new Movie();
                        movie.setTitle(movieResult.getTitle());
                        movie.setId(movieResult.getId());
                        movie.setOverview(movieResult.getOverview());
                        movie.setBackdropPath("https://image.tmdb.org/t/p/" + getContext().getString(R.string.image_size_w500) + movieResult.getBackdropPath());
                        data.add(movie);
                    }
                    Realm uiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();
                    mBrowseMoviesAdapter = new MovieBrowseAdapter(data, getContext(), uiRealm);
                    recyclerView.setAdapter(mBrowseMoviesAdapter);
                }

                @Override
                public void onFailure(Call<MovieQueryReturn> call, Throwable t) {
                    Log.d(TAG, "MovieQueryReturn - Failure");
                }
            });
        } else {
            Log.d(TAG, "Null call");
        }
        mPage = 2;
    }

    public void notifyAdapter(){
        if (mBrowseMoviesAdapter != null) {
            mBrowseMoviesAdapter.notifyDataSetChanged();
        }
    }
}
