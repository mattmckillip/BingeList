package com.example.matt.bingeList.viewControllers.activities.movies;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
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
import com.example.matt.bingeList.models.movies.MovieQueryReturn;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.PaletteTransformation;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.activities.CastActivity;
import com.example.matt.bingeList.viewControllers.activities.CrewActivity;
import com.example.matt.bingeList.viewControllers.adapters.CastAdapter;
import com.example.matt.bingeList.viewControllers.adapters.CrewAdapter;
import com.example.matt.bingeList.viewControllers.adapters.SimilarMoviesAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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


public class BrowseMovieDetailActivity extends AppCompatActivity {
    private static final String TAG = "MovieBDetailActivity";
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;
    private static final int NUMBER_OF_SIMILAR_MOVIES_TO_DISPLAY = 10;

    private static final int DEFAULT_COLOR = 0x000000;
    private Integer movieID;
    private Movie movie;
    private Credits mCredits;
    private RealmList<Crew> mCrew = new RealmList<>();
    private RealmList<Cast> mCast = new RealmList<>();
    private CastAdapter castAdapter;
    private CrewAdapter crewAdapter;
    private ArrayList<MovieResult> similarMovieList = new ArrayList<>();
    private SimilarMoviesAdapter similarMovieAdapter;
    private Realm mUiRealm;
    private Context mContext;

    @BindView(R.id.appbar)
    AppBarLayout appbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

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
    FloatingActionButton fab;

    @BindView(R.id.similar_movies_title)
    TextView similarMoviesTitle;

    @BindView(R.id.cast_recycler_view)
    RecyclerView castRecyclerView;

    @BindView(R.id.crew_recycler_view)
    RecyclerView crewRecyclerView;

    @BindView(R.id.similar_movies_recycler_view)
    RecyclerView similarMovieRecyclerView;

    @BindView(R.id.see_more_cast)
    Button seeMoreCastButton;

    @BindView(R.id.see_more_crew)
    Button seeMoreCrewButton;

    @BindView(R.id.see_more_similar_movies)
    Button seeMoreSimilarMoviesButton;

    @BindView(R.id.genres)
    TextView genres;

    @BindView(R.id.imdb)
    IconicsButton imdbLink;

    @OnClick(R.id.fab)
    public void setWatched(View view) {
        RealmResults<Movie> watchListMovies = mUiRealm.where(Movie.class).equalTo("onWatchList", true).equalTo("id", movieID).findAll();
        RealmResults<Movie> watchedMovie = mUiRealm.where(Movie.class).equalTo("isWatched", true).equalTo("id", movieID).findAll();

        if (watchListMovies.size() == 1){
            Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
            uiRealm.beginTransaction();
            watchListMovies.get(0).deleteFromRealm();

            RealmResults<Credits> creditsResults = mUiRealm.where(Credits.class)
                    .equalTo("id", movieID)
                    .findAll();

            for (int i = 0; i < creditsResults.size(); i++) {
                creditsResults.get(i).deleteFromRealm();
            }

            uiRealm.commitTransaction();

            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_add).sizeDp(16).color(Color.WHITE));;

