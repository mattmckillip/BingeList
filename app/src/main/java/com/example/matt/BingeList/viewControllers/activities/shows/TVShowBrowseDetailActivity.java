package com.example.matt.bingeList.viewControllers.activities.shows;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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

import com.example.matt.bingeList.BuildConfig;
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
import com.example.matt.bingeList.viewControllers.fragments.movies.MovieWatchListFragment;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    private String mShowName;
    private Context mContext;
    private int mViewPagerPosition;
    private int mNetflixId;


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
        setIcons();
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvshow_activity_detail);
        mContext = getApplicationContext();

        getIntentExtras();
        ButterKnife.bind(this);

        mUiRealm = ((MyApplication) getApplication()).getUiRealm();
        fab.setImageDrawable(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_add).sizeDp(16).color(Color.WHITE));
        hideViews();

        // Set title of Detail page
        collapsingToolbar.setTitle(mShowName);
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

                if (response.isSuccessful()) {
                    show = response.body();

                    Picasso.with(getApplicationContext())
                            .load(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + show.getBackdropPath())
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

                                    setColors(mVibrantColor, mMutedColor);


                                    // Setting ViewPager for each Tabs
                                    mAdapterViewPager = new Adapter(getSupportFragmentManager());

                                    Bundle overviewBundle = new Bundle();
                                    overviewBundle.putInt(getApplicationContext().getString(R.string.showId), mShowId);
                                    overviewBundle.putInt("vibrantColor", mVibrantColor);
                                    overviewBundle.putInt("mutedColor", mMutedColor);
                                    TVShowOverviewFragment overviewFragment = new TVShowOverviewFragment();
                                    overviewFragment.setArguments(overviewBundle);

                                    Bundle seasonsBundle = new Bundle();
                                    seasonsBundle.putInt(getApplicationContext().getString(R.string.showId), mShowId);
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
                else {}
            }

            @Override
            public void onFailure(Call<TVShow> call, Throwable t) {}
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
        collapsingToolbar.setTitle(mShowName);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
                uiRealm.beginTransaction();
                show.setOnYourShows(true);
                show.setDate(new Date());
                uiRealm.copyToRealmOrUpdate(show);
                uiRealm.commitTransaction();
                FetchSeasonsTask fetchSeasonsTask = new FetchSeasonsTask();
                fetchSeasonsTask.execute(mShowId, show.getNumberOfSeasons());

                Snackbar.make(v, "Added to your shows!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }
    // HELPERS

    private void setColors(int mVibrantColor, int mMutedColor) {
        appbar.setBackgroundColor(mVibrantColor);
        collapsingToolbar.setBackgroundColor(mVibrantColor);
        collapsingToolbar.setContentScrimColor(mVibrantColor);
        collapsingToolbar.setStatusBarScrimColor(mVibrantColor);
        tabLayout.setBackgroundColor(mVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(mMutedColor));
        tabLayout.setSelectedTabIndicatorColor(mMutedColor);
    }

    private void getIntentExtras() {
        mShowId = getIntent().getIntExtra(mContext.getString(R.string.showId), 0);
        mShowName = getIntent().getStringExtra(mContext.getString(R.string.showTitle));
    }

    private void hideViews() {
        appbar.setVisibility(View.GONE);
        collapsingToolbar.setVisibility(View.GONE);
        background.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void setIcons() {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());
    }

    private void setTheme() {
        if(PreferencesHelper.getTheme(getApplicationContext()) == ThemeEnum.NIGHT_THEME){
            setTheme(R.style.DarkAppTheme_Base);
        }
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

        for (TVShowSeasonResult season: seasons) {
            if (season != null) {
                Season curSeason = new Season();

                curSeason.setAirDate(season.getAirDate());
                curSeason.setEpisodeCount(season.getEpisodes().size());
                curSeason.setId(season.getId());
                curSeason.setPosterPath(season.getPosterPath());
                curSeason.setShow_id(mShowId);
                curSeason.setSeasonNumber(season.getSeasonNumber());

                mUiRealm.beginTransaction();
                mUiRealm.copyToRealmOrUpdate(curSeason);

                RealmList<Episode> jsonEpisodeRealmList = season.getEpisodes();
                for (Episode episode : jsonEpisodeRealmList) {
                    episode.setShow_id(mShowId);
                    episode.setIsWatched(false);
                    episode.setSeasonNumber(curSeason.getSeasonNumber());
                    mUiRealm.copyToRealmOrUpdate(episode);
                }
                mUiRealm.commitTransaction();
            }
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
