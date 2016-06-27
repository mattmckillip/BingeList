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

package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.TVShowSeasonResult;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.ExpandableItemIndicator;
import com.example.matt.bingeList.uitls.PaletteTransformation;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        holder.mEpisodeProgress.setVisibility(View.GONE);

        // set text
        holder.mSeasonName.setText("Season" + curSeason.getSeasonNumber());
        holder.mNumberOfEpisodes.setText(curSeason.getEpisodes().size() + " Episodes");
        holder.mEpisodeProgress.getProgressDrawable().setColorFilter(
                vibrantColor, android.graphics.PorterDuff.Mode.SRC_IN);
        holder.mSeasonAirDate.setText(formatAirDate(curSeason.getAirDate().toString()));

        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w92/" + curSeason.getPosterPath()) // w92, w154, w185
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into(holder.mSeasonPoster);

        holder.mMoreOptions.setVisibility(View.GONE);


        // mark as clickable
        holder.itemView.setClickable(true);

        // set background resource (target view ID: container)
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;
            boolean isExpanded;
            boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }
            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
    }

    @Override
    public void onBindChildViewHolder(final MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // set text
        Episode curEpisode = seasons.get(groupPosition).getEpisodes().get(childPosition);
        holder.mEpisodeName.setText(curEpisode.getName());
        holder.mEpisodeDescription.setText(curEpisode.getOverview());
        holder.mEpisodeNumber.setText(formatEpisodeTitle(curEpisode.getSeasonNumber(), curEpisode.getEpisodeNumber()));
        holder.mEpisodeNumber.setTextColor(vibrantColor);
        holder.mEpisodeAirDate.setText("Aired on " + formatAirDate(curEpisode.getAirDate().toString()));

        holder.mWatchEpisode.setVisibility(View.GONE);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {
    }

    private String formatAirDate(String airDate) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        String date = null;
        try {
            newDate = dateFormater.parse(airDate);
            dateFormater = new SimpleDateFormat("MM/dd/yy");
            date = dateFormater.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
            date = airDate;
        }

        return date;
    }

    private String formatEpisodeTitle(Integer seasonNumber, Integer episodeNumber) {
        String seasonText = "";
        String episodeText = "";

        if (seasonNumber >= 10) {
            seasonText = "S" + Integer.toString(seasonNumber);
        } else {
            seasonText = "S0" + Integer.toString(seasonNumber);
        }

        if (episodeNumber >= 10) {
            episodeText = "E" + Integer.toString(episodeNumber);
        } else {
            episodeText = "E0" + Integer.toString(episodeNumber);
        }
        Log.d("Episode Number", seasonText + episodeText);
        return seasonText + episodeText;
    }

    public static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        public RelativeLayout mContainer;
        public TextView mSeasonName;
        public TextView mNumberOfEpisodes;
        public ImageView mSeasonPoster;
        public TextView mEpisodeName;
        public ProgressBar mEpisodeProgress;
        public IconicsImageView mWatchEpisode;
        public TextView mEpisodeDescription;
        public TextView mSeasonAirDate;
        public TextView mEpisodeNumber;
        public IconicsImageView mMoreOptions;
        public ExpandableItemIndicator mIndicator;
        public TextView mEpisodeAirDate;


        public MyBaseViewHolder(View v) {
            super(v);
            mContainer = (RelativeLayout) v.findViewById(R.id.container);
            mSeasonName = (TextView) v.findViewById(R.id.season_name);
            mNumberOfEpisodes = (TextView) v.findViewById(R.id.number_of_episodes);
            mSeasonAirDate = (TextView) v.findViewById(R.id.season_air_date);
            mSeasonPoster = (ImageView) v.findViewById(R.id.season_poster);
            mEpisodeName = (TextView) v.findViewById(R.id.episode_name);
            mEpisodeProgress = (ProgressBar) v.findViewById(R.id.episode_progress);
            mEpisodeDescription = (TextView) v.findViewById(R.id.episode_description);
            mEpisodeNumber = (TextView) v.findViewById(R.id.episode_number);
            mEpisodeAirDate = (TextView) v.findViewById(R.id.episode_air_date);
            mWatchEpisode = (IconicsImageView) v.findViewById(R.id.watch_episode);
            mMoreOptions = (IconicsImageView) v.findViewById(R.id.more_options);
            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);
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
