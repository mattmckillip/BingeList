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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.matt.movieWatchList.Models.JSONCast;
import com.example.matt.movieWatchList.Models.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.ViewControllers.Activities.TmdbActivity;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Provides UI for the view with Cards.
 */
public class MovieWatchListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        ContentAdapter cardAdapter = new ContentAdapter((MyApplication) getActivity().getApplication());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d("Card content","Content");
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final LayoutInflater inflater, ViewGroup parent, final RealmResults<JSONMovie> movieList, final Realm uiRealm,final ContentAdapter adapter) {
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
                    JSONMovie movie = movieList.get(getAdapterPosition());

                    uiRealm.beginTransaction();
                    //JSONMovie movieToAdd = uiRealm.createObject(movie);
                    RealmResults<JSONMovie> result1 = uiRealm.where(JSONMovie.class)
                            .equalTo("title", movie.getTitle())
                            .findAll();
                    result1.clear();
                    uiRealm.commitTransaction();
                    adapter.notifyDataSetChanged();

                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.
        private Realm uiRealm;
        private RealmResults<JSONMovie> movieList;

        public ContentAdapter(MyApplication app) {
            uiRealm = app.getUiRealm();

            // Build the query looking at all users:
            RealmQuery<JSONMovie> query = uiRealm.where(JSONMovie.class);

            // Execute the query:
            RealmResults<JSONMovie> movies = query.findAll();
            movieList = movies;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, movieList, uiRealm, this);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView title = (TextView) holder.itemView.findViewById(R.id.card_title);
            TextView genre = (TextView) holder.itemView.findViewById(R.id.card_text);
            ImageView coverArt = (ImageView) holder.itemView.findViewById(R.id.card_image);

            //coverArt.setImageBitmap(movieList.get(position).getBackdropBitmap());

            title.setText(movieList.get(position).getTitle());
            genre.setText(movieList.get(position).getOverview());
        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }
}