            Snackbar.make(view, movie.getTitle() + " removed from to watchlist!",
                    Snackbar.LENGTH_LONG).show();
        } else if (watchedMovie.size() == 1){
            Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
            uiRealm.beginTransaction();
            movie.setOnWatchList(true);
            movie.setWatched(false);
            uiRealm.copyToRealmOrUpdate(movie);
            uiRealm.commitTransaction();

            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_clear).sizeDp(16).color(Color.WHITE));;

            Snackbar.make(view, movie.getTitle() + " moved to watchlist!",
                    Snackbar.LENGTH_LONG).show();
        } else{
            Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
            uiRealm.beginTransaction();
            movie.setOnWatchList(true);
            uiRealm.copyToRealm(movie);
            uiRealm.copyToRealmOrUpdate(mCredits);
            uiRealm.commitTransaction();

            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_clear).sizeDp(16).color(Color.WHITE));;

            Snackbar.make(view, movie.getTitle() + " added to watchlist!",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.see_more_cast)
    public void seeMoreCast(View view) {
        Intent intent = new Intent(getBaseContext(), CastActivity.class);
        intent.putExtra(mContext.getString(R.string.movieId), movieID);
        intent.putExtra(mContext.getString(R.string.movieTitle), movie.getTitle());
        startActivity(intent);
    }

    @OnClick(R.id.see_more_crew)
    public void seeMoreCrew(View view) {
        Intent intent = new Intent(getBaseContext(), CrewActivity.class);
        intent.putExtra(mContext.getString(R.string.movieId), movieID);
        intent.putExtra(mContext.getString(R.string.movieTitle), movie.getTitle());
        startActivity(intent);
    }

    @OnClick(R.id.see_more_similar_movies)
    public void seeMoreSimilarMovies(View view) {
        Intent intent = new Intent(getBaseContext(), SimilarMoviesActivity.class);
        intent.putExtra(mContext.getString(R.string.movieId), movieID);
        startActivity(intent);
    }

    @OnClick(R.id.imdb)
    public void openIMDbPage(View view) {
        Uri uri = Uri.parse(mContext.getString(R.string.imdb_person_base_url) + movie.getImdbId());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());
        if(PreferencesHelper.getTheme(getApplicationContext()) == ThemeEnum.NIGHT_THEME){
            setTheme(R.style.DarkAppTheme_Base);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        movieID = getIntent().getIntExtra(mContext.getString(R.string.movieId), 0);
        mUiRealm = ((MyApplication) getApplication()).getUiRealm();

        SlidrConfig config = new SlidrConfig.Builder()
                                .position(SlidrPosition.LEFT)
                                .sensitivity(1f)
                                .velocityThreshold(2400)
                                .distanceThreshold(0.25f)
                                .edge(true)
                                .build();

        Slidr.attach(this, config);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setViewsInvisible();
        setAdapters();
        getMovie();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }

    private void updateUI() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "updateUI()");
        }
        // Set title of Detail page
        loadMovieBackgroundImage();

        setData();

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
                .baseUrl(mContext.getString(R.string.movie_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        final Call<MovieQueryReturn> similarMoviesCall = service.getSimilarMovies(Integer.toString(movieID));

        similarMoviesCall.enqueue(new Callback<MovieQueryReturn>() {
            @Override
            public void onResponse(Call<MovieQueryReturn> call, Response<MovieQueryReturn> response) {
                List<MovieResult> similarMovies = response.body().getMovieResults();

                // Populate cast and crew recycler views
                similarMovieRecyclerView.setAdapter(new SimilarMoviesAdapter(similarMovies, mContext, NUMBER_OF_SIMILAR_MOVIES_TO_DISPLAY));
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
                .baseUrl(mContext.getString(R.string.movie_base_url))
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

    private void loadMovieBackgroundImage() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadMovieBackgroundImage()");
        }
        //final ImageView image = (ImageView) findViewById(R.id.backdrop);
        //setViewsVisible();
        //setViewsInvisible();

        Picasso.with(this)
                .load(movie.getBackdropPath())
                .fit().centerCrop()
                .error(R.drawable.generic_movie_background)
                .transform(PaletteTransformation.instance())
                .into(backdrop, new PaletteTransformation.PaletteCallback(backdrop) {
                    @Override
                    public void onSuccess(Palette palette) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "onSuccess()");
                        }

                        Bitmap bitmap = ((BitmapDrawable) backdrop.getDrawable()).getBitmap(); // Ew!

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

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        addByteArray(stream.toByteArray());
                    }

                    @Override
                    public void onError() {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "onError()");
                        }
                        Bitmap bitmap = ((BitmapDrawable) backdrop.getDrawable()).getBitmap(); // Ew!

                        int vibrantColor = ContextCompat.getColor(mContext, R.color.colorAccent);
                        int mutedColor = ContextCompat.getColor(mContext, R.color.colorPrimary);

                        setColors(vibrantColor, mutedColor);
                        setViewsVisible();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        addByteArray(stream.toByteArray());
                    }
                });
    }

    private void getMovie() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "getMovie()");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.movie_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        Call<Movie> call = service.getMovie(Integer.toString(movieID));

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                movie = response.body();
                if (movie == null) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Null movie: " + response.raw().toString());
                    }
                }
                movie.setBackdropPath(mContext.getString(R.string.image_base_url) +  mContext.getString(R.string.image_size_w500)  + movie.getBackdropPath());
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, movie.getGenres().toString());
                }
                updateUI();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d("getMovie()", "Callback Failure");
                }
            }
        });
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
        similarMovieAdapter = new SimilarMoviesAdapter(similarMovieList, mContext, NUMBER_OF_SIMILAR_MOVIES_TO_DISPLAY);
        RecyclerView.LayoutManager similaryMovieLayoutManager = new LinearLayoutManager(mContext);
        similarMovieRecyclerView.setLayoutManager(similaryMovieLayoutManager);
        similarMovieRecyclerView.setItemAnimator(new DefaultItemAnimator());
        similarMovieRecyclerView.setAdapter(similarMovieAdapter);
    }

    private void addByteArray(byte[] image) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "addByteArray()");
        }
        movie.setBackdropBitmap(image);
        loadingPanel.setVisibility(View.GONE);
    }

    private void setData() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setData()");
        }

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
        RealmResults<Movie> watchedMovie = mUiRealm.where(Movie.class).equalTo("isWatched", true).equalTo("id", movieID).findAll();

        if (watchListMovies.size() == 1){
            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_clear).sizeDp(16).color(Color.WHITE));;
        } else if (watchedMovie.size() == 1){
            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_undo).sizeDp(16).color(Color.WHITE));;
        } else {
            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_add).sizeDp(16).color(Color.WHITE));;
        }
    }

    private String convertToReadableTime(int time) {
        int hours = time / 60; //since both are ints, you get an int
        int minutes = time % 60;
        return String.format("%dh %02dmin", hours, minutes);
    }


    private void setColors(int vibrantColor, int mutedColor) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setColors()");
        }

        plotTitle.setTextColor(vibrantColor);
        castTitle.setTextColor(vibrantColor);
        crewTitle.setTextColor(vibrantColor);
        overviewTitle.setTextColor(vibrantColor);
        similarMoviesTitle.setTextColor(vibrantColor);
        seeMoreSimilarMoviesButton.setTextColor(mutedColor);
        seeMoreCrewButton.setTextColor(mutedColor);
        seeMoreCastButton.setTextColor(mutedColor);
        imdbLink.setTextColor(mutedColor);
        LayerDrawable starProgressDrawable = (LayerDrawable) stars.getProgressDrawable();
        starProgressDrawable.getDrawable(2).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);
        starProgressDrawable.getDrawable(1).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);

        collapsingToolbar.setBackgroundColor(vibrantColor);
        collapsingToolbar.setContentScrimColor(vibrantColor);
        collapsingToolbar.setStatusBarScrimColor(vibrantColor);

        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    private void setViewsVisible(){
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setViewsVisible()");
        }

        appbar.setVisibility(View.VISIBLE);
        collapsingToolbar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        seeMoreCastButton.setVisibility(View.VISIBLE);
        seeMoreCrewButton.setVisibility(View.VISIBLE);
        seeMoreSimilarMoviesButton.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
    }

    private void setViewsInvisible(){
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setViewsInvisible()");
        }

        appbar.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.INVISIBLE);
        collapsingToolbar.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        seeMoreCastButton.setVisibility(View.INVISIBLE);
        seeMoreCrewButton.setVisibility(View.INVISIBLE);
        seeMoreSimilarMoviesButton.setVisibility(View.INVISIBLE);
    }
}
