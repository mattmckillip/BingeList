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

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;
import com.example.matt.movieWatchList.uitls.DrawerHelper;
import com.example.matt.movieWatchList.viewControllers.activities.movies.SearchMoviesActivity;
import com.example.matt.movieWatchList.viewControllers.activities.movies.MovieWatchListActivity;
import com.example.matt.movieWatchList.viewControllers.activities.SettingsActivity;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.BrowseTVShowsFragment;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Provides UI for the main screen.
 */
public class BrowseTVShowsActivity extends AppCompatActivity {
    private static final String TAG = MovieWatchListActivity.class.getSimpleName();
    Adapter adapterViewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    Drawer navigationDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Browse Shows");

        // Setting ViewPager for each Tabs
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(R.drawable.ic_trending_up_white_24dp);
        tabs.getTabAt(1).setIcon(R.drawable.ic_theaters_white_24dp);
        tabs.getTabAt(2).setIcon(R.drawable.ic_thumb_up_white_24dp);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create Navigation drawer
        navigationDrawer = new DrawerHelper().GetDrawer(this, toolbar, savedInstanceState);


        // Adding Floating Action Button to bottom right of main view
        fab.setImageResource(R.drawable.ic_search_white);
        final Intent intent = new Intent(this, SearchMoviesActivity.class);

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


        Bundle nowShowingBundle = new Bundle();
        nowShowingBundle.putInt("showType", BrowseMovieType.NOW_SHOWING);
        BrowseTVShowsFragment nowShowingMovies = new BrowseTVShowsFragment();
        nowShowingMovies.setArguments(nowShowingBundle);

        Bundle popularBundle = new Bundle();
        popularBundle.putInt("showType", BrowseMovieType.POPULAR);
        BrowseTVShowsFragment popularMovies = new BrowseTVShowsFragment();
        popularMovies.setArguments(popularBundle);

        Bundle topRatedBundle = new Bundle();
        topRatedBundle.putInt("showType", BrowseMovieType.TOP_RATED);
        BrowseTVShowsFragment topRatedMovies = new BrowseTVShowsFragment();
        topRatedMovies.setArguments(topRatedBundle);

        adapterViewPager.addFragment(popularMovies, " Popular");
        adapterViewPager.addFragment(nowShowingMovies, " Now Airing");
        adapterViewPager.addFragment(topRatedMovies, " Top Rated");

        viewPager.setAdapter(adapterViewPager);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if(position <= mFragmentList.size())
            {
                return mFragmentList.get(position);
            }
            return null;
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

    /*
     * Drawer Functions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("onQueryTextSubmit()", "HERE");

                Bundle searchBundle = new Bundle();
                searchBundle.putString("query", query);
                SearchFragment searchMovies = new SearchFragment();
                searchMovies.setArguments(searchBundle);

                adapterViewPager.addFragment(searchMovies, " Search");
                adapterViewPager.notifyDataSetChanged();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("onQueryTextChange()", "HERE");
                return false;
            }
        });*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            navigationDrawer.openDrawer();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.navigationDrawer.isDrawerOpen()) {
            this.navigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}

