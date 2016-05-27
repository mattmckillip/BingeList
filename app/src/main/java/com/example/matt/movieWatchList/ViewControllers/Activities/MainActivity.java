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

package com.example.matt.movieWatchList.ViewControllers.Activities;

import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import com.example.matt.movieWatchList.Models.Movie;
import com.example.matt.movieWatchList.Models.MovieWatchList;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.ViewControllers.Fragments.CardContentFragment;
import com.example.matt.movieWatchList.ViewControllers.Fragments.PopularMoviesFragment;
import com.example.matt.movieWatchList.ViewControllers.Fragments.testFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * Provides UI for the main screen.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    Adapter adapterViewPager;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Instantiate realms
        Realm uiRealm =  Realm.getInstance(getApplicationContext());
        /*RealmConfiguration config1 = new RealmConfiguration.Builder(this)
                .name("default")
                .schemaVersion(1)
                .migration(new RealmMigration() {
                    @Override
                    public long execute(Realm realm, long version) {
                        return 1;
                    }
                })
                .build();

        Realm uiRealm = Realm.getInstance(config1);*/


        /*uiRealm.beginTransaction();
        Movie movie1 = uiRealm.createObject(Movie.class); // Create a new object
        Movie movie2 = uiRealm.createObject(Movie.class); // Create a new object
        Movie movie3 = uiRealm.createObject(Movie.class); // Create a new object
        MovieWatchList list = uiRealm.createObject(MovieWatchList.class);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.the_godfather);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        movie1.setId(0);
        movie1.setName("The Godfather");
        movie1.setCountry("United States");
        movie1.setGenre("Drama, Crime");
        movie1.setPlot("The story spans the years from 1945 to 1955 and chronicles the fictional Italian-American Corleone crime family. When organized crime family patriarch Vito Corleone barely survives an attempt on his life, his youngest son, Michael, steps in to take care of the would-be killers, launching a campaign of bloody revenge.");
        movie1.setReleaseDate("1972-03-15");
        movie1.setImage(byteArray);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.good_will_hunting);
        stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();


        movie2.setId(1);
        movie2.setName("Good Will Hunting");
        movie2.setCountry("United States");
        movie2.setGenre("Drama");
        movie2.setPlot("Will Hunting, a janitor at MIT, has a gift for mathematics but needs help from a psychologist to find direction in his life.");
        movie2.setReleaseDate("1998-01-09");
        movie2.setImage(byteArray);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.the_revenant);
        stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();

        movie3.setId(2);
        movie3.setName("The Revenant");
        movie3.setCountry("United States");
        movie3.setGenre("Drama");
        movie3.setPlot("In the 1820s, a frontiersman, Hugh Glass, sets out on a path of vengeance against those who left him for dead after a bear mauling.");
        movie3.setReleaseDate("2016-01-06");
        movie3.setImage(byteArray);

        // Add movies to realm movie list
        RealmResults<Movie> movieResult = uiRealm.where(Movie.class).findAll();
        RealmList<Movie> movieList = new RealmList<Movie>();
        for(Movie student : movieResult) {
            movieList.add(student);
        }
        list.setMovieList(movieList);
        list.setId(0);

        uiRealm.commitTransaction();*/
        Realm.setDefaultConfiguration(uiRealm.getConfiguration());
        ((MyApplication) this.getApplication()).setUiRealm(uiRealm);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

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
                        Adapter adapter = new Adapter(getSupportFragmentManager());
                        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);


                        //Check to see which item was being clicked and perform appropriate action
                        switch (menuItem.getItemId()) {

                            //Replacing the main content with ContentFragment


                            case R.id.watch_list_menu_item:

                                adapter.addFragment(new CardContentFragment(), "Watch List");
                                adapter.addFragment(new CardContentFragment(), "Watched");
                                viewPager.setAdapter(adapter);

                                // Set Tabs inside Toolbar
                                tabs.setupWithViewPager(viewPager);

                                mDrawerLayout.closeDrawers();
                                return true;

                            case R.id.popular_menu_item:
                                Log.d("ADAPTER COUNT", Integer.toString(adapter.getCount()));
                                Snackbar.make(getCurrentFocus(), "Popular",
                                        Snackbar.LENGTH_LONG).show();

                                adapter = new Adapter(getSupportFragmentManager());
                                adapter.addFragment(new testFragment(), "Popular Movies");
                                Log.d("ADAPTER COUNT", Integer.toString(adapter.getCount()));
                                viewPager.setAdapter(adapter);

                                // Set Tabs inside Toolbar
                                tabs.setupWithViewPager(viewPager);

                                mDrawerLayout.closeDrawers();
                                return true;
                            case R.id.new_releases_menu_item:
                                Snackbar.make(getCurrentFocus(), "New Release",
                                        Snackbar.LENGTH_LONG).show();
                                mDrawerLayout.closeDrawers();
                                return true;
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Search for a movie!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        adapterViewPager = new Adapter(getSupportFragmentManager());
        adapterViewPager.addFragment(new PopularMoviesFragment(), "Watch List");
        adapterViewPager.addFragment(new PopularMoviesFragment(), "w");
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
