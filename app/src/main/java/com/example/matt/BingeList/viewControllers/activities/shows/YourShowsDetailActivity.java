package com.example.matt.bingeList.viewControllers.activities.shows;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.Season;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.uitls.TVShowRealmStaticHelper;
import com.example.matt.bingeList.viewControllers.fragments.movies.MovieWatchListFragment;
import com.example.matt.bingeList.viewControllers.fragments.shows.TVShowEpisodeFragment;
import com.example.matt.bingeList.viewControllers.fragments.shows.TVShowOverviewFragment;
import com.example.matt.bingeList.viewControllers.fragments.shows.TVShowWatchlistSeasonFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class YourShowsDetailActivity extends AppCompatActivity {
    private Integer mShowID;
    private Adapter mAdapterViewPager;
    private SlidrInterface mSlidrInterface;
    private TVShow mShow;
    private Bitmap mBitmap;
    private ArrayList<IconicsDrawable> mFabIcons = new ArrayList<>();
    private Integer mSelectedTab;

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
    ImageView backdrop;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.loadingPanel)
    RelativeLayout loadingPanel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(PreferencesHelper.getTheme(getApplicationContext()) == ThemeEnum.NIGHT_THEME){
            setTheme(R.style.DarkAppTheme_Base);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvshow_activity_detail);
        mShowID = getIntent().getIntExtra(getApplicationContext().getString(R.string.showId), 0);
        String ShowName = getIntent().getStringExtra("showName");
        mSelectedTab = 0;

        mFabIcons.add(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_clear).sizeDp(16).color(Color.WHITE));
        mFabIcons.add(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_check).sizeDp(16).color(Color.WHITE));
        mFabIcons.add(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_done_all).sizeDp(16).color(Color.WHITE));

        final Realm mUiRealm = ((MyApplication) getApplication()).getUiRealm();
        RealmQuery<TVShow> query = mUiRealm.where(TVShow.class);
        mShow = query.equalTo("id", mShowID).findFirst();
        RealmQuery<Episode> episodeRealmQuery = mUiRealm.where(Episode.class);
        RealmResults<Episode> episodeRealmResults = episodeRealmQuery.equalTo("isWatched", false).equalTo("show_id", mShow.getId()).findAll();

        if (episodeRealmResults.size() == 0) {
            mFabIcons.set(1, new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_clear_all).sizeDp(16).color(Color.WHITE));
        }

        ButterKnife.bind(this);

        fab.setImageDrawable(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_clear).sizeDp(16).color(Color.WHITE));

        // Set title of Detail page
        collapsingToolbar.setTitle(ShowName);

        mSlidrInterface = Slidr.attach(this);

        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Next Episode"));
        tabLayout.addTab(tabLayout.newTab().setText("Seasons"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set title of Detail page
        collapsingToolbar.setTitle(mShow.getName());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        loadingPanel.setVisibility(View.GONE);

        if (mShow.getBackdropBitmap() != null) {
            mBitmap = BitmapFactory.decodeByteArray(mShow.getBackdropBitmap(), 0, mShow.getBackdropBitmap().length, options);
            backdrop.setImageBitmap(mBitmap);
        } else {
            mBitmap = null;
        }

        if (mBitmap != null) {
            // save image as byte array
            int defaultColor = 0x000000;
            Palette palette = Palette.from(mBitmap).generate();

            int vibrantColor = palette.getVibrantColor(defaultColor);
            int mutedColor = palette.getLightMutedColor(defaultColor);

            if (vibrantColor == 0) {
                vibrantColor = getResources().getColor(R.color.lightColorPrimary);
            }

            if (mutedColor == 0) {
                mutedColor = getResources().getColor(R.color.lightColorAccent);
            }

            collapsingToolbar.setBackgroundColor(vibrantColor);
            collapsingToolbar.setContentScrimColor(vibrantColor);
            collapsingToolbar.setStatusBarScrimColor(vibrantColor);
            tabLayout.setBackgroundColor(vibrantColor);
            fab.setBackgroundTintList(ColorStateList.valueOf(mutedColor));
            tabLayout.setSelectedTabIndicatorColor(mutedColor);

            // Setting ViewPager for each Tabs
            mAdapterViewPager = new Adapter(getSupportFragmentManager());

            Bundle overviewBundle = new Bundle();
            overviewBundle.putInt(getApplicationContext().getString(R.string.showId), mShowID);
            overviewBundle.putInt("vibrantColor", vibrantColor);
            overviewBundle.putInt("mutedColor", mutedColor);
            TVShowOverviewFragment overviewFragment = new TVShowOverviewFragment();
            overviewFragment.setArguments(overviewBundle);

            Bundle nextEpisode = new Bundle();
            nextEpisode.putInt(getApplicationContext().getString(R.string.showId), mShowID);
            nextEpisode.putInt("vibrantColor", vibrantColor);
            nextEpisode.putInt("mutedColor", mutedColor);
            TVShowEpisodeFragment nextEpisodeFragment = new TVShowEpisodeFragment();
            nextEpisodeFragment.setArguments(overviewBundle);

            Bundle seasonsBundle = new Bundle();
            seasonsBundle.putInt(getApplicationContext().getString(R.string.showId), mShowID);
            seasonsBundle.putInt("vibrantColor", vibrantColor);
            seasonsBundle.putInt("mutedColor", mutedColor);
            TVShowWatchlistSeasonFragment seasonsFragment = new TVShowWatchlistSeasonFragment();
            seasonsFragment.setArguments(seasonsBundle);

            mAdapterViewPager.addFragment(overviewFragment, "");
            mAdapterViewPager.addFragment(nextEpisodeFragment, "");
            mAdapterViewPager.addFragment(seasonsFragment, "");
            viewPager.setAdapter(mAdapterViewPager);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 1) {
                        TVShowEpisodeFragment tvShowEpisodeFragment = (TVShowEpisodeFragment) mAdapterViewPager.getItem(position);
                        tvShowEpisodeFragment.update();

                    } else if (position == 2) {
                        TVShowWatchlistSeasonFragment tvShowWatchlistSeasonFragment = (TVShowWatchlistSeasonFragment) mAdapterViewPager.getItem(position);
                        tvShowWatchlistSeasonFragment.updateAllGroupsAndChildren();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });


        } else {
            Log.d("Pallete", "Bitmap Null");
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                mSelectedTab = tab.getPosition();
                if (mSelectedTab == 0) {
                    mSlidrInterface.unlock();
                    animateFab(mSelectedTab, mFabIcons);
                } else {
                    animateFab(mSelectedTab, mFabIcons);
                    mSlidrInterface.lock();
                }
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
                if (mSelectedTab == 0) {
                    String showName = mShow.getName();

                    mUiRealm.beginTransaction();
                    TVShow TVShowResultsToRemove = mUiRealm.where(TVShow.class)
                            .equalTo("id", mShow.getId())
                            .findFirst();
                    TVShowResultsToRemove.deleteFromRealm();

                    RealmResults<Episode> EpisodeResultsToRemove = mUiRealm.where(Episode.class)
                            .equalTo("show_id", mShow.getId())
                            .findAll();
                    for (int i = 0; i < EpisodeResultsToRemove.size(); i++) {
                        EpisodeResultsToRemove.get(i).deleteFromRealm();
                    }

                    RealmResults<Season> SeasonResultsToRemove = mUiRealm.where(Season.class)
                            .equalTo("show_id", mShow.getId())
                            .findAll();
                    for (int i = 0; i < SeasonResultsToRemove.size(); i++) {
                        SeasonResultsToRemove.get(i).deleteFromRealm();
                    }

                    mUiRealm.commitTransaction();

                    Snackbar.make(v, showName + " removed from your shows", Snackbar.LENGTH_LONG).show();

                } else if (mSelectedTab == 1) {
                    Episode nextEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShow.getId(), mUiRealm);

                    if (nextEpisode != null) {
                        TVShowRealmStaticHelper.watchEpisode(nextEpisode, mUiRealm);
                        TVShowEpisodeFragment tvShowEpisodeFragment = (TVShowEpisodeFragment) mAdapterViewPager.getItem(mSelectedTab);
                        tvShowEpisodeFragment.update();
                    } else {
                        Snackbar.make(v, "Error watching this episode", Snackbar.LENGTH_LONG).show();
                    }


                } else if (mSelectedTab == 2) {
                    Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

                    RealmQuery<Episode> query = uiRealm.where(Episode.class);
                    RealmResults<Episode> episodeRealmResults = query.equalTo("isWatched", false).equalTo("show_id", mShow.getId()).findAll();

                    if (episodeRealmResults.size() > 0) { // unwatched episodes
                        uiRealm.beginTransaction();
                        RealmList<Season> seasons = mShow.getSeasons();
                        for (int i = 0; i < seasons.size(); i++) {
                            Season season = seasons.get(i);
                            RealmResults<Episode> episodes = uiRealm.where(Episode.class).equalTo("show_id", mShowID).findAll();
                            for (int j = 0; j < episodes.size(); j++) {
                                episodes.get(j).setIsWatched(true);
                            }
                        }
                        uiRealm.commitTransaction();

                        TVShowWatchlistSeasonFragment fragment = (TVShowWatchlistSeasonFragment) mAdapterViewPager.getRegisteredFragment(2);
                        fragment.updateAllGroupsAndChildren();

                        Snackbar.make(v, "All episodes marked watched!",
                                Snackbar.LENGTH_LONG).show();

                        mFabIcons.set(1, new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_clear_all).sizeDp(16).color(Color.WHITE));
                        animateFab(mSelectedTab, mFabIcons);

                    } else { //all episodes watched
                        uiRealm.beginTransaction();
                        RealmList<Season> seasons = mShow.getSeasons();
                        for (int i = 0; i < seasons.size(); i++) {
                            Season season = seasons.get(i);

                            RealmResults<Episode> episodes = uiRealm.where(Episode.class).equalTo("show_id", mShowID).findAll();
                            for (int j = 0; j < episodes.size(); j++) {
                                episodes.get(j).setIsWatched(false);
                            }
                        }
                        uiRealm.commitTransaction();

                        TVShowWatchlistSeasonFragment fragment = (TVShowWatchlistSeasonFragment) mAdapterViewPager.getRegisteredFragment(2);
                        fragment.updateAllGroupsAndChildren();

                        Snackbar.make(v, "All episodes marked unwatched!",
                                Snackbar.LENGTH_LONG).show();

                        mFabIcons.set(1, new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_done_all).sizeDp(16).color(Color.WHITE));
                        animateFab(mSelectedTab, mFabIcons);
                    }
                }
            }
        });
    }



    protected void animateFab(final int position, final ArrayList<IconicsDrawable> drawables) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.setImageDrawable(drawables.get(position));

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
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

        public Fragment getRegisteredFragment(int position) {
            return mFragmentList.get(position);
        }
    }
}
