package com.example.matt.movieWatchList.viewControllers.activities.movies;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONGenre;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.viewControllers.adapters.CastAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;


public class MovieWatchListDetailActivity extends AppCompatActivity {
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;
    private static final String TAG = "MovieWLDetailActivity";
    private static final int DEFAULT_COLOR = 0x000000;

    private Integer movieID;
    private JSONMovie movie;
    private RealmList<JSONCast> castList = new RealmList<>();
    private CastAdapter castAdapter;
    private RealmList<JSONCast> crewList = new RealmList<>();
    private CastAdapter crewAdapter;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieID = getIntent().getIntExtra("movieId", 0);

        setContentView(R.layout.movie_watchlist_detail_activity);

        Slidr.attach(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setAdapters();
        setViewsInvisible();
        setListeners();

        Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

        // Build the query looking at all users:
        RealmQuery<JSONMovie> query = uiRealm.where(JSONMovie.class);

        // Execute the query:
        this.movie = query.equalTo("id", movieID).findFirst();

        // Adding Floating Action Button to bottom right of main view
        IconicsDrawable search = new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_done).sizeDp(16).color(Color.WHITE);
        fab.setImageDrawable(search);

        updateUI();
    }

    private void setListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
                uiRealm.beginTransaction();
                //JSONMovie movieToAdd = uiRealm.createObject(movie);
                movie.setOnWatchList(true);
                uiRealm.copyToRealm(movie);

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

        imdbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.imdb.com/title/" + movie.getImdbID());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void setViewsInvisible() {
        appbar.setVisibility(View.INVISIBLE);
        collapsingToolbar.setVisibility(View.INVISIBLE);
        scroll_view.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
    }

    private void setAdapters() {
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
    }

    private void updateUI() {
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

            setColors(vibrantColor, mutedColor);

            if (vibrantColor == DEFAULT_COLOR) {
                vibrantColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
            }
            if (mutedColor == DEFAULT_COLOR) {
                mutedColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
            }
            setColors(vibrantColor, mutedColor);
            setViewsVisible();
        } else {
            Log.d("Pallete", "Bitmap Null");
            setViewsVisible();
        }

        plot.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        // Populate cast and crew recycler views
        castRecyclerView.setAdapter(new CastAdapter(movie.getCast(), getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));
        crewRecyclerView.setAdapter(new CastAdapter(movie.getCrew(), getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY));
        castRecyclerView.setFocusable(false);
        crewRecyclerView.setFocusable(false);
    }

    private void setViewsVisible() {
        appbar.setVisibility(View.VISIBLE);
        collapsingToolbar.setVisibility(View.VISIBLE);
        scroll_view.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        loadingPanel.setVisibility(View.GONE);
    }

    private void setColors(int vibrantColor, int mutedColor) {
        plotTitle.setTextColor(vibrantColor);
        castTitle.setTextColor(vibrantColor);
        crewTitle.setTextColor(vibrantColor);
        overviewTitle.setTextColor(vibrantColor);

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

    private void setData() {
        collapsingToolbar.setTitle(movie.getTitle());
        plot.setText(movie.getOverview());
        stars.setRating(movie.getVote_average().floatValue());
        runtime.setText(convertToReadableTime(movie.getRuntime()));
        userRating.setText(Double.toString(movie.getVote_average()) + "/10");
        String genreString = "";
        for (JSONGenre genre : movie.getGenres()){
            genreString = genreString + genre.getGenre() + "\n";
        }
        genres.setText(genreString);
    }

    private String convertToReadableTime(int time) {
        int hours = time / 60; //since both are ints, you get an int
        int minutes = time % 60;
        return String.format("%dh %02dmin", hours, minutes);
    }
}
