package com.example.matt.bingeList.viewControllers.activities.movies;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.movies.MovieQueryReturn;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.viewControllers.adapters.BrowseMoviesAdapter;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Matt on 6/7/2016.
 */
public class SimilarMoviesActivity extends AppCompatActivity {
    private static final String TAG = SimilarMoviesActivity.class.getName();
    private BrowseMoviesAdapter mAdapter;
    private Integer mMovieId;
    private Realm mUiRealm;
    private RealmList<Movie> data;
    private Context mContext;
    private int mVibrantColor;


    @BindView(R.id.recycler_view)
    RecyclerView searchRecyclerView;

    @BindView(R.id.toolber)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());
        Iconics.registerFont(new CommunityMaterial());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_with_appbar);
        ButterKnife.bind(this);

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true)
                .build();

        Slidr.attach(this, config);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMovieId = extras.getInt(mContext.getString(R.string.movieId));
            mVibrantColor = extras.getInt("vibrantColor");

            //The key argument here must match that used in the other activity
        }
        mUiRealm = ((MyApplication) getApplication()).getUiRealm();
        mContext = getApplicationContext();

        Movie movie = mUiRealm.where(Movie.class).equalTo("id", mMovieId).findFirst();
        if (movie == null){
            if (BuildConfig.DEBUG){
                Log.d(TAG, "Movie is null");
            }
            Snackbar.make(getCurrentFocus(), "Bad data", Snackbar.LENGTH_INDEFINITE);
            return;
        }

        toolbar.setTitle("Similar to " + movie.getTitle());
        toolbar.setBackgroundColor(mVibrantColor);
        mAppBar.setBackgroundColor(mVibrantColor);

        data = new RealmList<>();
        mAdapter = new BrowseMoviesAdapter(data, mContext, mUiRealm);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(castLayoutManager);
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchRecyclerView.setAdapter(mAdapter);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            //supportActionBar.setHomeAsUpIndicator(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_b).color(Color.WHITE).sizeDp(24));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadSimilarMovies();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }

    private void loadSimilarMovies() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadSimilarMovies()");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.movie_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        final Call<MovieQueryReturn> similarMoviesCall = service.getSimilarMovies(Integer.toString(mMovieId));

        similarMoviesCall.enqueue(new Callback<MovieQueryReturn>() {
            @Override
            public void onResponse(Call<MovieQueryReturn> call, Response<MovieQueryReturn> response) {
                if (response.isSuccessful()) {
                    List<MovieResult> movieResults = response.body().getMovieResults();
                    data = new RealmList<>();
                    for (MovieResult movieResult : movieResults) {
                        Movie movie = new Movie();
                        movie.setTitle(movieResult.getTitle());
                        movie.setId(movieResult.getId());
                        movie.setOverview(movieResult.getOverview());
                        movie.setBackdropPath(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + movieResult.getBackdropPath());
                        data.add(movie);
                    }
                    // Populate cast and crew recycler views
                    searchRecyclerView.setAdapter(new BrowseMoviesAdapter(data, mContext, mUiRealm));
                    searchRecyclerView.setFocusable(false);
                }
            }

            @Override
            public void onFailure(Call<MovieQueryReturn> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d("getSimilarMovies()", "No Response");
                }
            }
        });
    }
}
