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

package com.example.matt.movieWatchList.viewControllers.fragments.shows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.Realm.JSONEpisode;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.viewControllers.activities.shows.TVShowWatchListDetailActivity;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TVShowWatchListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        boolean isWatched;

        if (getArguments().getInt("watched") == 1) {
            isWatched = true;
        } else {
            isWatched = false;
        }

        ContentAdapter cardAdapter = new ContentAdapter((MyApplication) getActivity().getApplication(), getActivity(), isWatched);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Context mContext;

        public ViewHolder(final LayoutInflater inflater, final ViewGroup parent, final RealmResults<JSONShow> showList, final Realm uiRealm, final ContentAdapter adapter, final boolean isWatched, Context context) {
            super(inflater.inflate(R.layout.tvshow_your_show_card, parent, false));
            mContext = context;

            ImageButton moreOptions = (ImageButton) itemView.findViewById(R.id.more_button);
            moreOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Context wrapper = new ContextThemeWrapper(mContext, R.style.MyPopupMenu);
                    PopupMenu popup = new PopupMenu(wrapper, v);
                    //PopupMenu popup = new PopupMenu(mContext, v);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_your_tv_shows_options, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            JSONShow show = null;

                            switch (item.getItemId()) {
                                case R.id.action_remove:
                                    show = showList.get(getAdapterPosition());

                                    uiRealm.beginTransaction();
                                    //JSONMovie movieToAdd = uiRealm.createObject(movie);
                                    RealmResults<JSONShow> result1 = uiRealm.where(JSONShow.class)
                                            .equalTo("id", show.getId())
                                            .findAll();
                                    RealmResults<JSONEpisode> episodesToClear = uiRealm.where(JSONEpisode.class)
                                            .equalTo("show_id", show.getId())
                                            .findAll();
                                    result1.clear();
                                    episodesToClear.clear();
                                    uiRealm.commitTransaction();
                                    adapter.notifyDataSetChanged();

                                    Snackbar.make(v, "Removed from your shows",
                                            Snackbar.LENGTH_LONG).show();
                                    return true;

                                case R.id.action_mark_show_watched:
                                    show = showList.get(getAdapterPosition());

                                    RealmQuery<JSONEpisode> query = uiRealm.where(JSONEpisode.class);
                                    RealmResults<JSONEpisode> episodes = null;

                                    episodes =  query.equalTo("show_id", show.getId()).findAll();

                                    uiRealm.beginTransaction();
                                    for (int i = 0; i < episodes.size(); i++) {
                                        episodes.get(i).setIsWatched(true);
                                    }
                                    uiRealm.commitTransaction();

                                    //TODO figure out how to notify

                                    Snackbar.make(v, "Show watched!", Snackbar.LENGTH_SHORT);
                                    return true;
                            }
                            return false;

                        }
                    });

                    popup.show();//showing popup menu

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    JSONShow movie = showList.get(getAdapterPosition());
                    Intent intent = new Intent(context, TVShowWatchListDetailActivity.class);
                    Log.d("FRAGMENT SHOW ID", Integer.toString(movie.getId()));
                    intent.putExtra("showID", movie.getId());
                    context.startActivity(intent);
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
        private RealmResults<JSONShow> showList;
        private Activity activity;
        private boolean isWatched;

        public ContentAdapter(MyApplication app, Activity activity, boolean isWatched) {
            uiRealm = app.getUiRealm();
            this.isWatched = isWatched;


            // Build the query looking at all users:
            RealmQuery<JSONShow> query = uiRealm.where(JSONShow.class);

            // Execute the query:
            if (isWatched) {
                RealmResults<JSONShow> shows = query.equalTo("isWatched", true).findAll();
                this.activity = activity;
                showList = shows;
            } else {
                RealmResults<JSONShow> shows = query.findAll();
                this.activity = activity;
                showList = shows;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, showList, uiRealm, this, isWatched, activity.getApplicationContext());
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView title = (TextView) holder.itemView.findViewById(R.id.card_title);
            TextView genre = (TextView) holder.itemView.findViewById(R.id.card_text);
            ImageView coverArt = (ImageView) holder.itemView.findViewById(R.id.card_image);
            TextView episodeProgressText = (TextView) holder.itemView.findViewById(R.id.episodes);


            Bitmap bmp;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            if (showList.get(position).getBackdropBitmap() != null) {
                bmp = BitmapFactory.decodeByteArray(showList.get(position).getBackdropBitmap(), 0, showList.get(position).getBackdropBitmap().length, options);
                coverArt.setImageBitmap(bmp);
            }

            title.setText(showList.get(position).getName());
            title.setText(showList.get(position).getName());
            genre.setText(showList.get(position).getOverview());

            JSONShow show = showList.get(position);
            RealmQuery<JSONEpisode> query = uiRealm.where(JSONEpisode.class);
            RealmResults<JSONEpisode> watchedEpisodes =  query.equalTo("show_id", show.getId()).equalTo("isWatched", true).findAll();
            RealmResults<JSONEpisode> allEpisodes = uiRealm.where(JSONEpisode.class).equalTo("show_id", show.getId()).findAll();

            episodeProgressText.setText(Integer.toString(watchedEpisodes.size()) + "/" + Integer.toString(allEpisodes.size()));
        }

        @Override
        public int getItemCount() {
            return showList.size();
        }
    }
}
