package com.example.matt.movieWatchList.viewControllers.activities.shows;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.shows.TVShow;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;
import com.example.matt.movieWatchList.viewControllers.activities.SettingsActivity;
import com.example.matt.movieWatchList.viewControllers.activities.movies.BrowseMoviesActivity;
import com.example.matt.movieWatchList.viewControllers.activities.movies.SearchMoviesActivity;
import com.example.matt.movieWatchList.viewControllers.adapters.CastAdapter;
import com.example.matt.movieWatchList.viewControllers.fragments.DummyFragment;
import com.example.matt.movieWatchList.viewControllers.fragments.movies.BrowseMoviesFragment;
import com.example.matt.movieWatchList.viewControllers.fragments.movies.MovieWatchListFragment;
import com.example.matt.movieWatchList.viewControllers.fragments.movies.SearchFragment;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmMigration;


/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class BrowseTVShowsDetailActivity extends AppCompatActivity {
    Integer showID;
    Bitmap thisBitmap;
    private JSONShow realmShow;
    private TVShow show;
    private RealmList<JSONCast> castList = new RealmList<>();
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;

    private RealmList<JSONCast> crewList = new RealmList<>();
    private RecyclerView crewRecyclerView;
    private CastAdapter crewAdapter;

    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;

    Adapter adapterViewPager;
    private DrawerLayout mDrawerLayout;


    @BindView(R.id.appBar)
    AppBarLayout appbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    /*@BindView(R.id.scroll_view)
    NestedScrollView scroll_view;

    @BindView(R.id.backdrop)
    ImageView backdrop;

    @BindView(R.id.loadingPanel)
    RelativeLayout loadingPanel;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rating)
    RatingBar stars;

    @BindView(R.id.plot_title)
    TextView plotTitle;

    @BindView(R.id.cast_title)
    TextView castTitle;

    @BindView(R.id.crew_title)
    TextView crewTitle;

    @BindView(R.id.overview_title)
    TextView overviewTitle;

    @BindView(R.id.runtime)
    TextView runtime;

    @BindView(R.id.user_rating)
    TextView userRating;

    @BindView(R.id.more_info)
    LinearLayout layout;

    @BindView(R.id.expand_text_view)
    ExpandableTextView plot;

    @BindView(R.id.fab)
    FloatingActionButton fab;*/

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvshow_activity_detail);

        ButterKnife.bind(this);

        // Attach the Slidr Mechanism to this activity
        Slidr.attach(this);

        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Seasons"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        adapterViewPager = new Adapter(getSupportFragmentManager());

        Bundle fightClubSearchBundle = new Bundle();
        fightClubSearchBundle.putString("query", "fight club");
        SearchFragment fragment1 = new SearchFragment();
        fragment1.setArguments(fightClubSearchBundle);

        Bundle deadpoolSearchBundle = new Bundle();
        deadpoolSearchBundle.putString("query", "deadpool");
        SearchFragment fragment2 = new SearchFragment();
        fragment2.setArguments(deadpoolSearchBundle);

        adapterViewPager.addFragment(fragment1, "Fight Club");
        adapterViewPager.addFragment(fragment2, "Deadpool");
        viewPager.setAdapter(adapterViewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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


        /*showID = getIntent().getIntExtra("showID",0);

        // Attach the Slidr Mechanism to this activity
        Slidr.attach(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Cast recycler view
        castRecyclerView = (RecyclerView) findViewById(R.id.cast_recycler_view);
        castAdapter = new CastAdapter(castList, getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        castRecyclerView.setLayoutManager(castLayoutManager);
        castRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castRecyclerView.setAdapter(castAdapter);

        // Cast recycler view
        crewRecyclerView = (RecyclerView) findViewById(R.id.crew_recycler_view);
        crewAdapter = new CastAdapter(crewList, getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager crewLayoutManager = new LinearLayoutManager(getApplicationContext());
        crewRecyclerView.setLayoutManager(crewLayoutManager);
        crewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        crewRecyclerView.setAdapter(crewAdapter);

        appbar.setVisibility(View.GONE);
        collapsing_toolbar.setVisibility(View.GONE);
        scroll_view.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

                uiRealm.beginTransaction();
                realmShow.setOnWatchList(true);
                //JSONMovie movieToAdd = uiRealm.createObject(show);
                uiRealm.copyToRealm(realmShow);
                uiRealm.commitTransaction();

                Snackbar.make(v, "Added to watch list!",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/tv/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TVShowAPI service = retrofit.create(TVShowAPI.class);

        Call<TVShow> call = service.getTVShow(Integer.toString(showID));

        call.enqueue(new Callback<TVShow>() {
                @Override
                public void onResponse(retrofit.Response<TVShow> response, Retrofit retrofit) {
                    Log.d("getMovie()", "Callback Success");
                    show = response.body();
                    show.setBackdropPath("https://image.tmdb.org/t/p/w500//" + show.getBackdropPath());
                    realmShow = show.convertToRealm();

                    updateUI();
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("getMovie()", "Callback Failure");
                }
            });

            }*/


            /*private void addByteArray(byte[] image) {
                realmShow.setBackdropBitmap(image);
            }*/

/*    private void updateUI(){
        //this.show = show;
        // Set Collapsing Toolbar layout to the screen
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Set title of Detail page
        collapsingToolbar.setTitle(show.getName());

        final ImageView image = (ImageView) findViewById(R.id.backdrop);
        final Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/Lobster-Regular.ttf");
        try {
            final Field field = collapsingToolbar.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);

            final Object object = field.get(collapsingToolbar);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            ((TextPaint) tpf.get(object)).setTypeface(tf);
        } catch (Exception ignored) {
        }
        Log.d("Update UI", "3");

        Picasso.with(this)
                .load(show.getBackdropPath())
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into(image, new PaletteTransformation.PaletteCallback(image) {
                    @Override public void onSuccess(Palette palette) {
                        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap(); // Ew!

                        appbar.setVisibility(View.VISIBLE);
                        collapsing_toolbar.setVisibility(View.VISIBLE);
                        scroll_view.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);
                        loadingPanel.setVisibility(View.GONE);


                        int defaultColor = 0x000000;
                        int vibrantColor = palette.getVibrantColor(defaultColor);

                        if (vibrantColor != 0){
                            plotTitle.setTextColor(vibrantColor);
                            castTitle.setTextColor(vibrantColor);
                            crewTitle.setTextColor(vibrantColor);
                            overviewTitle.setTextColor(vibrantColor);

                            LayerDrawable starProgressDrawable = (LayerDrawable) stars.getProgressDrawable();
                            starProgressDrawable.getDrawable(2).setColorFilter(palette.getMutedColor(defaultColor), PorterDuff.Mode.SRC_ATOP);
                            starProgressDrawable.getDrawable(1).setColorFilter(palette.getMutedColor(defaultColor), PorterDuff.Mode.SRC_ATOP);

                            collapsingToolbar.setBackgroundColor(vibrantColor);
                            collapsingToolbar.setContentScrimColor(vibrantColor);
                            collapsingToolbar.setStatusBarScrimColor(vibrantColor);

                            fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));


                        } else {
                            Log.d("Palette", "Could not gather vibrant color");
                        }

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        addByteArray(stream.toByteArray());
                    }

                    @Override
                    public void onError() {
                        //TODO
                    }
                });


        plot.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        plot.setText(realmShow.getOverview());
        stars.setRating(realmShow.getVoteAverage().floatValue());
        runtime.setText(Integer.toString(realmShow.getNumberOfSeasons()) + " seasons");
        userRating.setText(Double.toString(realmShow.getVoteAverage())+ "/10");



        collapsing_toolbar.setVisibility(View.VISIBLE);

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/tv/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TVShowAPI service = retrofit.create(TVShowAPI.class);
        Call<Credits> call = service.getCredits(Integer.toString(showID));

        call.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(retrofit.Response<Credits> response, Retrofit retrofit) {
                Log.d("GetCredits()", "Callback Success");
                List<Cast> cast = response.body().getCast();
                List<Crew> crew = response.body().getCrew();

                RealmList<JSONCast> realmCast = new RealmList<>();
                for( int i = 0; i <= 3; i++) {
                    realmCast.add(cast.get(i).convertToRealm());
                }

                RealmList<JSONCast> realmCrew = new RealmList<>();
                for( int i = 0; i <= 3; i++) {
                    realmCrew.add(crew.get(i).convertToRealm());
                }

                realmShow.setCrew(realmCrew);
                realmShow.setCast(realmCast);

                // Populate cast and crew recycler views
                castRecyclerView.setAdapter( new CastAdapter(realmShow.getCast(), getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));
                crewRecyclerView.setAdapter( new CastAdapter(realmShow.getCrew(), getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));
                castRecyclerView.setFocusable(false);
                crewRecyclerView.setFocusable(false);

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("GetCredits()", "Callback Failure");
            }
        });
    }*/
}
