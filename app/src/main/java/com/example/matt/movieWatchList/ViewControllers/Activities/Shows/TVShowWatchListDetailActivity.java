package com.example.matt.movieWatchList.viewControllers.activities.shows;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.shows.TVShow;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONEpisode;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.Models.Realm.JSONSeason;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.TVShowAPI;
import com.example.matt.movieWatchList.uitls.PaletteTransformation;
import com.example.matt.movieWatchList.viewControllers.adapters.CastAdapter;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowBrowseSeasonFragment;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowOverviewFragment;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowWatchlistSeasonFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class TVShowWatchListDetailActivity extends AppCompatActivity {
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    Integer showID;
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
    ImageView backdrop;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.loadingPanel)
    RelativeLayout loadingPanel;

    private RealmList<JSONCast> castList = new RealmList<>();
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;
    private RealmList<JSONCast> crewList = new RealmList<>();
    private RecyclerView crewRecyclerView;
    private CastAdapter crewAdapter;
    private DrawerLayout mDrawerLayout;
    private SlidrInterface slidrInterface;
    private JSONShow show;
    private Bitmap thisBitmap;
    ArrayList<IconicsDrawable> mFabIcons = new ArrayList<>();
    private Integer mSelectedTab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvshow_activity_detail);
        showID = getIntent().getIntExtra("showID", 0);
        String ShowName = getIntent().getStringExtra("showName");
        mSelectedTab = 0;

        mFabIcons.add(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_remove_circle_outline).sizeDp(16).color(Color.WHITE));
        mFabIcons.add(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_done_all).sizeDp(16).color(Color.WHITE));

        Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
        RealmQuery<JSONShow> query = uiRealm.where(JSONShow.class);
        show = query.equalTo("id", showID).findFirst();
        RealmQuery<JSONEpisode> episodeRealmQuery = uiRealm.where(JSONEpisode.class);
        RealmResults<JSONEpisode> episodeRealmResults = episodeRealmQuery.equalTo("isWatched", false).equalTo("show_id", show.getId()).findAll();

        if (episodeRealmResults.size() == 0) {
            mFabIcons.set(1, new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_clear_all).sizeDp(16).color(Color.WHITE));
        }


        Log.d("Number of seasons", Integer.toString(show.getSeasons().size()));
        ButterKnife.bind(this);

        fab.setImageDrawable(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_remove_circle_outline).sizeDp(16).color(Color.WHITE));

        // Set title of Detail page
        collapsingToolbar.setTitle(ShowName);
        Log.d("WatchList backdrop", show.getBackdropBitmap().toString());

        // Attach the Slidr Mechanism to this activity
        slidrInterface = Slidr.attach(this);

        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Seasons"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set title of Detail page
        collapsingToolbar.setTitle(show.getName());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        loadingPanel.setVisibility(View.GONE);

        if (show.getBackdropBitmap() != null) {
            thisBitmap = BitmapFactory.decodeByteArray(show.getBackdropBitmap(), 0, show.getBackdropBitmap().length, options);
            backdrop.setImageBitmap(thisBitmap);
        } else {
            thisBitmap = null;
        }

        if (thisBitmap != null) {
            // save image as byte array
            int defaultColor = 0x000000;
            Palette palette = Palette.from(thisBitmap).generate();

            int vibrantColor = palette.getVibrantColor(defaultColor);
            int mutedColor = palette.getLightMutedColor(defaultColor);

            Log.d("vibrant color", Integer.toString(vibrantColor));

            if (vibrantColor == 0) {
                vibrantColor = getResources().getColor(R.color.colorPrimary);
            }

            if (mutedColor == 0) {
                mutedColor = getResources().getColor(R.color.colorAccent);
            }

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
            TVShowWatchlistSeasonFragment seasonsFragment = new TVShowWatchlistSeasonFragment();
            seasonsFragment.setArguments(seasonsBundle);

            adapterViewPager.addFragment(overviewFragment, "");
            adapterViewPager.addFragment(seasonsFragment, "");
            viewPager.setAdapter(adapterViewPager);


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
                    slidrInterface.unlock();

                    animateFab(mSelectedTab, mFabIcons);
                } else {
                    animateFab(mSelectedTab, mFabIcons);
                    slidrInterface.lock();
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
                    //TODO Remove from your shows
                } else if (mSelectedTab == 1) {
                    Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

                    RealmQuery<JSONEpisode> query = uiRealm.where(JSONEpisode.class);
                    RealmResults<JSONEpisode> episodeRealmResults = query.equalTo("isWatched", false).equalTo("show_id", show.getId()).findAll();
                    Log.d("Unwatched Episodes", Integer.toString(episodeRealmResults.size()));

                    if (episodeRealmResults.size() > 0) { // unwatched episodes
                        uiRealm.beginTransaction();
                        RealmList<JSONSeason> seasons = show.getSeasons();
                        for (int i = 0; i < seasons.size(); i++) {
                            JSONSeason season = seasons.get(i);
                            RealmList<JSONEpisode> episodes = season.getEpisodes();
                            for (int j = 0; j < episodes.size(); j++) {
                                episodes.get(j).setIsWatched(true);
                            }
                        }
                        uiRealm.commitTransaction();

                        TVShowWatchlistSeasonFragment fragment = (TVShowWatchlistSeasonFragment) adapterViewPager.getRegisteredFragment(1);
                        fragment.updateAllGroupsAndChildren();

                        Snackbar.make(v, "All episodes marked watched!",
                                Snackbar.LENGTH_LONG).show();

                        mFabIcons.set(1, new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_clear_all).sizeDp(16).color(Color.WHITE));
                        animateFab(mSelectedTab, mFabIcons);

                    } else { //all episodes watched
                        uiRealm.beginTransaction();
                        RealmList<JSONSeason> seasons = show.getSeasons();
                        for (int i = 0; i < seasons.size(); i++) {
                            JSONSeason season = seasons.get(i);
                            RealmList<JSONEpisode> episodes = season.getEpisodes();
                            for (int j = 0; j < episodes.size(); j++) {
                                episodes.get(j).setIsWatched(false);
                            }
                        }
                        uiRealm.commitTransaction();

                        TVShowWatchlistSeasonFragment fragment = (TVShowWatchlistSeasonFragment) adapterViewPager.getRegisteredFragment(1);
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
