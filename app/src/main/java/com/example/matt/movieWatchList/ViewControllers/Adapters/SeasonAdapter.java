/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.matt.movieWatchList.viewControllers.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AndroidException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.shows.Episode;
import com.example.matt.movieWatchList.Models.POJO.shows.TVShowSeasonResult;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.ExpandableItemIndicator;
import com.example.matt.movieWatchList.uitls.PaletteTransformation;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class SeasonAdapter
        extends AbstractExpandableItemAdapter<SeasonAdapter.MyGroupViewHolder, SeasonAdapter.MyChildViewHolder> {
    private static final String TAG = "MyExpandableItemAdapter";
    private List<TVShowSeasonResult> seasons;
    private Context context;
    private int vibrantColor;
    private int mutedColor;

    public SeasonAdapter(List<TVShowSeasonResult> seasons, int vibrantColor, int mutedColor) {
        this.seasons = seasons;
        this.vibrantColor = vibrantColor;
        this.mutedColor = mutedColor;

        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    //private AbstractExpandableDataProvider mProvider;

    @Override
    public int getGroupCount() {
        return seasons.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return seasons.get(groupPosition).getEpisodes().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return seasons.get(groupPosition).getSeasonNumber();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return seasons.get(groupPosition).getEpisodes().get(childPosition).getEpisodeNumber();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.season_group_item, parent, false);
        return new MyGroupViewHolder(v);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.season_episode_item, parent, false);
        return new MyChildViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(final MyGroupViewHolder holder, int groupPosition, int viewType) {
        TVShowSeasonResult curSeason = seasons.get(groupPosition);

        // set text
        holder.mSeasonName.setText("Season" + curSeason.getSeasonNumber());
        holder.mNumberOfEpisodes.setText(curSeason.getEpisodes().size() + " Episodes");
        holder.mEpisodeProgress.getProgressDrawable().setColorFilter(
                vibrantColor, android.graphics.PorterDuff.Mode.SRC_IN);
        Random r = new Random();
        int i = r.nextInt(100);
        holder.mEpisodeProgress.setProgress(i);
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w92/" + curSeason.getPosterPath()) // w92, w154, w185
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into(holder.mSeasonPoster);

        holder.mWatchSeason.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                holder.mWatchSeason.setColor(vibrantColor);
            }
        });

        // mark as clickable
        holder.itemView.setClickable(true);

        // set background resource (target view ID: container)
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;
            boolean isExpanded;
            boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.bg_group_item_expanded_state;
                isExpanded = true;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
                isExpanded = false;
            }

            //holder.mContainer.setBackgroundResource(bgResId);
            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
    }

    @Override
    public void onBindChildViewHolder(final MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // set text
        Episode curEpisode = seasons.get(groupPosition).getEpisodes().get(childPosition);
        holder.mEpisodeName.setText(curEpisode.getName());
        holder.mEpisodeDescription.setText(curEpisode.getOverview());
        holder.mEpisodeNumber.setText("S0" + curEpisode.getEpisodeNumber() + "E0" + curEpisode.getEpisodeNumber());
        holder.mEpisodeNumber.setTextColor(vibrantColor);
        holder.mEpisodeAirDate.setText("Aired on " + curEpisode.getAirDate());

        // set background resource (target view ID: container)
        int bgResId;
        bgResId = R.drawable.bg_item_normal_state;
        holder.mContainer.setBackgroundResource(bgResId);


        holder.mWatchEpisode.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                holder.mWatchEpisode.setColor(vibrantColor);
            }
        });
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {
    }

    public static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        public RelativeLayout mContainer;
        public TextView mSeasonName;
        public TextView mNumberOfEpisodes;
        public ImageView mSeasonPoster;
        public TextView mEpisodeName;
        public ProgressBar mEpisodeProgress;
        public IconicsImageView mWatchSeason;
        public IconicsImageView mWatchEpisode;
        public TextView mEpisodeDescription;
        public TextView mEpisodeAirDate;
        public TextView mEpisodeNumber;


        public MyBaseViewHolder(View v) {
            super(v);
            mContainer = (RelativeLayout) v.findViewById(R.id.container);
            mSeasonName = (TextView) v.findViewById(R.id.season_name);
            mNumberOfEpisodes = (TextView) v.findViewById(R.id.number_of_episodes);
            mSeasonPoster = (ImageView) v.findViewById(R.id.season_poster);
            mEpisodeName = (TextView) v.findViewById(R.id.episode_name);
            mEpisodeProgress = (ProgressBar) v.findViewById(R.id.episode_progress);
            mWatchSeason = (IconicsImageView) v.findViewById(R.id.watch_season);
            mEpisodeDescription = (TextView) v.findViewById(R.id.episode_description);
            mEpisodeNumber = (TextView) v.findViewById(R.id.episode_number);
            mEpisodeAirDate = (TextView) v.findViewById(R.id.episode_air_date);
            mWatchEpisode = (IconicsImageView) v.findViewById(R.id.watch_episode);
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        public ExpandableItemIndicator mIndicator;

        public MyGroupViewHolder(View v) {
            super(v);
            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View v) {
            super(v);
        }
    }
}
