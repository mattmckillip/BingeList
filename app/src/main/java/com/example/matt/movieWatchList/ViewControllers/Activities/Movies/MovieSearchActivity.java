package com.example.matt.movieWatchList.viewControllers.activities.movies;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.movies.MovieQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.movies.MovieResult;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.SearchMoviesAPI;
import com.example.matt.movieWatchList.viewControllers.activities.SettingsActivity;
import com.example.matt.movieWatchList.viewControllers.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Matt on 6/7/2016.
 */
public class MovieSearchActivity extends AppCompatActivity {
    private SearchAdapter searchAdapter;
    private List<MovieResult> searchMovieResults;

    @BindView(R.id.search_recycler_view)
    RecyclerView searchRecyclerView;

    @BindView(R.id.search_toolber)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        searchMovieResults = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchMovieResults, getApplicationContext());
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(castLayoutManager);
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchRecyclerView.setAdapter(searchAdapter);

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
                                Intent watchListIntent = new Intent(MovieSearchActivity.this, MovieWatchListActivity.class);
                                startActivity(watchListIntent);
                                return true;

                            case R.id.movie_browse_menu_item:
                                Intent browseIntent = new Intent(MovieSearchActivity.this, MovieBrowseActivity.class);
                                startActivity(browseIntent);
                                return true;

                            case R.id.movie_search_menu_item:
                                mDrawerLayout.closeDrawers();
                                return true;

                            case R.id.settings_menu_item:
                                Intent settingsIntent = new Intent(MovieSearchActivity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                                return true;
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


        /*TextView navHeaderText = (TextView) findViewById(R.id.nav_header_text);
        Typeface font = Typeface.
                createFromAsset(this.getAssets(), "fonts/Lobster-Regular.ttf");
        navHeaderText.setTypeface(font);*/
    }

    /*
     * Drawer Functions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SearchView", "onQueryTextSubmit");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://api.themoviedb.org/3/search/movie/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                SearchMoviesAPI service = retrofit.create(SearchMoviesAPI.class);

                Call<MovieQueryReturn> call = service.searchKeywords(query.replaceAll(" ", "+"));

                call.enqueue(new Callback<MovieQueryReturn>() {
                    @Override
                    public void onResponse(retrofit.Response<MovieQueryReturn> response, Retrofit retrofit) {
                        Log.d("getMovie()", "Callback Success");
                        List<MovieResult> movieResults = response.body().getMovieResults();

                        searchRecyclerView.setAdapter(new SearchAdapter(movieResults, getApplicationContext()));
                        searchRecyclerView.setFocusable(false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("getMovie()", "Callback Failure");
                    }
                });

                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
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
