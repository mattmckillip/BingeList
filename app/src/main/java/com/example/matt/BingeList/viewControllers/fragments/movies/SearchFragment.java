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

import com.example.matt.bingeList.models.movies.MovieQueryReturn;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.SearchMoviesAPI;
import com.example.matt.bingeList.viewControllers.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchFragment extends Fragment {
    private List<MovieResult> searchMovieResults;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private String query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        query = getArguments().getString("query");

        searchMovieResults = new ArrayList<>();

        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new SearchAdapter(searchMovieResults, getActivity());
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager searchLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(searchLayoutManager);
        searchMovies(query);

        return recyclerView;
    }

    public void searchMovies(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/search/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SearchMoviesAPI service = retrofit.create(SearchMoviesAPI.class);

        Call<MovieQueryReturn> call = service.searchKeywords(query.replaceAll(" ", "+"));

        call.enqueue(new Callback<MovieQueryReturn>() {
            @Override
            public void onResponse(Call<MovieQueryReturn> call, Response<MovieQueryReturn> response) {
                Log.d("getMovie()", "Callback Success");
                List<MovieResult> movieResults = response.body().getMovieResults();

                recyclerView.setAdapter(new SearchAdapter(movieResults, getContext()));
                recyclerView.setFocusable(false);
            }

            @Override
            public void onFailure(Call<MovieQueryReturn> call, Throwable t) {
                Log.d("getMovie()", "Callback Failure");
            }
        });
    }
}
