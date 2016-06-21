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

package com.example.matt.movieWatchList.viewControllers.activities.shows;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.DrawerHelper;
import com.example.matt.movieWatchList.viewControllers.activities.movies.MovieBrowseActivity;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowWatchListFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;


/**
 * Provides UI for the main screen.
 */
public class TVShowWatchListActivity extends AppCompatActivity {
    private static final String TAG = TVShowWatchListActivity.class.getSimpleName();
    Adapter adapterViewPager;
    Drawer navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_activity);

        // Instantiate realms
        RealmConfiguration config1 = new RealmConfiguration.Builder(this)
                .name("default")
                .schemaVersion(6)
                .migration(new RealmMigration() {
                    @Override
                    public long execute(Realm realm, long version) {
                        return 6;
                    }
                })
                .build();

        Realm.setDefaultConfiguration(config1);
        Realm uiRealm = Realm.getInstance(getApplicationContext());

        ((MyApplication) this.getApplication()).setUiRealm(uiRealm);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_show_watchlist);

        // Setting ViewPager for each Tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        try {
            tabs.getTabAt(0).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));
            tabs.getTabAt(1).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
        } catch (NullPointerException npe) {
            //pass
        }

        // Create Navigation drawer
        navigationDrawer = new DrawerHelper().GetDrawer(this, toolbar, savedInstanceState);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).sizeDp(16).color(Color.WHITE));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        IconicsDrawable search = new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_search).sizeDp(24).color(Color.WHITE);
        fab.setImageDrawable(search);

        final Intent intent = new Intent(this, MovieBrowseActivity.class);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        adapterViewPager = new Adapter(getSupportFragmentManager());

        Bundle watchedMoviesBundle = new Bundle();
        watchedMoviesBundle.putInt("watched", 1);
        TVShowWatchListFragment watchedMovies = new TVShowWatchListFragment();
        watchedMovies.setArguments(watchedMoviesBundle);

        Bundle watchListMoviesBundle = new Bundle();
        watchListMoviesBundle.putInt("watched", 0);
        TVShowWatchListFragment watchListMovies = new TVShowWatchListFragment();
        watchListMovies.setArguments(watchListMoviesBundle);

        adapterViewPager.addFragment(watchListMovies, "Your Shows");
        adapterViewPager.addFragment(watchedMovies, "New Episodes");
        viewPager.setAdapter(adapterViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem sortItem = menu.findItem(R.id.action_sort);
        sortItem.setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_sort).sizeDp(16).color(Color.WHITE));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.d("onOptionsItemSelected()", "settings");

                return true;

            case R.id.action_sort:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Log.d("onOptionsItemSelected()", "Sort");

                return true;

            case android.R.id.home:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Log.d("onOptionsItemSelected()", "Sort");
                navigationDrawer.openDrawer();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

        /*int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);*/
    }

    @Override
    public void onBackPressed() {
        if (this.navigationDrawer.isDrawerOpen()) {
            this.navigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
