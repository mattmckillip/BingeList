package com.example.matt.movieWatchList.viewControllers.activities.movies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.Cast;
import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.Crew;
import com.example.matt.movieWatchList.Models.POJO.movies.Movie;
import com.example.matt.movieWatchList.Models.POJO.movies.MovieQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.movies.MovieResult;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONGenre;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.MovieAPI;
import com.example.matt.movieWatchList.uitls.PaletteTransformation;
import com.example.matt.movieWatchList.viewControllers.adapters.CastAdapter;
import com.example.matt.movieWatchList.viewControllers.adapters.SimilarMoviesAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.view.IconicsButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


public class MovieBrowseDetailActivity extends AppCompatActivity {
    private static final String TAG = "MovieBDetailActivity";
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;
    private static final int DEFAULT_COLOR = 0x000000;
    private Integer movieID;
    private JSONMovie realmMovie;
    private Movie movie;

    // Cast Recycler view
    private RealmList<JSONCast> castList = new RealmList<>();
    private CastAdapter castAdapter;

    // Crew Recycler view
    private RealmList<JSONCast> crewList = new RealmList<>();
    private CastAdapter crewAdapter;

    // Similar Movies Recycler view
    private ArrayList<MovieResult> similarMovieList = new ArrayList<>();
    private SimilarMoviesAdapter similarMovieAdapter;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_browse_detail_activity);

        ButterKnife.bind(this);

        movieID = getIntent().getIntExtra("movieId", 0);

        Slidr.attach(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setViewsInvisible();
        setAdapters();
        setListeners();
        getMovie();
    }

    private void updateUI() {
        Log.d(TAG, "updateUI()");
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
        Log.d(TAG, "loadSimilarMovies()");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        final Call<MovieQueryReturn> similarMoviesCall = service.getSimilarMovies(Integer.toString(movieID));

        similarMoviesCall.enqueue(new Callback<MovieQueryReturn>() {
            @Override
            public void onResponse(retrofit.Response<MovieQueryReturn> response, Retrofit retrofit) {
                List<MovieResult> similarMovies = response.body().getMovieResults();

                // Populate cast and crew recycler views
                similarMovieRecyclerView.setAdapter(new SimilarMoviesAdapter(similarMovies, getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));
                similarMovieRecyclerView.setFocusable(false);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("getSimilarMovies()", "Callback Failure");
            }
        });
    }

    private void loadCredits() {
        Log.d(TAG, "loadCredits()");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);
        final Call<Credits> call = service.getCredits(Integer.toString(movieID));

        call.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(retrofit.Response<Credits> response, Retrofit retrofit) {
                List<Cast> cast = response.body().getCast();
                List<Crew> crew = response.body().getCrew();

                RealmList<JSONCast> realmCast = new RealmList<>();
                Integer castSize = Math.min(3, cast.size());
                Integer crewSize = Math.min(3, crew.size());

                for (int i = 0; i < castSize; i++) {
                    realmCast.add(cast.get(i).convertToRealm());
                }

                RealmList<JSONCast> realmCrew = new RealmList<>();
                for (int i = 0; i < crewSize; i++) {
                    Log.d("Crew", Integer.toString(i));
                    realmCrew.add(crew.get(i).convertToRealm());
                }

                realmMovie.setCrew(realmCrew);
                realmMovie.setCast(realmCast);

                // Populate cast and crew recycler views
                castRecyclerView.setAdapter(new CastAdapter(realmMovie.getCast(), getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));
                crewRecyclerView.setAdapter(new CastAdapter(realmMovie.getCrew(), getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));

                castRecyclerView.setFocusable(false);
                crewRecyclerView.setFocusable(false);

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("GetCredits()", "Callback Failure");
            }
        });
    }

    private void loadMovieBackgroundImage() {
        Log.d(TAG, "loadMovieBackgroundImage()");
        //final ImageView image = (ImageView) findViewById(R.id.backdrop);
        //setViewsVisible();
        //setViewsInvisible();

        Picasso.with(this)
                .load(movie.getBackdropPath())
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into(backdrop, new PaletteTransformation.PaletteCallback(backdrop) {
                    @Override
                    public void onSuccess(Palette palette) {
                        Log.d(TAG, "onSuccess()");

                        Bitmap bitmap = ((BitmapDrawable) backdrop.getDrawable()).getBitmap(); // Ew!

                        int vibrantColor = palette.getVibrantColor(DEFAULT_COLOR);
                        int mutedColor = palette.getMutedColor(DEFAULT_COLOR);

                        setColors(vibrantColor, mutedColor);

                        if (vibrantColor == DEFAULT_COLOR) {
                            vibrantColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
                        }
                        if (mutedColor == DEFAULT_COLOR) {
                            mutedColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
                        }
                        setColors(vibrantColor, mutedColor);
                        setViewsVisible();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        addByteArray(stream.toByteArray());
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "onError()");
                    }
                });
    }

    private void getMovie() {
        Log.d(TAG, "getMovie()");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        Call<Movie> call = service.getMovie(Integer.toString(movieID));

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(retrofit.Response<Movie> response, Retrofit retrofit) {
                movie = response.body();
                movie.setBackdropPath("https://image.tmdb.org/t/p/" +  getApplicationContext().getString(R.string.image_size_w500)  + movie.getBackdropPath());
                Log.d(TAG, movie.getGenres().toString());
                realmMovie = movie.convertToRealm();

                updateUI();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("getMovie()", "Callback Failure");
            }
        });
    }

    private void setAdapters() {
        Log.d(TAG, "setAdapters()");

        // Cast recycler view
        castAdapter = new CastAdapter(castList, getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        castRecyclerView.setLayoutManager(castLayoutManager);
        castRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castRecyclerView.setAdapter(castAdapter);

        // Cast recycler view
        crewAdapter = new CastAdapter(crewList, getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager crewLayoutManager = new LinearLayoutManager(getApplicationContext());
        crewRecyclerView.setLayoutManager(crewLayoutManager);
        crewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        crewRecyclerView.setAdapter(crewAdapter);


        // Similar Moves recycler view
        similarMovieAdapter = new SimilarMoviesAdapter(similarMovieList, getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager similaryMovieLayoutManager = new LinearLayoutManager(getApplicationContext());
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        similarMovieRecyclerView.setLayoutManager(similaryMovieLayoutManager);
        similarMovieRecyclerView.setItemAnimator(new DefaultItemAnimator());
        similarMovieRecyclerView.setAdapter(similarMovieAdapter);
    }

    private void setListeners() {
        Log.d(TAG, "setListeners()");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

                uiRealm.beginTransaction();
                realmMovie.setOnWatchList(true);
                uiRealm.copyToRealm(realmMovie);
                uiRealm.commitTransaction();

                Snackbar.make(v, "Added to watch list!",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        seeMoreCastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "See more cast",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        seeMoreCrewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "See more crew",
                        Snackbar.LENGTH_SHORT).show();
            }
        });


        seeMoreSimilarMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "See more similar movies",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        imdbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.imdb.com/title/" + realmMovie.getImdbID());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void addByteArray(byte[] image) {
        Log.d(TAG, "addByteArray()");
        realmMovie.setBackdropBitmap(image);
        loadingPanel.setVisibility(View.GONE);
    }

    private void setData() {
        Log.d(TAG, "setData()");

        collapsingToolbar.setTitle(movie.getTitle());
        plot.setText(realmMovie.getOverview());
        stars.setRating(realmMovie.getVote_average().floatValue());
        runtime.setText(convertToReadableTime(realmMovie.getRuntime()));
        userRating.setText(Double.toString(realmMovie.getVote_average()) + "/10");
        String genreString = "";
        for (JSONGenre genre : realmMovie.getGenres()){
            genreString = genreString + genre.getGenre() + "\n";
        }
        genres.setText(genreString);
    }

    private String convertToReadableTime(int time) {
        int hours = time / 60; //since both are ints, you get an int
        int minutes = time % 60;
        return String.format("%dh %02dmin", hours, minutes);
    }


    private void setColors(int vibrantColor, int mutedColor) {
        Log.d(TAG, "setColors()");

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
        Log.d(TAG, "setViewsVisible()");

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
        Log.d(TAG, "setViewsInvisible()");

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
