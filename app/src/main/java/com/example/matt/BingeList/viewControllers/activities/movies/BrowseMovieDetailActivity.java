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
import com.example.matt.bingeList.models.Country;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.Crew;
import com.example.matt.bingeList.models.Genre;
import com.example.matt.bingeList.models.NetflixRouletteResponse;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.movies.MovieQueryReturn;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.API.NetflixAPI;
import com.example.matt.bingeList.uitls.Enums.NetflixStreaming;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.PaletteTransformation;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.activities.CastActivity;
import com.example.matt.bingeList.viewControllers.activities.CrewActivity;
import com.example.matt.bingeList.viewControllers.adapters.movies.BrowseMoviesAdapter;
import com.example.matt.bingeList.viewControllers.adapters.CastAdapter;
import com.example.matt.bingeList.viewControllers.adapters.CrewAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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
    private static final String TAG = BrowseMovieDetailActivity.class.getSimpleName();
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;
    private static final int NUMBER_OF_SIMILAR_MOVIES_TO_DISPLAY = 5;

    private static final int DEFAULT_COLOR = 0x000000;
    private Integer movieID;
    private Movie movie;
    private Credits mCredits;
    private RealmList<Crew> mCrew = new RealmList<>();
    private RealmList<Cast> mCast = new RealmList<>();
    private CastAdapter castAdapter;
    private CrewAdapter crewAdapter;
    private RealmList<Movie> similarMovieList = new RealmList<>();
    private BrowseMoviesAdapter similarMovieAdapter;
    private Realm mUiRealm;
    private Context mContext;
    private int mVibrantColor;
    private int mNetflixId;

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

    @BindView(R.id.mpaa_rating)
    TextView mpaaRating;

    @BindView(R.id.streaming_header)
    TextView mStreamingHeader;

    @BindView(R.id.netflix_image)
    ImageView mNetflixImage;

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

        if (watchListMovies.size() == 1){ // on watchlist, so the fab will remove this movie
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
        } else if (watchedMovie.size() == 1){ // watched, so fab will unwatch movie
            Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
            uiRealm.beginTransaction();
            movie.setOnWatchList(true);
            movie.setWatched(false);
            uiRealm.copyToRealmOrUpdate(movie);
            uiRealm.commitTransaction();

            fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_clear).sizeDp(16).color(Color.WHITE));;

            Snackbar.make(view, movie.getTitle() + " moved to watchlist!",
                    Snackbar.LENGTH_LONG).show();
        } else{ // browse to watchlist
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
        intent.putExtra("id", movieID);
        intent.putExtra("title", movie.getTitle());
        intent.putExtra("isMovie", true);

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
        intent.putExtra("vibrantColor", mVibrantColor);
        startActivity(intent);
    }

    @OnClick(R.id.imdb)
    public void openIMDbPage(View view) {
        Uri uri = Uri.parse(mContext.getString(R.string.imdb_person_base_url) + movie.getImdbId());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.netflix_image)
    public void openNetflix(View view) {
        Uri uri = Uri.parse(mContext.getString(R.string.netflix_base_url) + mNetflixId);
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

        Slidr.attach(this);

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
        // Set title of Detail page
        loadMovieBackgroundImage();

        setData();

        plot.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        castRecyclerView.setFocusable(false);

        loadCredits();
        loadSimilarMovies();
    }

    //HELPER METHODS
    private void loadSimilarMovies() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.movie_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        final Call<MovieQueryReturn> similarMoviesCall = service.getSimilarMovies(Integer.toString(movieID));

        similarMoviesCall.enqueue(new Callback<MovieQueryReturn>() {
            @Override
            public void onResponse(Call<MovieQueryReturn> call, Response<MovieQueryReturn> response) {
                if (response.isSuccessful()) {
                    List<MovieResult> tempSimilarMovies = response.body().getMovieResults();

                    similarMovieList = new RealmList<>();
                    for (int i = 0; i < tempSimilarMovies.size() && i < NUMBER_OF_SIMILAR_MOVIES_TO_DISPLAY; i++) {
                        Movie movie = new Movie();
                        movie.setTitle(tempSimilarMovies.get(i).getTitle());
                        movie.setId(tempSimilarMovies.get(i).getId());
                        movie.setOverview(tempSimilarMovies.get(i).getOverview());
                        movie.setBackdropPath(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + tempSimilarMovies.get(i).getBackdropPath());
                        similarMovieList.add(movie);
                    }
                    similarMovieRecyclerView.setAdapter(new BrowseMoviesAdapter(similarMovieList, mContext, mUiRealm));
                    similarMovieRecyclerView.setFocusable(false);
                }
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

        Picasso.with(this)
                .load(movie.getBackdropPath())
                .fit().centerCrop()
                .error(R.drawable.generic_movie_background)
                .placeholder(R.drawable.generic_movie_background)
                .transform(PaletteTransformation.instance())
                .into(backdrop, new PaletteTransformation.PaletteCallback(backdrop) {
                    @Override
                    public void onSuccess(Palette palette) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "onSuccess()");
                        }

                        Bitmap bitmap = ((BitmapDrawable) backdrop.getDrawable()).getBitmap(); // Ew!

                        mVibrantColor = palette.getVibrantColor(DEFAULT_COLOR);
                        int mutedColor = palette.getMutedColor(DEFAULT_COLOR);

                        if (mVibrantColor == DEFAULT_COLOR) {
                            mVibrantColor = ContextCompat.getColor(mContext, R.color.lightColorPrimary);
                        }
                        if (mutedColor == DEFAULT_COLOR) {
                            mutedColor = ContextCompat.getColor(mContext, R.color.lightColorAccent);
                        }
                        setColors(mVibrantColor, mutedColor);
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.movie_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        Call<Movie> call = service.getMovie(Integer.toString(movieID));

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    movie = response.body();
                    if (movie != null) {
                        movie.setBackdropPath(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + movie.getBackdropPath());
                        updateUI();
                    }
                } else {
                    Snackbar.make(scrollView, "Error accessing API, please try again", Snackbar.LENGTH_LONG).show();
                }
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
        similarMovieAdapter = new BrowseMoviesAdapter(similarMovieList, mContext, mUiRealm);
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
    }

    private void setData() {
        mNetflixImage.setVisibility(View.GONE);
        mStreamingHeader.setVisibility(View.GONE);
        movie.setNetflixStreaming(NetflixStreaming.NOT_STREAMING);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://netflixroulette.net/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetflixAPI service = retrofit.create(NetflixAPI.class);

        Call<NetflixRouletteResponse> call = service.checkNetflix(movie.getImdbId());

        call.enqueue(new Callback<NetflixRouletteResponse>() {
            @Override
            public void onResponse(Call<NetflixRouletteResponse> call, Response<NetflixRouletteResponse> response) {
                Log.d(TAG, response.raw().toString());
                if (response.isSuccessful()) {
                    if (response.body().getNetflixId() != null && !response.body().getNetflixId().equals("null")) {
                        mNetflixId = response.body().getNetflixId();
                        mNetflixImage.setVisibility(View.VISIBLE);
                        mStreamingHeader.setVisibility(View.VISIBLE);
                        movie.setNetflixStreaming(NetflixStreaming.STREAMING);
                    }
                }
            }

            @Override
            public void onFailure(Call<NetflixRouletteResponse> call, Throwable t) {}
        });

        collapsingToolbar.setTitle(movie.getTitle());
        plot.setText(movie.getOverview());
        stars.setRating(movie.getVoteAverage().floatValue());
        runtime.setText(convertToReadableTime(movie.getRuntime()));
        userRating.setText(Double.toString(movie.getVoteAverage()) + "/10");
        for (Country country : movie.getReleases().getCountries()){
            if (country.getIso31661().equals("US")){
                mpaaRating.setText(country.getCertification());
            }
        }
        String genreString = "";
        for (Genre genre : movie.getGenres()){
            genreString = genreString + genre.getName() + ", ";
        }
        genreString = genreString.substring(0, genreString.length() - 2);
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
        appbar.setVisibility(View.VISIBLE);
        collapsingToolbar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        loadingPanel.setVisibility(View.GONE);
    }

    private void setViewsInvisible(){
        appbar.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.INVISIBLE);
        collapsingToolbar.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
    }
}
