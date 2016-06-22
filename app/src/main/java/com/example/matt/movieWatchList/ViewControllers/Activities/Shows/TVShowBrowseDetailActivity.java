package com.example.matt.movieWatchList.viewControllers.activities.shows;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.matt.movieWatchList.Models.POJO.shows.TVShow;
import com.example.matt.movieWatchList.Models.POJO.shows.TVShowSeasonResult;
import com.example.matt.movieWatchList.Models.Realm.JSONEpisode;
import com.example.matt.movieWatchList.Models.Realm.JSONSeason;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.TVShowAPI;
import com.example.matt.movieWatchList.uitls.PaletteTransformation;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowBrowseSeasonFragment;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowOverviewFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class TVShowBrowseDetailActivity extends AppCompatActivity {
    Adapter adapterViewPager;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.background)
    ImageView background;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.loadingPanel)
    RelativeLayout loadingPanel;

    private Integer showID;
    private int vibrantColor;
    private int mutedColor;
    private JSONShow realmShow;
    private SlidrInterface slidrInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvshow_activity_detail);
        showID = getIntent().getIntExtra("showID", 0);
        String ShowName = getIntent().getStringExtra("showName");

        Log.d("SHOWID", Integer.toString(showID));

        ButterKnife.bind(this);

        appbar.setVisibility(View.GONE);
        collapsingToolbar.setVisibility(View.GONE);
        background.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        loadingPanel.setVisibility(View.VISIBLE);

        // Set title of Detail page
        collapsingToolbar.setTitle(ShowName);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/tv/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final TVShowAPI service = retrofit.create(TVShowAPI.class);

        Call<TVShow> call = service.getTVShow(Integer.toString(showID));
        call.enqueue(new Callback<TVShow>() {
            @Override
            public void onResponse(retrofit.Response<TVShow> response, Retrofit retrofit) {
                background.setVisibility(View.VISIBLE);
                collapsingToolbar.setVisibility(View.VISIBLE);
                Log.d("Resonse", response.raw().toString());

                realmShow = response.body().convertToRealm();

                Picasso.with(getApplicationContext())
                        .load("https://image.tmdb.org/t/p/w500//" + response.body().getBackdropPath())
                        .fit().centerCrop()
                        .transform(PaletteTransformation.instance())
                        .into(background, new PaletteTransformation.PaletteCallback(background) {
                            @Override
                            public void onSuccess(Palette palette) {
                                Bitmap bitmap = ((BitmapDrawable) background.getDrawable()).getBitmap(); // Ew!
                                appbar.setVisibility(View.VISIBLE);
                                tabLayout.setVisibility(View.VISIBLE);
                                fab.setVisibility(View.VISIBLE);
                                loadingPanel.setVisibility(View.GONE);

                                int defaultColor = 0x000000;
                                vibrantColor = palette.getVibrantColor(defaultColor);
                                mutedColor = palette.getLightMutedColor(defaultColor);
                                if (vibrantColor == 0) {
                                    vibrantColor = getResources().getColor(R.color.colorPrimary);
                                }

                                if (mutedColor == 0) {
                                    mutedColor = getResources().getColor(R.color.colorAccent);
                                }

                                appbar.setBackgroundColor(vibrantColor);
                                collapsingToolbar.setBackgroundColor(vibrantColor);
                                collapsingToolbar.setContentScrimColor(vibrantColor);
                                collapsingToolbar.setStatusBarScrimColor(vibrantColor);
                                tabLayout.setBackgroundColor(vibrantColor);
                                fab.setBackgroundTintList(ColorStateList.valueOf(mutedColor));
                                tabLayout.setSelectedTabIndicatorColor(mutedColor);


                                // Setting ViewPager for each Tabs
                                adapterViewPager = new Adapter(getSupportFragmentManager());

                                Bundle overviewBundle = new Bundle();
                                overviewBundle.putInt("showID", showID);
                                overviewBundle.putInt("vibrantColor", vibrantColor);
                                overviewBundle.putInt("mutedColor", mutedColor);
                                TVShowOverviewFragment overviewFragment = new TVShowOverviewFragment();
                                overviewFragment.setArguments(overviewBundle);

                                Bundle seasonsBundle = new Bundle();
                                seasonsBundle.putInt("showID", showID);
                                seasonsBundle.putInt("vibrantColor", vibrantColor);
                                seasonsBundle.putInt("mutedColor", mutedColor);
                                TVShowBrowseSeasonFragment seasonsFragment = new TVShowBrowseSeasonFragment();
                                seasonsFragment.setArguments(seasonsBundle);

                                adapterViewPager.addFragment(overviewFragment, "");
                                adapterViewPager.addFragment(seasonsFragment, "");
                                viewPager.setAdapter(adapterViewPager);

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                addByteArray(stream.toByteArray());
                            }

                            @Override
                            public void onError() {
                                //TODO
                            }
                        });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("getMovie()", "Callback Failure");
            }
        });

        // Attach the Slidr Mechanism to this activity
        slidrInterface = Slidr.attach(this);

        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Seasons"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("Tab Selected", Integer.toString(tab.getPosition()));
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) slidrInterface.unlock();
                else slidrInterface.lock();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
                uiRealm.beginTransaction();
                realmShow.setOnWatchList(true);
                //JSONMovie movieToAdd = uiRealm.createObject(movie);
                uiRealm.copyToRealm(realmShow);
                uiRealm.commitTransaction();
                Log.d("realm transaction","success");
                FetchSeasonsTask fetchSeasonsTask = new FetchSeasonsTask();
                fetchSeasonsTask.execute(showID, realmShow.getNumberOfSeasons());

                Snackbar.make(v, "Added to your shows!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void addByteArray(byte[] image) {
        realmShow.setBackdropBitmap(image);
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

    public void UpdateRealmSeasons(ArrayList<TVShowSeasonResult> seasons) {
        //add to realm
        Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
        Log.d("realm transaction","attempting to add");

        RealmList<JSONSeason> jsonSeasonRealmList = new RealmList<>();
        for (TVShowSeasonResult season: seasons) {
            JSONSeason realmSeason = season.convertToRealm();
            jsonSeasonRealmList.add(realmSeason);

            RealmList<JSONEpisode> jsonEpisodeRealmList = realmSeason.getEpisodes();
            for (JSONEpisode episode: jsonEpisodeRealmList) {
                episode.setShow_id(showID);
            }
        }

        uiRealm.beginTransaction();
        realmShow.setSeasons(jsonSeasonRealmList);
        uiRealm.copyToRealmOrUpdate(realmShow);
        uiRealm.commitTransaction();
    }

    private class FetchSeasonsTask extends AsyncTask<Integer, Integer, ArrayList<TVShowSeasonResult>> {
        protected ArrayList<TVShowSeasonResult> doInBackground(Integer... params) {
            Integer showID = params[0];
            Integer numberOfSeasons = params[1];

            ExecutorService backgroundExecutor = Executors.newFixedThreadPool(numberOfSeasons);

            ArrayList<TVShowSeasonResult> seasons = new ArrayList<>();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/tv/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .callbackExecutor(backgroundExecutor)
                    .build();

            final TVShowAPI service = retrofit.create(TVShowAPI.class);

            for (int i = 1; i <= numberOfSeasons; i++) {
                Call<TVShowSeasonResult> call = service.getSeasons(Integer.toString(showID), Integer.toString(i));
                try {
                    seasons.add(call.execute().body());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return seasons;
        }

        protected void onPostExecute(ArrayList<TVShowSeasonResult> result) {
            UpdateRealmSeasons(result);
        }
    }
}
