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

package com.example.matt.movieWatchList.viewControllers.activities;

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
import com.example.matt.movieWatchList.viewControllers.fragments.BrowseMoviesFragment;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Provides UI for the main screen.
 */
public class BrowseMoviesActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    Adapter adapterViewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(this.getAssets(), "fonts/Lobster-Regular.ttf");
                if(tv.getText().equals(this.getTitle())){
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }

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
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        //Check to see which item was being clicked and perform appropriate action
                        switch (menuItem.getItemId()) {

                            //Replacing the main content with ContentFragment
                            case R.id.movie_watch_list_menu_item:
                                Intent watchListIntent = new Intent(BrowseMoviesActivity.this, MainActivity.class);
                                startActivity(watchListIntent);
                                return true;

                            case R.id.movie_browse_menu_item:
                                mDrawerLayout.closeDrawers();
                                return true;

                            case R.id.tv_browse_menu_item:
                                Intent browseTVShowsIntent = new Intent(BrowseMoviesActivity.this, BrowseTVShowsActivity.class);
                                startActivity(browseTVShowsIntent);
                                return true;

                            case R.id.movie_search_menu_item:
                                Intent searchIntent = new Intent(BrowseMoviesActivity.this, SearchActivity.class);
                                startActivity(searchIntent);
                                return true;

                            case R.id.settings_menu_item:
                                Intent settingsIntent = new Intent(BrowseMoviesActivity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                                return true;
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        // Adding Floating Action Button to bottom right of main view
        fab.setImageResource(R.drawable.ic_search_white);
        final Intent intent = new Intent(this, SearchActivity.class);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        /*TextView navHeaderText = (TextView) findViewById(R.id.nav_header_text);
        Typeface font = Typeface.
                createFromAsset(this.getAssets(), "fonts/Lobster-Regular.ttf");
        navHeaderText.setTypeface(font);*/
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        adapterViewPager = new Adapter(getSupportFragmentManager());


        Bundle nowShowingBundle = new Bundle();
        nowShowingBundle.putInt("movieType", BrowseMovieType.NOW_SHOWING);
        BrowseMoviesFragment nowShowingMovies = new BrowseMoviesFragment();
        nowShowingMovies.setArguments(nowShowingBundle);

        Bundle popularBundle = new Bundle();
        popularBundle.putInt("movieType", BrowseMovieType.POPULAR);
        BrowseMoviesFragment popularMovies = new BrowseMoviesFragment();
        popularMovies.setArguments(popularBundle);

        Bundle topRatedBundle = new Bundle();
        topRatedBundle.putInt("movieType", BrowseMovieType.TOP_RATED);
        BrowseMoviesFragment topRatedMovies = new BrowseMoviesFragment();
        topRatedMovies.setArguments(topRatedBundle);

        adapterViewPager.addFragment(popularMovies, " Popular");
        adapterViewPager.addFragment(nowShowingMovies, " In Theaters");
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
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

