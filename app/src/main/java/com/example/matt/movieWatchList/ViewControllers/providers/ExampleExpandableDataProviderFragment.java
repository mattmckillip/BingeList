package com.example.matt.movieWatchList.viewControllers.providers;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.matt.movieWatchList.viewControllers.adapters.AbstractExpandableDataProvider;


public class ExampleExpandableDataProviderFragment extends Fragment {
    private ExampleExpandableDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new ExampleExpandableDataProvider();
    }

    public AbstractExpandableDataProvider getDataProvider() {
        return mDataProvider;
    }
}