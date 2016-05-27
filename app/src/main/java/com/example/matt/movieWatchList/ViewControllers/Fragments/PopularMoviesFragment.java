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
import android.widget.TextView;

import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.ViewControllers.Activities.DetailActivity;

import java.util.ArrayList;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Provides UI for the view with Cards.
 */
public class PopularMoviesFragment extends Fragment {
    private ArrayList<MovieDb> popularMovies;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //String apiKey = "788bf2d4d9f5db03979efed58cbf6713";
        //TmdbApi tmdb = new TmdbApi(apiKey);
        Log.d("POPULAR MOVIES", "THIS");
        popularMovies = new ArrayList<MovieDb>();

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        ContentAdapter adapter = new ContentAdapter(popularMovies);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerView = recyclerView;
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final LayoutInflater inflater, ViewGroup parent, final ArrayList<MovieDb> movieList) {
            super(inflater.inflate(R.layout.item_card, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    //Movie movie = movieList.get(getAdapterPosition());
                    Intent intent = new Intent(context, DetailActivity.class);
                    //intent.putExtra("movieId", movie.getId());
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
        ArrayList<MovieDb> popularMovies;
        public ContentAdapter(ArrayList<MovieDb> movieList) {
            popularMovies = movieList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, popularMovies);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView title = (TextView) holder.itemView.findViewById(R.id.card_title);
            TextView genre = (TextView) holder.itemView.findViewById(R.id.card_text);
            /*ImageView coverArt = (ImageView) holder.itemView.findViewById(R.id.card_image);

            Bitmap bmp = BitmapFactory.decodeByteArray(movieList.get(position).getImage(), 0, movieList.get(position).getImage().length);
            coverArt.setImageBitmap(bmp);*/

            title.setText(popularMovies.get(position).getTitle());
            genre.setText(popularMovies.get(position).getGenres().toString());
        }

        @Override
        public int getItemCount() {
            return popularMovies.size();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<MovieDb>> {

        private ArrayList<MovieDb> resp;
        ProgressDialog progressDialog;

        @Override
        protected ArrayList<MovieDb> doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                String apiKey = "788bf2d4d9f5db03979efed58cbf6713";
                TmdbApi tmdb = new TmdbApi(apiKey);
                ArrayList<MovieDb> result = (ArrayList<MovieDb>) tmdb.getMovies().getPopularMovies("", 0).getResults();
                resp = result;
            } catch (Exception e) {
                e.printStackTrace();
                resp = null;
            }
            Log.d("doInBackground", resp.toString());
            return resp;
        }


        @Override
        protected void onPostExecute(ArrayList<MovieDb> result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            Log.d("Popular movies On Post", result.toString());
            popularMovies = result;
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getContext(),
                    "ProgressDialog",
                    "Wait for ");
        }
    }
}
