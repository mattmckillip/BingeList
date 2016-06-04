package com.example.matt.movieWatchList.ViewControllers.Activities;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.JSONCast;
import com.example.matt.movieWatchList.Models.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.ViewControllers.Adapters.CastAdapter;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.lang.reflect.Field;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

/**
 * Created by Matt on 6/3/2016.
 */
public class WatchListDetailActivity extends AppCompatActivity {
    Integer movieID;
    Bitmap thisBitmap;
    private JSONMovie movie;
    private RealmList<JSONCast> castList = new RealmList<>();
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;

    private RealmList<JSONCast> crewList = new RealmList<>();
    private RecyclerView crewRecyclerView;
    private CastAdapter crewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieID = getIntent().getIntExtra("movieId",0);
        Log.d("Movie ID", Integer.toString(movieID));

        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Cast recycler view
        castRecyclerView = (RecyclerView) findViewById(R.id.cast_recycler_view);
        castAdapter = new CastAdapter(castList);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        castRecyclerView.setLayoutManager(castLayoutManager);
        castRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castRecyclerView.setAdapter(castAdapter);

        // Cast recycler view
        crewRecyclerView = (RecyclerView) findViewById(R.id.crew_recycler_view);
        crewAdapter = new CastAdapter(crewList);
        RecyclerView.LayoutManager crewLayoutManager = new LinearLayoutManager(getApplicationContext());
        crewRecyclerView.setLayoutManager(crewLayoutManager);
        crewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        crewRecyclerView.setAdapter(crewAdapter);

        findViewById(R.id.appbar).setVisibility(View.GONE);
        findViewById(R.id.collapsing_toolbar).setVisibility(View.GONE);
        findViewById(R.id.scroll_view).setVisibility(View.GONE);
        findViewById(R.id.fab).setVisibility(View.GONE);

        Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();

        // Build the query looking at all users:
        RealmQuery<JSONMovie> query = uiRealm.where(JSONMovie.class);

        // Execute the query:
        this.movie = query.equalTo("id",movieID).findFirst();
        updateUI();

        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        findViewById(R.id.fab).setVisibility(View.GONE);
    }

    private void updateUI(){
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Set title of Detail page
        collapsingToolbar.setTitle(movie.getTitle());
        final ImageView image = (ImageView) findViewById(R.id.image);
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

        Bitmap thisBitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        thisBitmap = BitmapFactory.decodeByteArray(movie.getBackdropBitmap(), 0, movie.getBackdropBitmap().length, options);
        image.setImageBitmap(thisBitmap);

        if (thisBitmap != null) {
            // save image as byte array
            int defaultColor = 0x000000;
            Palette palette = Palette.from(thisBitmap).generate();
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            RatingBar stars = (RatingBar) findViewById(R.id.rating);
            TextView plotTitle = (TextView) findViewById(R.id.plot_title);
            TextView castTitle = (TextView) findViewById(R.id.cast_title);
            TextView crewTitle = (TextView) findViewById(R.id.crew_title);

            int vibrantColor = palette.getVibrantColor(defaultColor);
            Log.d("vibrant color", Integer.toString(vibrantColor));

            if (vibrantColor != 0){
                plotTitle.setTextColor(vibrantColor);
                castTitle.setTextColor(vibrantColor);
                crewTitle.setTextColor(vibrantColor);

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

            findViewById(R.id.appbar).setVisibility(View.VISIBLE);
            findViewById(R.id.collapsing_toolbar).setVisibility(View.VISIBLE);
            findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
            findViewById(R.id.fab).setVisibility(View.VISIBLE);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
        else {
            Log.d("Pallete", "Bitmap Null");
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.more_info);
        ExpandableTextView plot = (ExpandableTextView) findViewById(R.id.expand_text_view);
        RatingBar stars = (RatingBar) layout.findViewById(R.id.rating);
        TextView runtime = (TextView) layout.findViewById(R.id.runtime);
        TextView releaseDate = (TextView) layout.findViewById(R.id.release_date);

        plot.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });
        plot.setText(movie.getOverview());
        stars.setRating(movie.getVote_average().floatValue());
        runtime.setText(Integer.toString(movie.getRuntime()));
        releaseDate.setText(movie.getReleaseDate());

        /*RealmList<JSONCast> slicedCastList = new RealmList<>();
        RealmList<JSONCast> slicedCrewList = new RealmList<>();

        for (int i=0; i < 3; i++){
            slicedCastList.add(movie.getCast().get(i));
            slicedCrewList.add(movie.getCrew().get(i));
        }*/
        // Populate cast and crew recycler views
        /*castRecyclerView.setAdapter( new CastAdapter(slicedCastList));
        crewRecyclerView.setAdapter( new CastAdapter(slicedCrewList));
        castRecyclerView.setFocusable(false);
        crewRecyclerView.setFocusable(false);*/
    }
}