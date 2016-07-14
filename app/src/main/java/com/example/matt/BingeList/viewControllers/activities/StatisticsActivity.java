package com.example.matt.bingeList.viewControllers.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.DrawerHelper;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.fragments.movies.MovieStatsFragment;
import com.example.matt.bingeList.viewControllers.fragments.shows.TVShowStatsFragment;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Matt on 6/10/2016.
 */
public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = StatisticsActivity.class.getSimpleName();

    private Adapter mAdapterViewPager;
    private Drawer mNavigationDrawer;
    private int mViewPagerPosition;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());

        if(PreferencesHelper.getTheme(getApplicationContext()) == ThemeEnum.NIGHT_THEME){
            setTheme(R.style.DarkAppTheme_Base);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_tab_layout);

        ButterKnife.bind(this);

        mViewPagerPosition = 0;

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Stats");

        // Setting ViewPager for each Tabs
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        tabs.setupWithViewPager(viewPager);
        setTabDrawables();


        // Create Navigation drawer
        mNavigationDrawer = new DrawerHelper().GetDrawer(this, toolbar, savedInstanceState);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).sizeDp(16).color(Color.WHITE));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        mAdapterViewPager = new Adapter(getSupportFragmentManager());

        Bundle nowShowingBundle = new Bundle();
        TVShowStatsFragment nowShowingMovies = new TVShowStatsFragment();
        nowShowingMovies.setArguments(nowShowingBundle);

        Bundle topRatedBundle = new Bundle();
        MovieStatsFragment topRatedMovies = new MovieStatsFragment();
        topRatedMovies.setArguments(topRatedBundle);

        mAdapterViewPager.addFragment(nowShowingMovies, "TV Shows");
        mAdapterViewPager.addFragment(topRatedMovies, "Movies");

        viewPager.setAdapter(mAdapterViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        int viewMode = PreferencesHelper.getRecyclerviewViewType(getApplicationContext());
        if (viewMode == ViewType.CARD){
            menu.findItem(R.id.card_view).setChecked(true);
        } else if (viewMode == ViewType.COMPACT_CARD){
            menu.findItem(R.id.compact_view).setChecked(true);
        } else if (viewMode == ViewType.LIST){
            menu.findItem(R.id.list_view).setChecked(true);
        }

        int theme = PreferencesHelper.getTheme(getApplicationContext());
        if (theme == ThemeEnum.DAY_THEME){
            menu.findItem(R.id.light_theme).setChecked(true);
        } else if (theme == ThemeEnum.NIGHT_THEME){
            menu.findItem(R.id.dark_theme).setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                if (BuildConfig.DEBUG) {
                    Log.d("onOptionsItemSelected()", "settings");
                }

                return true;

            case R.id.card_view:
                if (BuildConfig.DEBUG) {
                    Log.d("onOptionsItemSelected()", "card_view");
                }
                PreferencesHelper.setRecyclerviewViewType(ViewType.CARD, getApplicationContext());
                PreferencesHelper.printValues(getApplicationContext());
                viewPager.setAdapter(mAdapterViewPager);

                item.setChecked(true);
                tabs.setupWithViewPager(viewPager);
                setTabDrawables();

                return true;

            case R.id.compact_view:
                if (BuildConfig.DEBUG) {
                    Log.d("onOptionsItemSelected()", "compact_view");
                }
                PreferencesHelper.setRecyclerviewViewType(ViewType.COMPACT_CARD, getApplicationContext());
                PreferencesHelper.printValues(getApplicationContext());
                viewPager.setAdapter(mAdapterViewPager);

                item.setChecked(true);
                tabs.setupWithViewPager(viewPager);
                setTabDrawables();

                return true;

            case R.id.list_view:
                if (BuildConfig.DEBUG) {
                    Log.d("onOptionsItemSelected()", "list_view");
                }
                PreferencesHelper.setRecyclerviewViewType(ViewType.LIST, getApplicationContext());
                mAdapterViewPager.notifyDataSetChanged();
                viewPager.setAdapter(mAdapterViewPager);

                item.setChecked(true);
                tabs.setupWithViewPager(viewPager);
                setTabDrawables();

                return true;

            case R.id.light_theme:
                if (BuildConfig.DEBUG) {
                    Log.d("onOptionsItemSelected()", "light_theme");
                }
                PreferencesHelper.setTheme(ThemeEnum.DAY_THEME, getApplicationContext());
                item.setChecked(true);
                finish();
                startActivity(getIntent());;

                return true;

            case R.id.dark_theme:
                if (BuildConfig.DEBUG) {
                    Log.d("onOptionsItemSelected()", "dark_theme");
                }
                PreferencesHelper.setTheme(ThemeEnum.NIGHT_THEME, getApplicationContext());
                item.setChecked(true);
                finish();
                startActivity(getIntent());;

                return true;

            case android.R.id.home:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                if (BuildConfig.DEBUG) {
                    Log.d("onOptionsItemSelected()", "Sort");
                }
                mNavigationDrawer.openDrawer();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

        /*int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);*/
    }

    @Override
    public void onBackPressed() {
        if (this.mNavigationDrawer.isDrawerOpen()) {
            this.mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void setTabDrawables(){
        tabs.getTabAt(0).setIcon(new IconicsDrawable(this).icon(MaterialDesignIconic.Icon.gmi_tv_alt_play).color(Color.WHITE));
        tabs.getTabAt(1).setIcon(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_movie).color(Color.WHITE));
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
