package com.example.matt.movieWatchList.viewControllers.activities;

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

import com.example.matt.movieWatchList.Models.POJO.MultiSearchQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.MultiSearchResult;
import com.example.matt.movieWatchList.Models.POJO.movies.MovieQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.movies.MovieResult;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.MultiSearchAPI;
import com.example.matt.movieWatchList.uitls.API.SearchMoviesAPI;
import com.example.matt.movieWatchList.uitls.DrawerHelper;
import com.example.matt.movieWatchList.viewControllers.activities.movies.BrowseMoviesActivity;
import com.example.matt.movieWatchList.viewControllers.activities.movies.MovieWatchListActivity;
import com.example.matt.movieWatchList.viewControllers.adapters.MultiSearchAdapter;
import com.example.matt.movieWatchList.viewControllers.adapters.SearchAdapter;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Matt on 6/15/2016.
 */
public class SearchActivity extends AppCompatActivity {
    private SearchAdapter searchAdapter;
    private List<MovieResult> searchMovieResults;

    @BindView(R.id.search_recycler_view)
    RecyclerView searchRecyclerView;

    @BindView(R.id.search_toolber)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    Drawer navigationDrawer;


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
        getSupportActionBar().setTitle("Search");

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Create Navigation drawer
        navigationDrawer = new DrawerHelper().GetDrawer(this, toolbar, savedInstanceState);

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
                        .baseUrl("http://api.themoviedb.org/3/search/multi")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MultiSearchAPI service = retrofit.create(MultiSearchAPI.class);

                Call<MultiSearchQueryReturn> call = service.searchKeywords(query.replaceAll(" ", "+"));

                call.enqueue(new Callback<MultiSearchQueryReturn>() {
                    @Override
                    public void onResponse(retrofit.Response<MultiSearchQueryReturn> response, Retrofit retrofit) {
                        Log.d("getMovie()", response.raw().toString());
                        List<MultiSearchResult> multiSearchResults = response.body().getResults();

                        searchRecyclerView.setAdapter(new MultiSearchAdapter(multiSearchResults, getApplicationContext()));
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
