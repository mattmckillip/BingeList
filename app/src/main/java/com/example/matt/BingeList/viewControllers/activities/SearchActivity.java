package com.example.matt.bingeList.viewControllers.activities;

import android.graphics.Color;
import android.os.Bundle;
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

import com.example.matt.bingeList.models.MultiSearchQueryReturn;
import com.example.matt.bingeList.models.MultiSearchResult;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MultiSearchAPI;
import com.example.matt.bingeList.uitls.DrawerHelper;
import com.example.matt.bingeList.viewControllers.adapters.MultiSearchAdapter;
import com.example.matt.bingeList.viewControllers.adapters.SearchAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchActivity extends AppCompatActivity {
    private Drawer mNavigationDrawer;
    private SearchAdapter mSearchAdapter;
    private List<MovieResult> mSearchMovieResults;

    @BindView(R.id.search_recycler_view)
    RecyclerView searchRecyclerView;

    @BindView(R.id.search_toolber)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        ButterKnife.bind(this);

        mSearchMovieResults = new ArrayList<>();
        mSearchAdapter = new SearchAdapter(mSearchMovieResults, getApplicationContext());
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(castLayoutManager);
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchRecyclerView.setAdapter(mSearchAdapter);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_more_vert).color(Color.WHITE).sizeDp(24));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Create Navigation drawer
        mNavigationDrawer = new DrawerHelper().GetDrawer(this, toolbar, savedInstanceState);

    }

    /*
     * Drawer Functions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        myActionMenuItem.setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_search).sizeDp(16).color(Color.WHITE));

        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SearchView", "onQueryTextSubmit");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://api.themoviedb.org/3/search/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MultiSearchAPI service = retrofit.create(MultiSearchAPI.class);

                Call<MultiSearchQueryReturn> call = service.searchKeywords(query.replaceAll(" ", "+"));

                call.enqueue(new Callback<MultiSearchQueryReturn>() {
                    @Override
                    public void onResponse(Call<MultiSearchQueryReturn> call, Response<MultiSearchQueryReturn> response) {
                        Log.d("getMovie()", response.raw().toString());
                        List<MultiSearchResult> multiSearchResults = response.body().getResults();
                        Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

                        searchRecyclerView.setAdapter(new MultiSearchAdapter(multiSearchResults, getApplicationContext(), uiRealm));
                        searchRecyclerView.setFocusable(false);
                    }

                    @Override
                    public void onFailure(Call<MultiSearchQueryReturn> call, Throwable t) {
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
            mNavigationDrawer.openDrawer();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.mNavigationDrawer.isDrawerOpen()) {
            this.mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}