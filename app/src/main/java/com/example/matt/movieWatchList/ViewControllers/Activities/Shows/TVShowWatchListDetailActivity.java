package com.example.matt.movieWatchList.viewControllers.activities.shows;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.viewControllers.adapters.CastAdapter;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowBrowseSeasonFragment;
import com.example.matt.movieWatchList.viewControllers.fragments.shows.TVShowOverviewFragment;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;


/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class TVShowWatchListDetailActivity extends AppCompatActivity {
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    Integer showID;
    Adapter adapterViewPager;
    @BindView(R.id.appBar)
    AppBarLayout appbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.background)
    ImageView backdrop;
    private RealmList<JSONCast> castList = new RealmList<>();
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;
    private RealmList<JSONCast> crewList = new RealmList<>();
    private RecyclerView crewRecyclerView;
    private CastAdapter crewAdapter;
    private DrawerLayout mDrawerLayout;
    private SlidrInterface slidrInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvshow_activity_detail);
        showID = getIntent().getIntExtra("showID", 0);
        String ShowName = getIntent().getStringExtra("showName");

        ButterKnife.bind(this);

        // Set title of Detail page
        collapsing_toolbar.setTitle(ShowName);

        // Attach the Slidr Mechanism to this activity
        slidrInterface = Slidr.attach(this);

        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Seasons"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
        // Build the query looking at all users:
        RealmQuery<JSONShow> query = uiRealm.where(JSONShow.class);
        // Execute the query:
        JSONShow realmShow = query.equalTo("id", showID).findFirst();

        Picasso.with(getApplicationContext())
                .load(realmShow.getBackdropPath())
                .fit().centerCrop()
                .into(backdrop);

        // Setting ViewPager for each Tabs
        adapterViewPager = new Adapter(getSupportFragmentManager());

        Bundle overviewBundle = new Bundle();
        overviewBundle.putInt("showID", showID);
        TVShowOverviewFragment overviewFragment = new TVShowOverviewFragment();
        overviewFragment.setArguments(overviewBundle);

        Bundle seasonsBundle = new Bundle();
        seasonsBundle.putInt("showID", showID);
        TVShowBrowseSeasonFragment seasonsFragment = new TVShowBrowseSeasonFragment();
        seasonsFragment.setArguments(seasonsBundle);

        TVShowBrowseSeasonFragment testSeasons = new TVShowBrowseSeasonFragment();

        adapterViewPager.addFragment(overviewFragment, "");
        adapterViewPager.addFragment(testSeasons, "");
        viewPager.setAdapter(adapterViewPager);

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
}
