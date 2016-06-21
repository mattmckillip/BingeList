package com.example.matt.movieWatchList.viewControllers.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.Realm.JSONEpisode;
import com.example.matt.movieWatchList.Models.Realm.JSONSeason;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.ExpandableItemIndicator;
import com.example.matt.movieWatchList.uitls.PaletteTransformation;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Picasso;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class WatchListSeasonAdapter extends AbstractExpandableItemAdapter<WatchListSeasonAdapter.MyGroupViewHolder, WatchListSeasonAdapter.MyChildViewHolder> {
    private static final String TAG = "MyExpandableItemAdapter";
    private RealmList<JSONSeason> seasons;
    private Context context;
    private int vibrantColor;
    private int mutedColor;
    private Realm uiRealm;
    private JSONEpisode curEpisode;
    private RecyclerViewExpandableItemManager mExpandableItemManager;

    private View.OnClickListener mItemOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickItemView(v);
        }
    };

    public WatchListSeasonAdapter(RecyclerViewExpandableItemManager expandableItemManager, RealmList<JSONSeason> seasons, int vibrantColor, int mutedColor, Realm uiRealm) {
        this.seasons = seasons;
        this.vibrantColor = vibrantColor;
        this.mutedColor = mutedColor;
        this.uiRealm = uiRealm;

        mExpandableItemManager = expandableItemManager;

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
        return seasons.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return seasons.get(groupPosition).getEpisodes().get(childPosition).getId();
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
        return new MyGroupViewHolder(v, mItemOnClickListener);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.season_episode_item, parent, false);
        return new MyChildViewHolder(v, mItemOnClickListener);
    }

    @Override
    public void onBindGroupViewHolder(final MyGroupViewHolder holder, int groupPosition, int viewType) {
        JSONSeason curSeason = seasons.get(groupPosition);

        // set text
        holder.mSeasonName.setText("Season" + curSeason.getSeasonNumber());
        holder.mNumberOfEpisodes.setText(curSeason.getEpisodes().size() + " Episodes");
        holder.mEpisodeProgress.getProgressDrawable().setColorFilter(
                vibrantColor, android.graphics.PorterDuff.Mode.SRC_IN);

        RealmQuery<JSONEpisode> query = uiRealm.where(JSONEpisode.class);
        RealmResults<JSONEpisode> episodes =  query.equalTo("seasonNumber", curSeason.getEpisodes().get(0).getSeasonNumber()).equalTo("isWatched", true).equalTo("season_id", curSeason.getId()).findAll();
        Log.d("Season " + curSeason.getSeasonNumber(), Integer.toString(episodes.size()));
        Log.d("Season " + curSeason.getSeasonNumber(),Double.toString((((double) episodes.size()/(double) curSeason.getEpisodes().size()) * 100.0)));

        holder.mEpisodeProgress.setProgress((int) (((double) episodes.size()/(double) curSeason.getEpisodes().size()) * 100.0));

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
        curEpisode = seasons.get(groupPosition).getEpisodes().get(childPosition);
        holder.mEpisodeName.setText(curEpisode.getName());
        holder.mEpisodeDescription.setText(curEpisode.getOverview());
        holder.mEpisodeNumber.setText("S0" + curEpisode.getEpisodeNumber() + "E0" + curEpisode.getEpisodeNumber());
        holder.mEpisodeNumber.setTextColor(vibrantColor);
        holder.mEpisodeAirDate.setText("Aired on " + curEpisode.getAirDate());

        // set background resource (target view ID: container)
        int bgResId;
        bgResId = R.drawable.bg_item_normal_state;
        holder.mContainer.setBackgroundResource(bgResId);

        if (curEpisode.getIsWatched()) {
            holder.mWatchEpisode.setColor(vibrantColor);
        } else {
            holder.mWatchEpisode.setColor(ContextCompat.getColor(context, R.color.button_grey));
        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }

    void onClickItemView(View v) {
        RecyclerView.ViewHolder vh = RecyclerViewAdapterUtils.getViewHolder(v);
        int flatPosition = vh.getAdapterPosition();

        if (flatPosition == RecyclerView.NO_POSITION) {
            return;
        }

        long expandablePosition = mExpandableItemManager.getExpandablePosition(flatPosition);
        int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        switch (v.getId()) {
            // https://github.com/h6ah4i/android-advancedrecyclerview/blob/122fbd6261c95cde70a5c65eeaa686a6b9de5a48/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e_add_remove/AddRemoveExpandableExampleAdapter.java
            // child item events
            case R.id.watch_episode:
                handleOnClickWatchEpisodeButton(groupPosition, childPosition);
                break;
            default:
                throw new IllegalStateException("Unexpected click event");
        }
    }

    private void handleOnClickWatchEpisodeButton(int groupPosition, int childPosition) {
        curEpisode = seasons.get(groupPosition).getEpisodes().get(childPosition);

        if (curEpisode.getIsWatched()) {
            uiRealm.beginTransaction();
            curEpisode.setIsWatched(false);
            uiRealm.commitTransaction();
        } else {
            uiRealm.beginTransaction();
            curEpisode.setIsWatched(true);
            uiRealm.commitTransaction();
        }
        mExpandableItemManager.notifyChildItemChanged(groupPosition, childPosition);
        mExpandableItemManager.notifyGroupItemChanged(groupPosition);
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

        public MyBaseViewHolder(View v, View.OnClickListener clickListener) {
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

        public MyGroupViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);
            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public IconicsImageView mWatchEpisode;
        public MyChildViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);
            mWatchEpisode = (IconicsImageView) v.findViewById(R.id.watch_episode);
            mWatchEpisode.setOnClickListener(clickListener);
        }
    }
}