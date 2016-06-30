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
import android.widget.Toast;

import com.example.matt.bingeList.models.movies.ArchivedMovies;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.viewControllers.adapters.WatchedListMoviesAdapter;
import com.example.matt.bingeList.viewControllers.adapters.MoviesWatchListAdapter;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MovieWatchListFragment extends Fragment {
    MoviesWatchListAdapter mWatchListAdapter;
    WatchedListMoviesAdapter mWatchedAdapter;
    Realm mUiRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        boolean isWatched;
        mUiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();

        if (getArguments().getInt("watched") == 1) {
            isWatched = true;
        } else {
            isWatched = false;
        }

        Realm uiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();
        RealmQuery<Movie> query = uiRealm.where(Movie.class);

        if (isWatched) {
            RealmResults<Movie> movieRealmResults = query.equalTo("isWatched", true).findAll();
            RealmList<Movie> movies = new RealmList<>();
            for (Movie movieResult : movieRealmResults) {
                if(mUiRealm.where(ArchivedMovies.class).equalTo("movieId", movieResult.getId()).count() == 0) {
                    movies.add(movieResult);
                }
            }
            mWatchedAdapter = new WatchedListMoviesAdapter(movies, getContext(), uiRealm);
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
            RealmResults<Movie> movieRealmResults = mUiRealm.where(Movie.class).equalTo("isWatched", true).findAll();
            RealmList<Movie> movies = new RealmList<>();
            for (Movie movieResult : movieRealmResults) {
                if(mUiRealm.where(ArchivedMovies.class).equalTo("movieId", movieResult.getId()).count() == 0) {
                    movies.add(movieResult);
                }
            }
            mWatchedAdapter.UpdateData(movies);
            Log.d("MovieWatchListFragment", "mWatchedAdapter.notifyDataSetChanged()");
        }
    }
}
