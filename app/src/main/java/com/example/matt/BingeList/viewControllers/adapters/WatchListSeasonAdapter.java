package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.Season;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.ExpandableItemIndicator;
import com.example.matt.bingeList.uitls.PaletteTransformation;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class WatchListSeasonAdapter extends AbstractExpandableItemAdapter<WatchListSeasonAdapter.MyGroupViewHolder, WatchListSeasonAdapter.MyChildViewHolder> {
    private RealmList<Season> seasons;
    private Context context;
    private int vibrantColor;
    private int mutedColor;
    private Realm uiRealm;
    private Episode curEpisode;
    private Integer showId;
    private RecyclerViewExpandableItemManager mExpandableItemManager;
    private View.OnClickListener mItemOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickItemView(v);
        }
    };

    public WatchListSeasonAdapter(RecyclerViewExpandableItemManager expandableItemManager, RealmList<Season> seasons, int vibrantColor, int mutedColor, Realm uiRealm, Context context) {
        this.seasons = seasons;
        this.vibrantColor = vibrantColor;
        this.mutedColor = mutedColor;
        this.uiRealm = uiRealm;
        this.context = context;
        this.showId = seasons.get(0).getSeasonNumber();

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
        return seasons.get(groupPosition).getEpisodeCount();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return seasons.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return uiRealm.where(Episode.class).equalTo("show_id", showId).findAll().get(childPosition).getId();
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
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.season_group_item, parent, false);
        return new MyGroupViewHolder(v, mItemOnClickListener);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.season_episode_item, parent, false);
        return new MyChildViewHolder(v, mItemOnClickListener);
    }

    @Override
    public void onBindGroupViewHolder(final MyGroupViewHolder holder, int groupPosition, int viewType) {
        Season curSeason = seasons.get(groupPosition);

        // set text
        holder.mSeasonName.setText("Season" + curSeason.getSeasonNumber());
        holder.mNumberOfEpisodes.setText(curSeason.getEpisodeCount() + " Episodes");
        holder.mEpisodeProgress.getProgressDrawable().setColorFilter(
                vibrantColor, android.graphics.PorterDuff.Mode.SRC_IN);
        holder.mSeasonAirDate.setText(formatAirDate(curSeason.getAirDate()));

        RealmQuery<Episode> query = uiRealm.where(Episode.class);
        RealmResults<Episode> episodes =  query.equalTo("seasonNumber", curSeason.getSeasonNumber()).equalTo("isWatched", true).equalTo("season_id", curSeason.getId()).findAll();

        holder.mEpisodeProgress.setProgress((int) (((double) episodes.size()/(double) curSeason.getEpisodeCount()) * 100.0));

        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w92/" + curSeason.getPosterPath()) // w92, w154, w185
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into(holder.mSeasonPoster);

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
        curEpisode =  uiRealm.where(Episode.class).equalTo("show_id", showId).findAll().get(childPosition);
        holder.mEpisodeName.setText(curEpisode.getName());
        holder.mEpisodeDescription.setText(curEpisode.getOverview());
        holder.mEpisodeNumber.setText(formatEpisodeTitle(curEpisode.getSeasonNumber(), curEpisode.getEpisodeNumber()));
        holder.mEpisodeNumber.setTextColor(vibrantColor);
        holder.mEpisodeAirDate.setText("Aired on " + formatAirDate(curEpisode.getAirDate()));

        if (curEpisode.getIsWatched()) {
            holder.mWatchEpisode.setColor(vibrantColor);
        } else {
            holder.mWatchEpisode.setColor(ContextCompat.getColor(context, R.color.button_grey));
        }
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

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return false;
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
            // common events
            case R.id.container:
                if (childPosition == RecyclerView.NO_POSITION) {
                    handleOnClickGroupItemContainerView(groupPosition);
                } else {
                    handleOnClickChildItemContainerView(groupPosition, childPosition);
                }
                break;
            case R.id.more_options:
                handleOnClickGroupItemAddChild2BottomButton(groupPosition, v);
                break;
            case R.id.watch_episode:
                handleOnClickWatchEpisodeButton(groupPosition, childPosition);
                break;
            default:
                throw new IllegalStateException("Unexpected click event");
        }
    }

    private void handleOnClickGroupItemAddChild2BottomButton(final int groupPosition, final View v) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(context, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_tv_season_group_options, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                RealmQuery<Episode> query = uiRealm.where(Episode.class);
                RealmResults<Episode> episodes = null;

                switch (item.getItemId()) {

                    case R.id.action_mark_watched:
                        episodes =  query.equalTo("show_id", showId).equalTo("season_id", seasons.get(groupPosition).getId()).findAll();

                        uiRealm.beginTransaction();
                        for (int i = 0; i < episodes.size(); i++) {
                            episodes.get(i).setIsWatched(true);
                        }
                        uiRealm.commitTransaction();

                        if (isGroupExpanded(groupPosition)) {
                            for (int i = 0; i < episodes.size(); i++) {
                                mExpandableItemManager.notifyChildItemChanged(groupPosition, i);
                            }
                        }
                        mExpandableItemManager.notifyGroupItemChanged(groupPosition);

                        Snackbar.make(v, "Season watched!", Snackbar.LENGTH_SHORT);
                        return true;

                    case R.id.action_mark_unwatched:
                        episodes =  query.equalTo("show_id", showId).equalTo("season_id", seasons.get(groupPosition).getId()).findAll();
                        uiRealm.beginTransaction();

                        for (int i = 0; i < episodes.size(); i++) {
                            episodes.get(i).setIsWatched(false);
                        }
                        uiRealm.commitTransaction();

                        if (isGroupExpanded(groupPosition)) {
                            for (int i = 0; i < episodes.size(); i++) {
                                mExpandableItemManager.notifyChildItemChanged(groupPosition, i);
                            }
                        }

                        mExpandableItemManager.notifyGroupItemChanged(groupPosition);
                        Snackbar.make(v, "Season unwatched!", Snackbar.LENGTH_SHORT);

                        return true;
                }
                return false;

            }
        });

        popup.show();//showing popup menu
        //mExpandableItemManager.notifyChildItemRangeInserted(groupPosition, childCount, 2);
    }

    private void handleOnClickGroupItemContainerView(int groupPosition) {
        // toggle expanded/collapsed
        if (isGroupExpanded(groupPosition)) {
            mExpandableItemManager.collapseGroup(groupPosition);
        } else {
            mExpandableItemManager.expandGroup(groupPosition);
        }
    }

    private void handleOnClickChildItemContainerView(int groupPosition, int childPosition) {
    }


    private void handleOnClickWatchEpisodeButton(int groupPosition, int childPosition) {
        curEpisode =  uiRealm.where(Episode.class).equalTo("show_id", showId).findAll().get(childPosition);

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
        public TextView mSeasonAirDate;
        public ImageView mSeasonPoster;
        public TextView mEpisodeName;
        public ProgressBar mEpisodeProgress;
        public IconicsImageView mWatchEpisode;
        public TextView mEpisodeDescription;
        public TextView mEpisodeAirDate;
        public TextView mEpisodeNumber;
        public IconicsImageView mMoreOptions;
        public ExpandableItemIndicator mIndicator;

        public MyBaseViewHolder(View v, View.OnClickListener clickListener) {
            super(v);
            mContainer = (RelativeLayout) v.findViewById(R.id.container);
            mSeasonName = (TextView) v.findViewById(R.id.season_name);
            mSeasonAirDate = (TextView) v.findViewById(R.id.season_air_date);
            mNumberOfEpisodes = (TextView) v.findViewById(R.id.number_of_episodes);
            mSeasonPoster = (ImageView) v.findViewById(R.id.season_poster);
            mEpisodeName = (TextView) v.findViewById(R.id.episode_name);
            mEpisodeProgress = (ProgressBar) v.findViewById(R.id.episode_progress);
            mEpisodeDescription = (TextView) v.findViewById(R.id.episode_description);
            mEpisodeNumber = (TextView) v.findViewById(R.id.episode_number);
            mEpisodeAirDate = (TextView) v.findViewById(R.id.episode_air_date);
            mWatchEpisode = (IconicsImageView) v.findViewById(R.id.watch_episode);
            mMoreOptions = (IconicsImageView) v.findViewById(R.id.more_options);
            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);

            mContainer.setOnClickListener(clickListener);
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        public ExpandableItemIndicator mIndicator;

        public MyGroupViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);
            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);
            mMoreOptions.setOnClickListener(clickListener);
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);
            mWatchEpisode.setOnClickListener(clickListener);
        }
    }

    private boolean isGroupExpanded(int groupPosition) {
        return mExpandableItemManager.isGroupExpanded(groupPosition);
    }
}