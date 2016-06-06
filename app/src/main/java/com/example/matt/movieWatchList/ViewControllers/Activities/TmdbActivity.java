package com.example.matt.movieWatchList.ViewControllers.Activities;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
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
import com.example.matt.movieWatchList.uitls.PaletteTransformation;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class TmdbActivity extends AppCompatActivity {
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

        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Cast recycler view
        castRecyclerView = (RecyclerView) findViewById(R.id.cast_recycler_view);
        castAdapter = new CastAdapter(castList, getApplicationContext());
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        castRecyclerView.setLayoutManager(castLayoutManager);
        castRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castRecyclerView.setAdapter(castAdapter);

        // Cast recycler view
        crewRecyclerView = (RecyclerView) findViewById(R.id.crew_recycler_view);
        crewAdapter = new CastAdapter(crewList, getApplicationContext());
        RecyclerView.LayoutManager crewLayoutManager = new LinearLayoutManager(getApplicationContext());
        crewRecyclerView.setLayoutManager(crewLayoutManager);
        crewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        crewRecyclerView.setAdapter(crewAdapter);

        findViewById(R.id.appbar).setVisibility(View.GONE);
        findViewById(R.id.collapsing_toolbar).setVisibility(View.GONE);
        findViewById(R.id.scroll_view).setVisibility(View.GONE);
        findViewById(R.id.fab).setVisibility(View.GONE);


        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm uiRealm = ((MyApplication) getApplication()).getUiRealm();
                uiRealm.beginTransaction();
                //JSONMovie movieToAdd = uiRealm.createObject(movie);
                uiRealm.copyToRealm(movie);
                uiRealm.commitTransaction();

                Snackbar.make(v, "Added to watch list!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.fab).setVisibility(View.GONE);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }

    private void addByteArray(byte[] image) {
        movie.setBackdropBitmap(image);
    }

    private void updateUI(JSONMovie movie){
        Log.d("Update UI", "1");

        this.movie = movie;
        // Set Collapsing Toolbar layout to the screen
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Set title of Detail page
        collapsingToolbar.setTitle(movie.getTitle());
        Log.d("Update UI", "2");

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
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        image.setImageBitmap(bitmap);
                        int defaultColor = 0x000000;
                        int vibrantColor = palette.getVibrantColor(defaultColor);
                        collapsingToolbar.setBackgroundColor(vibrantColor);
                        collapsingToolbar.setContentScrimColor(vibrantColor);
                        collapsingToolbar.setStatusBarScrimColor(vibrantColor);
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };


        Picasso.with(this)
                .load(movie.getBackdropURL())
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into(image, new PaletteTransformation.PaletteCallback(image) {
                    @Override public void onSuccess(Palette palette) {
                        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap(); // Ew!

                        findViewById(R.id.appbar).setVisibility(View.VISIBLE);
                        findViewById(R.id.collapsing_toolbar).setVisibility(View.VISIBLE);
                        findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.fab).setVisibility(View.VISIBLE);
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        // TODO apply palette to text views, backgrounds, etc.
                        Log.d("Picasso", "pallete");

                        int defaultColor = 0x000000;
                        int vibrantColor = palette.getVibrantColor(defaultColor);

                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                        RatingBar stars = (RatingBar) findViewById(R.id.rating);
                        TextView plotTitle = (TextView) findViewById(R.id.plot_title);
                        TextView castTitle = (TextView) findViewById(R.id.cast_title);
                        TextView crewTitle = (TextView) findViewById(R.id.crew_title);

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

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        addByteArray(stream.toByteArray());
                    }

                    @Override
                    public void onError() {
                        //TODO
                    }
                });

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


        // Populate cast and crew recycler views
        castRecyclerView.setAdapter( new CastAdapter(movie.getCast(), getApplicationContext()));
        crewRecyclerView.setAdapter( new CastAdapter(movie.getCrew(), getApplicationContext()));
        castRecyclerView.setFocusable(false);
        crewRecyclerView.setFocusable(false);

        findViewById(R.id.collapsing_toolbar).setVisibility(View.VISIBLE);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, JSONMovie> {
        private JSONMovie resp;
        ProgressDialog progressDialog;

        @Override
        protected JSONMovie doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                Response movieResponse = null;
                Response castResponse = null;

                OkHttpClient client = new OkHttpClient();

                Request movieRequest = new Request.Builder()
                        .url("https://api.themoviedb.org/3/movie/" + movieID + "?api_key=788bf2d4d9f5db03979efed58cbf6713")
                        .build();

                movieResponse = client.newCall(movieRequest).execute();
                JSONMovie movie = new JSONMovie();
                JSONObject reader = new JSONObject(movieResponse.body().string());

                movie.setTitle(reader.get("title").toString());
                movie.setTagline(reader.get("tagline").toString());
                movie.setOverview(reader.get("overview").toString());
                movie.setBackdropURL("https://image.tmdb.org/t/p/w780//" + reader.get("backdrop_path").toString());
                movie.setPopularity((Double)reader.get("popularity"));
                movie.setVote_average((Double) reader.get("vote_average"));
                movie.setPosterURL(reader.get("poster_path").toString());
                movie.setId((Integer) reader.get("id"));
                movie.setReleaseDate(reader.get("release_date").toString());
                movie.setRuntime((Integer) reader.get("runtime"));

                Request castRequest = new Request.Builder()
                        .url("https://api.themoviedb.org/3/movie/" + movieID + "/credits?api_key=788bf2d4d9f5db03979efed58cbf6713")
                        .build();

                castResponse = client.newCall(castRequest).execute();

                // Populate Cast
                JSONObject castJSON = new JSONObject(castResponse.body().string());
                JSONArray array = castJSON.getJSONArray("cast");
                RealmList<JSONCast> cast = new RealmList<JSONCast>();
                Integer numberOfCast = 3;
                for(int i=0; i < numberOfCast; i++){
                    JSONObject movieJSON = array.getJSONObject(i);
                    JSONCast castMember = new JSONCast();

                    castMember.setCharacterName(movieJSON.get("character").toString());
                    castMember.setActorName(movieJSON.get("name").toString());
                    if (!movieJSON.get("profile_path").toString().equals("null")){
                        castMember.setImagePath("https://image.tmdb.org/t/p/w185/" + movieJSON.get("profile_path").toString());
                    }
                    cast.add(castMember);
                };
                movie.setCast(cast);
                Log.d("In Backgroud", "add cast");

                // Populate Crew
                Log.d("In Backgroud", "before crew array");

                JSONArray crewArray = castJSON.getJSONArray("crew");
                Log.d("In Backgroud", "after crew array");

                RealmList<JSONCast> crew = new RealmList<>();
                for(int i=0; i < numberOfCast; i++){
                    JSONObject movieJSON = crewArray.getJSONObject(i);
                    JSONCast crewMember = new JSONCast();

                    crewMember.setCharacterName(movieJSON.get("name").toString());
                    crewMember.setActorName(movieJSON.get("job").toString());
                    if (!movieJSON.get("profile_path").toString().equals("null")){
                        crewMember.setImagePath("https://image.tmdb.org/t/p/w185/" + movieJSON.get("profile_path").toString());
                    }

                    crew.add(crewMember);
                };
                movie.setCrew(crew);


                resp = movie;
            } catch (Exception t) {
                t.printStackTrace();
                resp = null;
            }
            return resp;
        }

        @Override
        protected void onPostExecute(JSONMovie result) {
            // execution of result of Long time consuming operation
            //progressDialog.dismiss();
            Log.d("Popular movies On Post", result.getTitle());
            updateUI(result);
        }

        @Override
        protected void onPreExecute() {
            /*progressDialog = ProgressDialog.show(getApplicationContext(),
                    "ProgressDialog",
                    "Wait for ");*/
        }
    }
}

