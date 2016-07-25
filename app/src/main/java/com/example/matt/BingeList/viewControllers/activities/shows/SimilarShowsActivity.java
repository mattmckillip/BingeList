package com.example.matt.bingeList.viewControllers.activities.shows;

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
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.models.shows.TVShowQueryReturn;
import com.example.matt.bingeList.models.shows.TVShowResult;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.viewControllers.adapters.shows.BrowseTVShowsAdapter;
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
public class SimilarShowsActivity extends AppCompatActivity {
    private static final String TAG = SimilarShowsActivity.class.getName();
    private BrowseTVShowsAdapter mAdapter;
    private Integer mShowId;
    private Realm mUiRealm;
    private RealmList<TVShow> data;
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
        mContext = getApplicationContext();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mShowId = extras.getInt(mContext.getString(R.string.showId));
            mVibrantColor = extras.getInt("vibrantColor");

            //The key argument here must match that used in the other activity
        }
        mUiRealm = ((MyApplication) getApplication()).getUiRealm();

        TVShow show = mUiRealm.where(TVShow.class).equalTo("id", mShowId).findFirst();
        if (show == null){
            if (BuildConfig.DEBUG){
                Log.d(TAG, "Show is null");
            }
            Snackbar.make(getCurrentFocus(), "Bad data", Snackbar.LENGTH_INDEFINITE);
            return;
        }

        toolbar.setTitle("Similar to " + show.getName());
        toolbar.setBackgroundColor(mVibrantColor);
        mAppBar.setBackgroundColor(mVibrantColor);

        data = new RealmList<>();
        mAdapter = new BrowseTVShowsAdapter(data, mContext, mUiRealm);
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

        loadSimilarShows();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }

    private void loadSimilarShows() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadSimilarShows()");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.tv_show_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TVShowAPI service = retrofit.create(TVShowAPI.class);

        final Call<TVShowQueryReturn> similarMoviesCall = service.getSimilarShows(Integer.toString(mShowId));

        similarMoviesCall.enqueue(new Callback<TVShowQueryReturn>() {
            @Override
            public void onResponse(Call<TVShowQueryReturn> call, Response<TVShowQueryReturn> response) {
                if (response.isSuccessful()) {
                    List<TVShowResult> showResults = response.body().getResults();
                    data = new RealmList<>();
                    for (TVShowResult showResult : showResults) {
                        TVShow tvShow = new TVShow();
                        tvShow.setName(showResult.getName());
                        tvShow.setId(showResult.getId());
                        tvShow.setOverview(showResult.getOverview());
                        tvShow.setBackdropPath(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + showResult.getBackdropPath());
                        data.add(tvShow);
                    }
                    // Populate cast and crew recycler views
                    searchRecyclerView.setAdapter(new BrowseTVShowsAdapter(data, mContext, mUiRealm));
                    searchRecyclerView.setFocusable(false);
                }
            }

            @Override
            public void onFailure(Call<TVShowQueryReturn> call, Throwable t) {
                Snackbar.make(getCurrentFocus(), "Unable to connect to API", Snackbar.LENGTH_SHORT);
            }
        });
    }
}
