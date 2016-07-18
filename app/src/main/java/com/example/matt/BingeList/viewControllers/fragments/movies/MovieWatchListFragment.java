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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matt.bingeList.models.movies.ArchivedMovies;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.Enums.MovieSort;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.adapters.MovieWatchlistAdapter;
import com.example.matt.bingeList.viewControllers.adapters.WatchedMoviesAdapter;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MovieWatchListFragment extends Fragment {
    private static final String TAG = "MovieWLFragment";
    private MovieWatchlistAdapter mWatchListAdapter;
    private WatchedMoviesAdapter mWatchedAdapter;
    private RecyclerView mRecyclerView;
    private Realm mUiRealm;
    private Context mContext;
    boolean isWatched;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        mUiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();
        mContext = container.getContext();

        if (getArguments().getInt("watched") == 1) {
            isWatched = true;
            mWatchedAdapter = new WatchedMoviesAdapter(new RealmList<Movie>(), getContext(), mUiRealm);

        } else {
            isWatched = false;
            mWatchListAdapter = new MovieWatchlistAdapter(new RealmList(), getContext(), mUiRealm);
        }
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mWatchListAdapter);

        //TODO get from context
        //int sortType = MovieSort.TOP_RATED;
        //sort(sortType);
        sort(PreferencesHelper.getMovieSort(getContext()));


        return mRecyclerView;
    }

    public void notifyAdapter(){
        if (mWatchListAdapter != null || mWatchedAdapter != null) {
            int sortType = PreferencesHelper.getMovieSort(getContext());
            sort(sortType);
        }
    }

    public void sort(int sortType){
        Log.d(TAG, Integer.toString(sortType));

        if (mWatchListAdapter != null) {
            RealmResults<Movie> movieRealmResults = null;
            if (sortType == MovieSort.RECENTLY_ADDED){
                movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAll();
            } else if (sortType == MovieSort.TOP_RATED){
                movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAllSorted("voteAverage", Sort.DESCENDING);
            } else if (sortType == MovieSort.RUNTIME_DESCENDING){
                movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAllSorted("runtime", Sort.DESCENDING);
            } else if (sortType == MovieSort.RUNTIME_ASCENDING){
                movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAllSorted("runtime", Sort.ASCENDING);
            }

            RealmList<Movie> movies = new RealmList<>();
            for (Movie movieResult : movieRealmResults) {
                if(mUiRealm.where(ArchivedMovies.class).equalTo("movieId", movieResult.getId()).count() == 0) {
                    movies.add(movieResult);
                }
            }

            mWatchListAdapter = new MovieWatchlistAdapter(movies, getContext(), mUiRealm);
            mRecyclerView.setAdapter(mWatchListAdapter);
        }
        if (mWatchedAdapter != null) {
            RealmResults<Movie> movieRealmResults = null;
            if (sortType == MovieSort.RECENTLY_ADDED){
                movieRealmResults = mUiRealm.where(Movie.class).equalTo("isWatched", true).findAll();
            } else if (sortType == MovieSort.TOP_RATED){
                movieRealmResults = mUiRealm.where(Movie.class).equalTo("isWatched", true).findAllSorted("voteAverage", Sort.DESCENDING);
            }  else if (sortType == MovieSort.RUNTIME_DESCENDING){
                movieRealmResults = mUiRealm.where(Movie.class).equalTo("isWatched", true).findAllSorted("runtime", Sort.DESCENDING);
            } else if (sortType == MovieSort.RUNTIME_ASCENDING){
                movieRealmResults = mUiRealm.where(Movie.class).equalTo("isWatched", true).findAllSorted("runtime", Sort.ASCENDING);
            }

            RealmList<Movie> movies = new RealmList<>();
            for (Movie movieResult : movieRealmResults) {
                if(mUiRealm.where(ArchivedMovies.class).equalTo("movieId", movieResult.getId()).count() == 0) {
                    movies.add(movieResult);
                }
            }
            mWatchedAdapter = new WatchedMoviesAdapter(movies, getContext(), mUiRealm);
            mRecyclerView.setAdapter(mWatchedAdapter);
        }
    }
}
