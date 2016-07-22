package com.example.matt.bingeList.viewControllers.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.Cast;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.adapters.CastAdapter;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CastActivity extends AppCompatActivity {
    private static final String TAG = CastActivity.class.getName();
    private static final Integer NUMBER_OF_CREW_TO_DISPLAY = 25;

    private CastAdapter mAdapter;
    private Integer mId;
    private String mTitle;
    private RealmList<Cast> mCastList;
    private Context mContext;
    private Credits mCredits;
    private boolean isMovie;

    @BindView(R.id.recycler_view)
    RecyclerView mCastRecyclerView;

    @BindView(R.id.toolber)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(PreferencesHelper.getTheme(getApplicationContext()) == ThemeEnum.NIGHT_THEME){
            setTheme(R.style.DarkAppTheme_Base);
        }

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

        mContext = getApplicationContext();

        if (extras != null) {
            mId = extras.getInt("id");
            mTitle = extras.getString("title");
            isMovie = extras.getBoolean("isMovie");
        }

        toolbar.setTitle(mTitle + " Cast");

        mCastList = new RealmList<>();
        mAdapter = new CastAdapter(mCastList, mContext, NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        mCastRecyclerView.setLayoutManager(castLayoutManager);
        mCastRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCastRecyclerView.setAdapter(mAdapter);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadCredits();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }

    private void loadCredits() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadCredits()");
        }

        if (isMovie) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/movie/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieAPI service = retrofit.create(MovieAPI.class);

            Call<Credits> call = service.getCredits(Integer.toString(mId));
            call.enqueue(new Callback<Credits>() {
                @Override
                public void onResponse(Call<Credits> call, Response<Credits> response) {
                    if (response.isSuccessful()) {
                        mCredits = response.body();
                        mCastList = mCredits.getCast();

                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Credits - " + mCredits.toString());
                            Log.d(TAG, "PersonCast - " + mCastList.toString());
                        }

                        Integer castSize = Math.min(NUMBER_OF_CREW_TO_DISPLAY, mCastList.size());

                        // Populate cast and crew recycler views
                        mCastRecyclerView.setAdapter(new CastAdapter(mCastList, mContext, castSize));
                        mCastRecyclerView.setFocusable(false);
                    } else {
                        Snackbar.make(mCastRecyclerView, "Error fetching cast", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d(TAG, "Reloading credits");
                                        loadCredits();
                                    }
                                })
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<Credits> call, Throwable t) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "GetCredits() Callback Failure");

                        Snackbar.make(mCastRecyclerView, "Error fetching cast", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d(TAG, "Reloading credits");
                                        loadCredits();
                                    }
                                })
                                .show();
                    }
                }
            });
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/tv/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TVShowAPI service = retrofit.create(TVShowAPI.class);

            Call<Credits> call = service.getCredits(Integer.toString(mId));
            call.enqueue(new Callback<Credits>() {
                @Override
                public void onResponse(Call<Credits> call, Response<Credits> response) {
                    if (response.isSuccessful()) {
                        mCredits = response.body();
                        mCastList = mCredits.getCast();

                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Credits - " + mCredits.toString());
                            Log.d(TAG, "PersonCast - " + mCastList.toString());
                        }

                        Integer castSize = Math.min(NUMBER_OF_CREW_TO_DISPLAY, mCastList.size());

                        // Populate cast and crew recycler views
                        mCastRecyclerView.setAdapter(new CastAdapter(mCastList, mContext, castSize));
                        mCastRecyclerView.setFocusable(false);
                    } else {
                        Snackbar.make(mCastRecyclerView, "Error fetching cast", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d(TAG, "Reloading credits");
                                        loadCredits();
                                    }
                                })
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<Credits> call, Throwable t) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "GetCredits() Callback Failure");

                        Snackbar.make(mCastRecyclerView, "Error fetching cast", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d(TAG, "Reloading credits");
                                        loadCredits();
                                    }
                                })
                                .show();
                    }
                }
            });
        }
    }
}