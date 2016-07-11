package com.example.matt.bingeList.viewControllers.activities.shows;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.Season;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.models.shows.TVShowSeasonResult;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.PaletteTransformation;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.fragments.shows.TVEpisodeFragment;
import com.example.matt.bingeList.viewControllers.fragments.shows.TVShowBrowseSeasonFragment;
import com.example.matt.bingeList.viewControllers.fragments.shows.TVShowOverviewFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TVShowBrowseDetailActivity extends AppCompatActivity {
    private static final String TAG = TVShowBrowseDetailActivity.class.getName();
    private Adapter mAdapterViewPager;
    private Integer mShowId;
    private int mVibrantColor;
    private int mMutedColor;
    private TVShow show;
    private SlidrInterface mSlidrInterface;
    private Realm mUiRealm;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());
        if(PreferencesHelper.getTheme(getApplicationContext()) == ThemeEnum.NIGHT_THEME){
            setTheme(R.style.DarkAppTheme_Base);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvshow_activity_detail);
        mShowId = getIntent().getIntExtra("showID", 0);
        String ShowName = getIntent().getStringExtra("showName");

        ButterKnife.bind(this);

        mUiRealm = ((MyApplication) getApplication()).getUiRealm();

        fab.setImageDrawable(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_add).sizeDp(16).color(Color.WHITE));

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

        Call<TVShow> call = service.getTVShow(Integer.toString(mShowId));
        call.enqueue(new Callback<TVShow>() {
            @Override
            public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                background.setVisibility(View.VISIBLE);
                collapsingToolbar.setVisibility(View.VISIBLE);

                show = response.body();

                Picasso.with(getApplicationContext())
                        .load("https://image.tmdb.org/t/p/w500/" + response.body().getBackdropPath())
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
                                mVibrantColor = palette.getVibrantColor(defaultColor);
                                mMutedColor = palette.getLightMutedColor(defaultColor);
                                if (mVibrantColor == 0) {
                                    mVibrantColor = getResources().getColor(R.color.lightColorPrimary);
                                }

                                if (mMutedColor == 0) {
                                    mMutedColor = getResources().getColor(R.color.lightColorAccent);
                                }

                                appbar.setBackgroundColor(mVibrantColor);
                                collapsingToolbar.setBackgroundColor(mVibrantColor);
                                collapsingToolbar.setContentScrimColor(mVibrantColor);
                                collapsingToolbar.setStatusBarScrimColor(mVibrantColor);
                                tabLayout.setBackgroundColor(mVibrantColor);
                                fab.setBackgroundTintList(ColorStateList.valueOf(mMutedColor));
                                tabLayout.setSelectedTabIndicatorColor(mMutedColor);

                                // Setting ViewPager for each Tabs
                                mAdapterViewPager = new Adapter(getSupportFragmentManager());

                                Bundle overviewBundle = new Bundle();
                                Log.d(TAG, Integer.toString(mShowId));
                                overviewBundle.putInt("showID", mShowId);
                                overviewBundle.putInt("vibrantColor", mVibrantColor);
                                overviewBundle.putInt("mutedColor", mMutedColor);
                                TVShowOverviewFragment overviewFragment = new TVShowOverviewFragment();
                                overviewFragment.setArguments(overviewBundle);

                                Bundle seasonsBundle = new Bundle();
                                seasonsBundle.putInt("showID", mShowId);
                                seasonsBundle.putInt("vibrantColor", mVibrantColor);
                                seasonsBundle.putInt("mutedColor", mMutedColor);
                                TVShowBrowseSeasonFragment seasonsFragment = new TVShowBrowseSeasonFragment();
                                seasonsFragment.setArguments(seasonsBundle);

                                mAdapterViewPager.addFragment(overviewFragment, "");
                                mAdapterViewPager.addFragment(seasonsFragment, "");
                                viewPager.setAdapter(mAdapterViewPager);

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
            public void onFailure(Call<TVShow> call, Throwable t) {
                Log.d("getMovie()", "Callback Failure");
            }
        });

        // Attach the Slidr Mechanism to this activity
        mSlidrInterface = Slidr.attach(this);

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
                if (tab.getPosition() == 0) mSlidrInterface.unlock();
                else mSlidrInterface.lock();
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
                show.setOnYourShows(true);
                uiRealm.copyToRealmOrUpdate(show);
                uiRealm.commitTransaction();
                Log.d("realm transaction","success");
                FetchSeasonsTask fetchSeasonsTask = new FetchSeasonsTask();
                fetchSeasonsTask.execute(mShowId, show.getNumberOfSeasons());

                Snackbar.make(v, "Added to your shows!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private void addByteArray(byte[] image) {
        show.setBackdropBitmap(image);
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
        Log.d("realm transaction","attempting to add");

        for (TVShowSeasonResult season: seasons) {
            Season curSeason = new Season();
            curSeason.setAirDate(season.getAirDate());
            curSeason.setEpisodeCount(season.getEpisodes().size());
            curSeason.setId(season.getId());
            curSeason.setPosterPath(season.getPosterPath());
            curSeason.setShow_id(mShowId);
            curSeason.setSeasonNumber(season.getSeasonNumber());

            mUiRealm.beginTransaction();
            mUiRealm.copyToRealmOrUpdate(curSeason);
            mUiRealm.commitTransaction();

            RealmList<Episode> jsonEpisodeRealmList = season.getEpisodes();
            for (Episode episode: jsonEpisodeRealmList) {
                mUiRealm.beginTransaction();
                episode.setShow_id(mShowId);
                episode.setIsWatched(false);
                Log.d(TAG, "Current season number: " + curSeason.getSeasonNumber());
                episode.setSeasonNumber(curSeason.getSeasonNumber());
                mUiRealm.copyToRealmOrUpdate(episode);
                mUiRealm.commitTransaction();
            }

            Log.d(TAG, "Number of episodes in show: " + mUiRealm.where(Episode.class).equalTo("show_id", mShowId).count());
            Log.d(TAG, "Number of episodes in Season 1: " + mUiRealm.where(Episode.class).equalTo("show_id", mShowId).equalTo("seasonNumber", 1).findAll().size());
        }
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
