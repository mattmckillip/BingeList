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

import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.adapters.movies.MovieWatchlistAdapter;
import com.example.matt.bingeList.viewControllers.adapters.movies.WatchedMoviesAdapter;

import io.realm.Realm;
import io.realm.RealmList;

public class MovieWatchListFragment extends Fragment {
    private static final String TAG = "MovieWLFragment";
    private MovieWatchlistAdapter mWatchListAdapter;
    private WatchedMoviesAdapter mWatchedAdapter;
    private RecyclerView mRecyclerView;
    private Realm mUiRealm;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        mUiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();
        mContext = container.getContext();

        if (getArguments().getInt("watched") == 1) {
            mWatchListAdapter = null;
            mWatchedAdapter = new WatchedMoviesAdapter(new RealmList<Movie>(), getContext(), mUiRealm);
            mRecyclerView.setAdapter(mWatchedAdapter);
        } else {
            mWatchedAdapter = null;
            mWatchListAdapter = new MovieWatchlistAdapter(new RealmList(), getContext(), mUiRealm);
            mRecyclerView.setAdapter(mWatchListAdapter);
        }
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sort(PreferencesHelper.getMovieSort(mContext));

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
            mWatchListAdapter.sort(sortType);
        }else if (mWatchedAdapter != null) {
            mWatchedAdapter.sort(sortType);
        }
    }
}
