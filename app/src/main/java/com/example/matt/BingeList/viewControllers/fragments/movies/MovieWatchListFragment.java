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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.viewControllers.activities.movies.MovieWatchListDetailActivity;
import com.example.matt.bingeList.viewControllers.adapters.MovieWatchedAdapter;
import com.example.matt.bingeList.viewControllers.adapters.MoviesWatchListAdapter;
import com.mikepenz.iconics.view.IconicsButton;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MovieWatchListFragment extends Fragment {
    MoviesWatchListAdapter mWatchListAdapter;
    MovieWatchedAdapter mWatchedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        boolean isWatched;

        if (getArguments().getInt("watched") == 1) {
            isWatched = true;
        } else {
            isWatched = false;
        }

        Realm uiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();
        RealmQuery<Movie> query = uiRealm.where(Movie.class);

        // Execute the query:
        if (isWatched) {
            RealmResults<Movie> movies = query.equalTo("isWatched", true).findAll();
            mWatchedAdapter = new MovieWatchedAdapter(movies, getContext(), uiRealm);
            recyclerView.setAdapter(mWatchedAdapter);

        } else {
            RealmResults<Movie> movies = query.equalTo("onWatchList", true).findAll();
            mWatchListAdapter = new MoviesWatchListAdapter(movies, getContext(), uiRealm);
            recyclerView.setAdapter(mWatchListAdapter);

        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public void notifyAdapter(){
        if (mWatchListAdapter != null) {
            mWatchListAdapter.notifyDataSetChanged();
        }

        if (mWatchedAdapter != null) {
            mWatchedAdapter.notifyDataSetChanged();
        }
    }
}
