/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.matt.movieWatchList.ViewControllers.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.JSONMovie;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.ViewControllers.Activities.DetailActivity;
import com.example.matt.movieWatchList.ViewControllers.Activities.TmdbActivity;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;
import com.example.matt.movieWatchList.uitls.PreCachingLayoutManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Provides UI for the view with Cards.
 */
public class BrowseMoviesFragment extends Fragment {
    private ArrayList<JSONMovie> popularMovies;
    private RecyclerView recyclerView;
    private ContentAdapter adapter;
    private ImageLoaderConfiguration imageLoaderConfig;

    private Integer movieType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieType = getArguments().getInt("movieType");

        popularMovies = new ArrayList<JSONMovie>();
        // Create global configuration and initialize ImageLoader with this config
        imageLoaderConfig = new ImageLoaderConfiguration.Builder(getContext()).build();
        ImageLoader.getInstance().init(imageLoaderConfig);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        adapter = new ContentAdapter(popularMovies);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new PreCachingLayoutManager(getActivity()));
        this.recyclerView = recyclerView;
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final LayoutInflater inflater, ViewGroup parent, final ArrayList<JSONMovie> movieList) {
            super(inflater.inflate(R.layout.item_card, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    JSONMovie movie = movieList.get(getAdapterPosition());
                    Intent intent = new Intent(context, TmdbActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    context.startActivity(intent);
                }
            });

            // Adding Snackbar to Action Button inside card
            Button button = (Button)itemView.findViewById(R.id.action_button);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Action is pressed",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            ImageButton favoriteImageButton =
                    (ImageButton) itemView.findViewById(R.id.favorite_button);
            favoriteImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Added to Favorite",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.share_button);
            shareImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Share article",
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        ArrayList<JSONMovie> popularMovies;
        public ContentAdapter(ArrayList<JSONMovie> movieList) {
            popularMovies = movieList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, popularMovies);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView title = (TextView) holder.itemView.findViewById(R.id.card_title);
            TextView overview = (TextView) holder.itemView.findViewById(R.id.card_text);
            ImageView coverArt = (ImageView) holder.itemView.findViewById(R.id.card_image);

            //Bitmap bmp = BitmapFactory.decodeByteArray(movieList.get(position).getImage(), 0, movieList.get(position).getImage().length);
            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
            //  which implements ImageAware interface)
            String path = popularMovies.get(position).getBackdropURL();

            if (path != null) {
                String imageUri = "https://image.tmdb.org/t/p/w300//" + path;
                imageLoader.displayImage(imageUri, coverArt);
                // Load image, decode it to Bitmap and return Bitmap to callback
                imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
            }

            title.setText(popularMovies.get(position).getTitle());
            overview.setText(popularMovies.get(position).getOverview());
        }

        @Override
        public int getItemCount() {
            return popularMovies.size();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<JSONMovie>> {

        private ArrayList<JSONMovie> resp;
        ProgressDialog progressDialog;

        @Override
        protected ArrayList<JSONMovie> doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                Log.d("TESTING", "1");
                Response response = null;
                OkHttpClient client = new OkHttpClient();

                if (movieType == BrowseMovieType.POPULAR) {
                    Request request = new Request.Builder()
                            .url("http://api.themoviedb.org/3/movie/popular?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
                            .build();
                    response = client.newCall(request).execute();
                    String jsonString = response.body().string();

                    JSONObject reader = new JSONObject(jsonString);
                    JSONArray array = reader.getJSONArray("results");


                    ArrayList<JSONMovie> movieList = new ArrayList<JSONMovie>();
                    for(int i=0; i < array.length(); i++){
                        JSONObject movieJSON = array.getJSONObject(i);
                        JSONMovie movie = new JSONMovie();

                        movie.setTitle(movieJSON.get("title").toString());
                        movie.setOverview(movieJSON.get("overview").toString());
                        movie.setBackdropURL(movieJSON.get("backdrop_path").toString());
                        movie.setId((Integer) movieJSON.get("id"));

                        movieList.add(movie);
                    }


                    resp = movieList;
                }
                else if (movieType == BrowseMovieType.NOW_SHOWING) {
                    Request request = new Request.Builder()
                            .url("http://api.themoviedb.org/3/movie/now_playing?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
                            .build();
                    response = client.newCall(request).execute();
                    String jsonString = response.body().string();

                    JSONObject reader = new JSONObject(jsonString);
                    JSONArray array = reader.getJSONArray("results");


                    ArrayList<JSONMovie> movieList = new ArrayList<JSONMovie>();
                    for(int i=0; i < array.length(); i++){
                        JSONObject movieJSON = array.getJSONObject(i);
                        JSONMovie movie = new JSONMovie();

                        movie.setTitle(movieJSON.get("title").toString());
                        movie.setOverview(movieJSON.get("overview").toString());
                        movie.setBackdropURL(movieJSON.get("backdrop_path").toString());
                        movie.setId((Integer) movieJSON.get("id"));

                        movieList.add(movie);
                    }


                    resp = movieList;
                }

                else if (movieType == BrowseMovieType.TOP_RATED) {
                    Request request = new Request.Builder()
                            .url("http://api.themoviedb.org/3/movie/top_rated?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
                            .build();
                    response = client.newCall(request).execute();
                    String jsonString = response.body().string();

                    JSONObject reader = new JSONObject(jsonString);
                    JSONArray array = reader.getJSONArray("results");


                    ArrayList<JSONMovie> movieList = new ArrayList<JSONMovie>();
                    for(int i=0; i < array.length(); i++){
                        JSONObject movieJSON = array.getJSONObject(i);
                        JSONMovie movie = new JSONMovie();

                        movie.setTitle(movieJSON.get("title").toString());
                        movie.setOverview(movieJSON.get("overview").toString());
                        movie.setBackdropURL(movieJSON.get("backdrop_path").toString());
                        movie.setId((Integer) movieJSON.get("id"));

                        movieList.add(movie);
                    }


                    resp = movieList;
                }

                else if (movieType == BrowseMovieType.NEW_RELEASE) {
                    Request request = new Request.Builder()
                            .url("http://api.themoviedb.org/3/movie/upcoming?language=en&api_key=788bf2d4d9f5db03979efed58cbf6713")
                            .build();
                    response = client.newCall(request).execute();
                    String jsonString = response.body().string();

                    JSONObject reader = new JSONObject(jsonString);
                    JSONArray array = reader.getJSONArray("results");


                    ArrayList<JSONMovie> movieList = new ArrayList<JSONMovie>();
                    for(int i=0; i < array.length(); i++){
                        JSONObject movieJSON = array.getJSONObject(i);
                        JSONMovie movie = new JSONMovie();

                        movie.setTitle(movieJSON.get("title").toString());
                        movie.setOverview(movieJSON.get("overview").toString());
                        movie.setBackdropURL(movieJSON.get("backdrop_path").toString());
                        movie.setId((Integer) movieJSON.get("id"));

                        movieList.add(movie);
                    }


                    resp = movieList;
                }
                else {
                    Log.d("OOPS", "No movie type");
                    resp = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp = new ArrayList<JSONMovie>();
            }
            Log.d("ArrayList", resp.toString());
            return resp;
        }


        @Override
        protected void onPostExecute(ArrayList<JSONMovie> result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            popularMovies = result;
            recyclerView.setAdapter( new ContentAdapter(popularMovies));
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getContext(),
                    "ProgressDialog",
                    "Wait for ");
        }
    }
}
