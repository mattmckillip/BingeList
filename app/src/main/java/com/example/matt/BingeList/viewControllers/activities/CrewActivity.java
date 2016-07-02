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
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.Crew;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.adapters.CrewAdapter;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CrewActivity extends AppCompatActivity {
    private static final String TAG = CrewActivity.class.getName();
    private static final Integer NUMBER_OF_CREW_TO_DISPLAY = 25;

    private CrewAdapter mAdapter;
    private Integer mId;
    private String mTitle;
    private RealmList<Crew> mCrewList;
    private Context mContext;
    private Credits mCredits;

    @BindView(R.id.recycler_view)
    RecyclerView mCrewRecyclerView;

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

        mContext = getApplicationContext();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mId = extras.getInt(mContext.getString(R.string.movieId));
            mTitle = extras.getString(mContext.getString(R.string.movieTitle));
        }

        toolbar.setTitle(mTitle + " Crew");

        mCrewList = new RealmList<>();
        mAdapter = new CrewAdapter(mCrewList, mContext, NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        mCrewRecyclerView.setLayoutManager(castLayoutManager);
        mCrewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCrewRecyclerView.setAdapter(mAdapter);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            //supportActionBar.setHomeAsUpIndicator(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_b).color(Color.WHITE).sizeDp(24));
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.movie_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        Call<Credits> call = service.getCredits(Integer.toString(mId));
        call.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(Call<Credits> call, Response<Credits> response) {
                if (response.isSuccessful()) {
                    mCredits = response.body();
                    mCrewList = mCredits.getCrew();

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Credits - " + mCredits.toString());
                        Log.d(TAG, "PersonCast - " + mCrewList.toString());
                    }

                    Integer crewSize = Math.min(NUMBER_OF_CREW_TO_DISPLAY, mCrewList.size());

                    // Populate cast and crew recycler views
                    mCrewRecyclerView.setAdapter(new CrewAdapter(mCrewList, mContext, crewSize));
                    mCrewRecyclerView.setFocusable(false);
                } else {
                    Snackbar.make(mCrewRecyclerView, "Error fetching crew", Snackbar.LENGTH_INDEFINITE)
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

                    Snackbar.make(mCrewRecyclerView, "Error fetching cast", Snackbar.LENGTH_INDEFINITE)
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