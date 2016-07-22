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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.uitls.TVShowRealmStaticHelper;
import com.example.matt.bingeList.viewControllers.adapters.YourShowsAdapter;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class YourShowsFragment extends Fragment {
    private static final String TAG = YourShowsFragment.class.getSimpleName();
    private RealmList<TVShow> data = new RealmList<>();
    private RecyclerView mRecyclerView;
    private YourShowsAdapter mYourShowsAdapter;
    private boolean mUnwatchedShows;
    private Realm mUiRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        mUiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();

        if (getArguments().getInt("unwatched") == 0) {
            mUnwatchedShows = false;
            RealmResults<TVShow> tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.DESCENDING);

            RealmList<TVShow> data = new RealmList<>();
            for (TVShow tvShowResult : tvShowRealmResults) {
                data.add(tvShowResult);
            }
        } else {
            mUnwatchedShows = true;
            data = TVShowRealmStaticHelper.getSortedShowsWithUnwatchedEpisodes(mUiRealm, PreferencesHelper.getShowSort(getContext()));
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mYourShowsAdapter = new YourShowsAdapter(data, getContext(), mUiRealm, mUnwatchedShows);
        mYourShowsAdapter.sort(PreferencesHelper.getShowSort(getContext()));
        mRecyclerView.setAdapter(mYourShowsAdapter);

        //sort(PreferencesHelper.getShowSort(getContext()));
        return mRecyclerView;
    }

    public void sort(int sortType){
        mYourShowsAdapter.sort(sortType);
    }

    /*public void sort(int sortType) {
        Log.d(TAG, Integer.toString(sortType));

        if (mUnwatchedShows) {
            RealmResults<TVShow> tvShowRealmResults = null;
            if (sortType == ShowSort.RECENTLY_ADDED) {
                tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.DESCENDING);
            } else if (sortType == ShowSort.TOP_RATED) {
                tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("voteAverage", Sort.DESCENDING);
            } else if (sortType == ShowSort.ADDED_FIRST) {
                tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.ASCENDING);
            }else {
                tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.DESCENDING);
            }

            RealmList<TVShow> data = new RealmList<>();
            for (TVShow tvShowResult : tvShowRealmResults) {
                data.add(tvShowResult);
            }

            mYourShowsAdapter = new YourShowsAdapter(data, getContext(), mUiRealm);
            mYourShowsAdapter.
            mRecyclerView.setAdapter(mYourShowsAdapter);
        } else {
            data = TVShowRealmStaticHelper.getSortedShowsWithUnwatchedEpisodes(mUiRealm, sortType);

            mYourShowsAdapter = new YourShowsAdapter(data, getContext(), mUiRealm);
            mRecyclerView.setAdapter(mYourShowsAdapter);
        }
    }*/
}
