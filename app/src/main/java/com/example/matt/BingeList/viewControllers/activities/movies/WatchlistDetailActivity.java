package com.example.matt.bingeList.viewControllers.activities.movies;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.models.Cast;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.Crew;
import com.example.matt.bingeList.models.Genre;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.movies.MovieQueryReturn;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.viewControllers.activities.CastActivity;
import com.example.matt.bingeList.viewControllers.activities.CrewActivity;
import com.example.matt.bingeList.viewControllers.adapters.CastAdapter;
import com.example.matt.bingeList.viewControllers.adapters.CrewAdapter;
import com.example.matt.bingeList.viewControllers.adapters.SimilarMoviesAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WatchlistDetailActivity extends AppCompatActivity {
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;
    private static final String TAG = "MovieWLDetailActivity";
    private static final int DEFAULT_COLOR = 0x000000;

    private Integer movieID;
    private Movie movie;
    private RealmList<Cast> mCast = new RealmList<>();
    private CastAdapter castAdapter;
    private RealmList<Crew> mCrew = new RealmList<>();
    private CrewAdapter crewAdapter;
    private Credits mCredits;
    private Context mContext;

    private ArrayList<MovieResult> similarMovieList = new ArrayList<>();
    private SimilarMoviesAdapter similarMovieAdapter;

    private Realm mUiRealm;

    @BindView(R.id.appbar)
    AppBarLayout appbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.backdrop)
    ImageView image;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.loadingPanel)
    RelativeLayout loadingPanel;

    @BindView(R.id.scroll_view)
    NestedScrollView scroll_view;

    @BindView(R.id.more_info)
    LinearLayout layout;

    @BindView(R.id.overview_title)
    TextView overviewTitle;

    @BindView(R.id.rating)
    RatingBar stars;

    @BindView(R.id.user_rating)
    TextView userRating;

    @BindView(R.id.runtime)
    TextView runtime;

    @BindView(R.id.plot_title)
    TextView plotTitle;

    @BindView(R.id.expand_text_view)
    ExpandableTextView plot;

    @BindView(R.id.cast_title)
    TextView castTitle;

    @BindView(R.id.cast_recycler_view)
    RecyclerView castRecyclerView;

    @BindView(R.id.crew_title)
    TextView crewTitle;

    @BindView(R.id.crew_recycler_view)
    RecyclerView crewRecyclerView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.genres)
    TextView genres;

    @BindView(R.id.imdb)
    IconicsButton imdbLink;

    @BindView(R.id.see_more_cast)
    Button seeMoreCastButton;

    @BindView(R.id.see_more_crew)
    Button seeMoreCrewButton;

    @BindView(R.id.see_more_similar_movies)
    Button seeMoreSimilarMoviesButton;

    @BindView(R.id.similar_movies_recycler_view)
    RecyclerView similarMovieRecyclerView;


    @OnClick(R.id.fab)
    public void setFab(View view) {
        Log.d(TAG, "setFab()");

        RealmResults<Movie> watchListMovies = mUiRealm.where(Movie.class).equalTo("onWatchList", true).equalTo("id", movieID).findAll();
        RealmResults<Movie> watchedMovies = mUiRealm.where(Movie.class).equalTo("isWatched", true).equalTo("id", movieID).findAll();
        Log.d(TAG, "Watch list size: " + Integer.toString(watchListMovies.size()));
        Log.d(TAG, "Watched list size: " + Integer.toString(watchedMovies.size()));

        if (watchListMovies.size() == 1){
            Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

            uiRealm.beginTransaction();
            movie.setWatched(true);
            movie.setOnWatchList(false);
            uiRealm.copyToRealmOrUpdate(movie);
            uiRealm.commitTransaction();

            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_undo).sizeDp(16).color(Color.WHITE));;

            Snackbar.make(view, movie.getTitle() + " watched!", Snackbar.LENGTH_LONG).show();
        } else if (watchedMovies.size() == 1) {
            Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

            uiRealm.beginTransaction();
            movie.setWatched(false);
            movie.setOnWatchList(true);
            uiRealm.copyToRealmOrUpdate(movie);
            uiRealm.commitTransaction();

            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_done).sizeDp(16).color(Color.WHITE));;

            Snackbar.make(view, movie.getTitle() + " moved to watchlist!", Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.see_more_cast)
    public void seeMoreCast(View view) {
        Intent intent = new Intent(getBaseContext(), CastActivity.class);
        intent.putExtra("movieID", movieID);
        startActivity(intent);
    }

    @OnClick(R.id.see_more_crew)
    public void seeMoreCrew(View view) {
        Intent intent = new Intent(getBaseContext(), CrewActivity.class);
        intent.putExtra("movieID", movieID);
        startActivity(intent);
    }

    @OnClick(R.id.see_more_similar_movies)
    public void seeMoreSimilarMovies(View view) {
        Intent intent = new Intent(getBaseContext(), SimilarMoviesActivity.class);
        intent.putExtra("movieID", movieID);
        startActivity(intent);
    }

    @OnClick(R.id.imdb)
    public void setImdbLink(View view) {
        Uri uri = Uri.parse("http://www.imdb.com/title/" + movie.getImdbId());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        movieID = getIntent().getIntExtra("movieId", 0);
        mUiRealm = ((MyApplication) getApplication()).getUiRealm();
        setContentView(R.layout.movie_detail_activity);

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true)
                .build();

        Slidr.attach(this, config);
        mContext = getApplicationContext();

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setAdapters();
        setViewsInvisible();

        movie = mUiRealm.where(Movie.class).equalTo("id", movieID).findFirst();
        mCredits = mUiRealm.where(Credits.class).equalTo("id", movieID).findFirst();

        if (mCredits != null) {
            mCrew = mCredits.getCrew();
            mCast = mCredits.getCast();
        }

        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }

    private void setViewsInvisible() {
        Log.d(TAG, "setViewsInvisible()");

        appbar.setVisibility(View.INVISIBLE);
        collapsingToolbar.setVisibility(View.INVISIBLE);
        scroll_view.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
    }

    private void setAdapters() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setAdapters()");
        }

        // PersonCast recycler view
        castAdapter = new CastAdapter(mCast, mContext, NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(mContext);
        castRecyclerView.setLayoutManager(castLayoutManager);
        castRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castRecyclerView.setAdapter(castAdapter);

        // PersonCast recycler view
        crewAdapter = new CrewAdapter(mCrew, mContext, NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager crewLayoutManager = new LinearLayoutManager(mContext);
        crewRecyclerView.setLayoutManager(crewLayoutManager);
        crewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        crewRecyclerView.setAdapter(crewAdapter);


        // Similar Moves recycler view
        similarMovieAdapter = new SimilarMoviesAdapter(similarMovieList, mContext, NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager similaryMovieLayoutManager = new LinearLayoutManager(mContext);
        similarMovieRecyclerView.setLayoutManager(similaryMovieLayoutManager);
        similarMovieRecyclerView.setItemAnimator(new DefaultItemAnimator());
        similarMovieRecyclerView.setAdapter(similarMovieAdapter);
    }

    private void updateUI() {
        Log.d(TAG, "updateUI()");

        // Set title of Detail page
        setData();

        Bitmap thisBitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        if (movie.getBackdropBitmap() != null) {
            thisBitmap = BitmapFactory.decodeByteArray(movie.getBackdropBitmap(), 0, movie.getBackdropBitmap().length, options);
            image.setImageBitmap(thisBitmap);
        } else {
            thisBitmap = null;
        }

        if (thisBitmap != null) {
            // save image as byte array
            int defaultColor = 0x000000;
            Palette palette = Palette.from(thisBitmap).generate();

            int vibrantColor = palette.getVibrantColor(DEFAULT_COLOR);
            int mutedColor = palette.getMutedColor(DEFAULT_COLOR);

            if (vibrantColor == DEFAULT_COLOR) {
                vibrantColor = ContextCompat.getColor(mContext, R.color.colorAccent);
            }
            if (mutedColor == DEFAULT_COLOR) {
                mutedColor = ContextCompat.getColor(mContext, R.color.colorPrimary);
            }
            setColors(vibrantColor, mutedColor);
            setViewsVisible();
        } else {
            Log.d(TAG, "Bitmap is null");
            setViewsVisible();
        }

        plot.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        loadCredits();
        loadSimilarMovies();

    }
    //HELPER METHODS
    private void loadSimilarMovies() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadSimilarMovies()");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        final Call<MovieQueryReturn> similarMoviesCall = service.getSimilarMovies(Integer.toString(movieID));

        similarMoviesCall.enqueue(new Callback<MovieQueryReturn>() {
            @Override
            public void onResponse(Call<MovieQueryReturn> call, Response<MovieQueryReturn> response) {
                List<MovieResult> similarMovies = response.body().getMovieResults();

                // Populate cast and crew recycler views
                similarMovieRecyclerView.setAdapter(new SimilarMoviesAdapter(similarMovies, mContext, NUMBER_OF_CREW_TO_DISPLAY));
                similarMovieRecyclerView.setFocusable(false);
            }

            @Override
            public void onFailure(Call<MovieQueryReturn> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d("getSimilarMovies()", "No Response");
                }
            }
        });
    }

    private void loadCredits() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadCredits()");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        Call<Credits> call = service.getCredits(Integer.toString(movieID));
        call.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(Call<Credits> call, Response<Credits> response) {
                mCredits = response.body();
                mCast = mCredits.getCast();
                mCrew = mCredits.getCrew();
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Credits - " + mCredits.toString());
                    Log.d(TAG, "PersonCast - " + mCast.toString());
                    Log.d(TAG, "PersonCrew - " + mCrew.toString());
                }

                Integer castSize = Math.min(NUMBER_OF_CREW_TO_DISPLAY, mCast.size());
                Integer crewSize = Math.min(NUMBER_OF_CREW_TO_DISPLAY, mCrew.size());

                // Populate cast and crew recycler views
                castRecyclerView.setAdapter(new CastAdapter(mCast, mContext, castSize));
                crewRecyclerView.setAdapter(new CrewAdapter(mCrew, mContext, crewSize));

                castRecyclerView.setFocusable(false);
                crewRecyclerView.setFocusable(false);
            }

            @Override
            public void onFailure(Call<Credits> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d("GetCredits()", "Callback Failure");
                }
            }
        });
    }

    private void setViewsVisible() {
        Log.d(TAG, "setViewsVisible()");

        appbar.setVisibility(View.VISIBLE);
        collapsingToolbar.setVisibility(View.VISIBLE);
        scroll_view.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        loadingPanel.setVisibility(View.GONE);
    }

    private void setColors(int vibrantColor, int mutedColor) {
        Log.d(TAG, "setColors()");

        plotTitle.setTextColor(vibrantColor);
        castTitle.setTextColor(vibrantColor);
        crewTitle.setTextColor(vibrantColor);
        overviewTitle.setTextColor(vibrantColor);

        seeMoreCrewButton.setTextColor(mutedColor);
        seeMoreCastButton.setTextColor(mutedColor);
        seeMoreSimilarMoviesButton.setTextColor(mutedColor);
        imdbLink.setTextColor(mutedColor);

        LayerDrawable starProgressDrawable = (LayerDrawable) stars.getProgressDrawable();
        starProgressDrawable.getDrawable(2).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);
        starProgressDrawable.getDrawable(1).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);

        collapsingToolbar.setBackgroundColor(vibrantColor);
        collapsingToolbar.setContentScrimColor(vibrantColor);
        collapsingToolbar.setStatusBarScrimColor(vibrantColor);

        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    private void setData() {
        Log.d(TAG, "setData()");

        collapsingToolbar.setTitle(movie.getTitle());
        plot.setText(movie.getOverview());
        stars.setRating(movie.getVoteAverage().floatValue());
        runtime.setText(convertToReadableTime(movie.getRuntime()));
        userRating.setText(Double.toString(movie.getVoteAverage()) + "/10");
        String genreString = "";
        for (Genre genre : movie.getGenres()){
            genreString = genreString + genre.getName() + ", ";
        }
        genres.setText(genreString);

        RealmResults<Movie> watchListMovies = mUiRealm.where(Movie.class).equalTo("onWatchList", true).equalTo("id", movieID).findAll();
        if (watchListMovies.size() == 1){
            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_done).sizeDp(16).color(Color.WHITE));;
        } else{
            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_undo).sizeDp(16).color(Color.WHITE));;
        }
    }

    private String convertToReadableTime(int time) {
        Log.d(TAG, "convertToReadableTime()");

        int hours = time / 60; //since both are ints, you get an int
        int minutes = time % 60;
        return String.format("%dh %02dmin", hours, minutes);
    }
}
