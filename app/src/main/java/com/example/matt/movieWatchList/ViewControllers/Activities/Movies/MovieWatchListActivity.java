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

package com.example.matt.movieWatchList.viewControllers.activities.movies;

import android.app.Activity;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;

import com.example.matt.movieWatchList.uitls.DrawerHelper;
import com.example.matt.movieWatchList.viewControllers.activities.SearchActivity;
import com.example.matt.movieWatchList.viewControllers.activities.SettingsActivity;
import com.example.matt.movieWatchList.viewControllers.activities.shows.BrowseTVShowsActivity;
import com.example.matt.movieWatchList.viewControllers.activities.shows.TVShowWatchListActivity;
import com.example.matt.movieWatchList.viewControllers.fragments.movies.MovieWatchListFragment;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;


/**
 * Provides UI for the main screen.
 */
public class MovieWatchListActivity extends AppCompatActivity {
    private static final String TAG = MovieWatchListActivity.class.getSimpleName();
    Adapter adapterViewPager;

    Drawer navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        getSupportActionBar().setTitle("Movie Watch List");
        applyFontForToolbarTitle(this);

        // Setting ViewPager for each Tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        try {
            tabs.getTabAt(0).setIcon(R.drawable.ic_dvr_white_24dp);
            tabs.getTabAt(1).setIcon(R.drawable.ic_playlist_add_check_white_24dp);
        } catch (NullPointerException npe) {
            //pass
        }

        // Create Navigation drawer
        navigationDrawer = new DrawerHelper().GetDrawer(this, toolbar, savedInstanceState);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent intent = new Intent(this, BrowseMoviesActivity.class);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        /*TextView navHeaderText = (TextView) mDrawerLayout.findViewById(R.id.nav_header_text);
        Typeface font = Typeface.
                createFromAsset(this.getAssets(), "fonts/Lobster-Regular.ttf");
        navHeaderText.setTypeface(font);*/
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        adapterViewPager = new Adapter(getSupportFragmentManager());

        Bundle watchedMoviesBundle = new Bundle();
        watchedMoviesBundle.putInt("watched", 1);
        MovieWatchListFragment watchedMovies = new MovieWatchListFragment();
        watchedMovies.setArguments(watchedMoviesBundle);

        Bundle watchListMoviesBundle = new Bundle();
        watchListMoviesBundle.putInt("watched", 0);
        MovieWatchListFragment watchListMovies = new MovieWatchListFragment();
        watchListMovies.setArguments(watchListMoviesBundle);

        adapterViewPager.addFragment(watchListMovies, " Watch List");
        adapterViewPager.addFragment(watchedMovies, " Watched");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    }

    @Override
    public void onBackPressed() {
        if (this.navigationDrawer.isDrawerOpen()) {
            this.navigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public static void applyFontForToolbarTitle(Activity context) {
        Toolbar toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(context.getAssets(), "fonts/Lobster-Regular.ttf");
                if (tv.getText().equals(context.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
    }
}
