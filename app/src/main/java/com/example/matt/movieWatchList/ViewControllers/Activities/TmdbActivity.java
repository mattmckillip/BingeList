package com.example.matt.movieWatchList.viewControllers.activities;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.Cast;
import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.Crew;
import com.example.matt.movieWatchList.Models.POJO.Movie;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.viewControllers.adapters.CastAdapter;
import com.example.matt.movieWatchList.uitls.MovieAPI;
import com.example.matt.movieWatchList.uitls.PaletteTransformation;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;



/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class TmdbActivity extends AppCompatActivity {
    Integer movieID;
    Bitmap thisBitmap;
    private JSONMovie realmMovie;
    private Movie movie;
    private RealmList<JSONCast> castList = new RealmList<>();
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;

    private RealmList<JSONCast> crewList = new RealmList<>();
    private RecyclerView crewRecyclerView;
    private CastAdapter crewAdapter;

    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;

    /*@BindView(R.id.fab)
    FloatingActionButton fab;*/

    @BindView(R.id.appbar)
    AppBarLayout appbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.scroll_view)
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

    @BindView(R.id.release_date)
    TextView releaseDate;

    @BindView(R.id.user_rating)
    TextView userRating;

    @BindView(R.id.more_info)
    LinearLayout layout;

    @BindView(R.id.expand_text_view)
    ExpandableTextView plot;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        movieID = getIntent().getIntExtra("movieId",0);

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
                realmMovie.setOnWatchList(true);
                //JSONMovie movieToAdd = uiRealm.createObject(movie);
                uiRealm.copyToRealm(realmMovie);
                uiRealm.commitTransaction();

                Snackbar.make(v, "Added to watch list!",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);

        Call<Movie> call = service.getMovie(Integer.toString(movieID));

        call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(retrofit.Response<Movie> response, Retrofit retrofit) {
                    Log.d("getMovie()", "Callback Success");
                    movie = response.body();
                    movie.setBackdropPath("https://image.tmdb.org/t/p/w500//" + movie.getBackdropPath());
                    realmMovie = movie.convertToRealm();

                    /*MovieAPI service = retrofit.create(MovieAPI.class);
                    Call<Credits> call = service.getCredits(Integer.toString(movieID));

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

                            realmMovie.setCrew(realmCrew);
                            realmMovie.setCast(realmCast);

                            updateUI();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d("GetCredits()", "Callback Failure");
                        }
                    });*/
                    updateUI();
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("getMovie()", "Callback Failure");
                }
            });

    }


    private void addByteArray(byte[] image) {
        realmMovie.setBackdropBitmap(image);
    }

    private void updateUI(){
        //this.movie = movie;
        // Set Collapsing Toolbar layout to the screen
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Set title of Detail page
        collapsingToolbar.setTitle(movie.getTitle());

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
                .load(movie.getBackdropPath())
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

        plot.setText(realmMovie.getOverview());
        stars.setRating(realmMovie.getVote_average().floatValue());
        runtime.setText(Integer.toString(realmMovie.getRuntime()) + " min");
        releaseDate.setText(realmMovie.getReleaseDate());
        userRating.setText(Double.toString(realmMovie.getVote_average())+ "/10");



        collapsing_toolbar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);
        Call<Credits> call = service.getCredits(Integer.toString(movieID));

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

                realmMovie.setCrew(realmCrew);
                realmMovie.setCast(realmCast);

                // Populate cast and crew recycler views
                castRecyclerView.setAdapter( new CastAdapter(realmMovie.getCast(), getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));
                crewRecyclerView.setAdapter( new CastAdapter(realmMovie.getCrew(), getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));
                castRecyclerView.setFocusable(false);
                crewRecyclerView.setFocusable(false);

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("GetCredits()", "Callback Failure");
            }
        });
    }
}
