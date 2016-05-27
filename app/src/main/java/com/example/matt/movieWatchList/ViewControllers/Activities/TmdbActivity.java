package com.example.matt.movieWatchList.ViewControllers.Activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.Movie;
import com.example.matt.movieWatchList.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;


/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class TmdbActivity extends AppCompatActivity {
    Integer movieID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieID = getIntent().getIntExtra("movieId",0);
        System.out.print(movieID);

        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Added to watch list!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void updateUI(MovieDb movie){
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Set title of Detail page
        collapsingToolbar.setTitle(movie.getTitle());

        ImageView image = (ImageView) findViewById(R.id.image);

        //Bitmap bmp = BitmapFactory.decodeByteArray(movieList.get(position).getImage(), 0, movieList.get(position).getImage().length);
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
        //  which implements ImageAware interface)
        String imageUri = "https://image.tmdb.org/t/p/w300//" + movie.getBackdropPath();
        imageLoader.displayImage(imageUri, image);
        // Load image, decode it to Bitmap and return Bitmap to callback
        imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // Do whatever you want with Bitmap
            }
        });
        // Load image, decode it to Bitmap and return Bitmap synchronously
        //Bitmap bmp = imageLoader.loadImageSync(imageUri);


        LinearLayout layout = (LinearLayout) findViewById(R.id.more_info);
        TextView plot = (TextView) layout.findViewById(R.id.plot);
        TextView cast = (TextView) layout.findViewById(R.id.cast);
        TextView crew = (TextView) layout.findViewById(R.id.crew);
        Log.d("Cast", movie.getGenres().get(0).getName());

        plot.setText(movie.getOverview());
        cast.setText(movie.getGenres().get(0).getName());
        crew.setText(movie.getTagline());
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, MovieDb> {

        private MovieDb resp;
        ProgressDialog progressDialog;

        @Override
        protected MovieDb doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                String apiKey = "788bf2d4d9f5db03979efed58cbf6713";
                TmdbApi tmdb = new TmdbApi(apiKey);
                TmdbMovies movies = new TmdbApi(apiKey).getMovies();
                resp = movies.getMovie(movieID, "en");
            } catch (Exception e) {
                e.printStackTrace();
                resp = null;
            }
            Log.d("doInBackground", resp.getTitle());
            return resp;
        }


        @Override
        protected void onPostExecute(MovieDb result) {
            // execution of result of Long time consuming operation
            //progressDialog.dismiss();
            Log.d("Popular movies On Post", result.toString());
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

