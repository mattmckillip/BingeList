package com.example.matt.movieWatchList.ViewControllers.Activities;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;


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
    JSONMovie movie;
    private RealmList<JSONCast> castList = new RealmList<>();
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieID = getIntent().getIntExtra("movieId",0);
        System.out.print(movieID);

        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // Cast recycler view
        castRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        castAdapter = new CastAdapter(castList);
        castRecyclerView.setLayoutManager(mLayoutManager);
        castRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castRecyclerView.setAdapter(castAdapter);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();


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
    }


    private void updateUI(JSONMovie movie){
        this.movie = movie;
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Set title of Detail page
        collapsingToolbar.setTitle(movie.getTitle());
        final ImageView image = (ImageView) findViewById(R.id.image);

        //Bitmap bmp = BitmapFactory.decodeByteArray(movieList.get(position).getImage(), 0, movieList.get(position).getImage().length);
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
        //  which implements ImageAware interface)
        String imageUri = "https://image.tmdb.org/t/p/w300//" + movie.getBackdropURL();

        imageLoader.displayImage(imageUri, image);
        // Load image, decode it to Bitmap and return Bitmap to callback
        imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // Do whatever you want with Bitmap
                thisBitmap = loadedImage;
                Log.d("In call back", "loading image");

                if (thisBitmap != null && !thisBitmap.isRecycled()) {
                    int defaultColor = 0x000000;
                    Palette palette = Palette.from(thisBitmap).generate();
                    NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                    RatingBar stars = (RatingBar) findViewById(R.id.rating);

                    //nestedScrollView.setBackgroundColor(palette.getLightMutedColor(defaultColor));
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantColor(defaultColor)));

                    int vibrantColor = palette.getVibrantColor(defaultColor);

                    LayerDrawable starProgressDrawable = (LayerDrawable) stars.getProgressDrawable();
                    starProgressDrawable.getDrawable(2).setColorFilter(palette.getMutedColor(defaultColor), PorterDuff.Mode.SRC_ATOP);
                    starProgressDrawable.getDrawable(1).setColorFilter(palette.getMutedColor(defaultColor), PorterDuff.Mode.SRC_ATOP);
                    //starProgressDrawable.getDrawable(0).setColorFilter(vibrantColor, PorterDuff.Mode.SRC_ATOP);

                    collapsingToolbar.setBackgroundColor(vibrantColor);
                    collapsingToolbar.setContentScrimColor(vibrantColor);
                    collapsingToolbar.setStatusBarScrimColor(vibrantColor);
                }
                else {
                    Log.d("PAllete", "Bitmap Null");
                }
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.more_info);
        ExpandableTextView plot = (ExpandableTextView) findViewById(R.id.expand_text_view);
        TextView popularity = (TextView) layout.findViewById(R.id.poularity);
        RatingBar stars = (RatingBar) layout.findViewById(R.id.rating);

        castRecyclerView.setAdapter( new CastAdapter(movie.getCast()));

        plot.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });
        plot.setText(movie.getOverview());
        popularity.setText(Double.toString(Math.ceil(movie.getPopularity()))+"/100");
        stars.setRating(movie.getVote_average().floatValue());

        castList = movie.getCast();
        castAdapter.notifyDataSetChanged();
    }

    private static class ViewHolder {
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
                Log.d("reader", reader.toString());
                movie.setTitle(reader.get("title").toString());
                movie.setTagline(reader.get("tagline").toString());
                movie.setOverview(reader.get("overview").toString());
                movie.setBackdropURL(reader.get("backdrop_path").toString());
                movie.setPopularity((Double)reader.get("popularity"));
                movie.setVote_average((Double) reader.get("vote_average"));
                movie.setPosterURL(reader.get("poster_path").toString());

                Request castRequest = new Request.Builder()
                        .url("https://api.themoviedb.org/3/movie/" + movieID + "/credits?api_key=788bf2d4d9f5db03979efed58cbf6713")
                        .build();

                castResponse = client.newCall(castRequest).execute();
                JSONObject castJSON = new JSONObject(castResponse.body().string());

                JSONArray array = castJSON.getJSONArray("cast");

                RealmList<JSONCast> cast = new RealmList<JSONCast>();
                for(int i=0; i < array.length(); i++){
                    JSONObject movieJSON = array.getJSONObject(i);
                    JSONCast castMember = new JSONCast();

                    castMember.setCharacterName(movieJSON.get("character").toString());
                    castMember.setActorName(movieJSON.get("name").toString());
                    castMember.setImagePath("https://image.tmdb.org/t/p/w185/" + movieJSON.get("profile_path").toString());

                    cast.add(castMember);
                };
                movie.setCast(cast);

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

