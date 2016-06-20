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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;
import com.example.matt.movieWatchList.uitls.DrawerHelper;
import com.example.matt.movieWatchList.viewControllers.activities.SearchActivity;
import com.example.matt.movieWatchList.viewControllers.activities.movies.MovieSearchActivity;
import com.example.matt.movieWatchList.viewControllers.activities.movies.MovieWatchListActivity;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowBrowseFragment;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Provides UI for the main screen.
 */
public class TVShowBrowseActivity extends AppCompatActivity {
    private static final String TAG = MovieWatchListActivity.class.getSimpleName();
    Adapter adapterViewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    Drawer navigationDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_activity);

        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Browse Shows");

        // Setting ViewPager for each Tabs
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_trending_up).color(Color.WHITE));
        tabs.getTabAt(1).setIcon(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_new_box).color(Color.WHITE));
        tabs.getTabAt(2).setIcon(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_thumb_up).color(Color.WHITE));

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create Navigation drawer
        navigationDrawer = new DrawerHelper().GetDrawer(this, toolbar, savedInstanceState);


        // Adding Floating Action Button to bottom right of main view
        IconicsDrawable search = new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_search).sizeDp(16).color(Color.WHITE);
        fab.setImageDrawable(search);

        final Intent intent = new Intent(this, SearchActivity.class);

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
        TVShowBrowseFragment nowShowingMovies = new TVShowBrowseFragment();
        nowShowingMovies.setArguments(nowShowingBundle);

        Bundle popularBundle = new Bundle();
        popularBundle.putInt("showType", BrowseMovieType.POPULAR);
        TVShowBrowseFragment popularMovies = new TVShowBrowseFragment();
        popularMovies.setArguments(popularBundle);

        Bundle topRatedBundle = new Bundle();
        topRatedBundle.putInt("showType", BrowseMovieType.TOP_RATED);
        TVShowBrowseFragment topRatedMovies = new TVShowBrowseFragment();
        topRatedMovies.setArguments(topRatedBundle);

        adapterViewPager.addFragment(popularMovies, "Popular");
        adapterViewPager.addFragment(nowShowingMovies, "Now Airing");
        adapterViewPager.addFragment(topRatedMovies, "Top Rated");

        viewPager.setAdapter(adapterViewPager);
    }

    /*
     * Drawer Functions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem sortItem = menu.findItem(R.id.action_sort);
        sortItem.setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_sort).sizeDp(16).color(Color.WHITE));
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

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position <= mFragmentList.size()) {
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
}

