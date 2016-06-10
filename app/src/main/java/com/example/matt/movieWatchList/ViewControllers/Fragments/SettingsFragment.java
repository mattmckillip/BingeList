package com.example.matt.movieWatchList.viewControllers.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.matt.movieWatchList.R;

/**
 * Created by Matt on 6/10/2016.
 */
public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}