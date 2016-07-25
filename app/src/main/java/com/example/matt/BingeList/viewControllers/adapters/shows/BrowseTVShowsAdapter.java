package com.example.matt.bingeList.viewControllers.adapters.shows;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.Season;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.models.shows.TVShowSeasonResult;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.uitls.BadgeDrawable;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.uitls.TVShowRealmStaticHelper;
import com.example.matt.bingeList.uitls.UniversalStaticHelper;
import com.example.matt.bingeList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.YourShowsDetailActivity;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BrowseTVShowsAdapter extends RecyclerView.Adapter<BrowseTVShowsAdapter.BrowseTVShowsViewHolder> {
    private static final String TAG = BrowseTVShowsAdapter.class.getSimpleName();
    private RealmList<TVShow> mShowList;
    private Context mContext;
    private Realm mUiRealm;
    private TVShow mShow;
    private int mShowId;
    private int viewMode;
    private BrowseTVShowsViewHolder mHolder;
    private int mPosition;

    private  Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            mUiRealm.beginTransaction();
            mShow.setBackdropBitmap(stream.toByteArray());
            mShow.setOnYourShows(true);
            mShow.setDate(new Date());
            mUiRealm.copyToRealmOrUpdate(mShow);
            mUiRealm.commitTransaction();

            FetchSeasonsTask fetchSeasonsTask = new FetchSeasonsTask();
            fetchSeasonsTask.execute(mShowId, mShow.getNumberOfSeasons());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    public BrowseTVShowsAdapter(RealmList<TVShow> showList, Context context, Realm uiRealm) {
        this.mShowList = showList;
        mContext = context;
        mUiRealm = uiRealm;
        setHasStableIds(true);
        viewMode = PreferencesHelper.getRecyclerviewViewType(mContext);
    }

    public void addMoreShows(RealmList<TVShow> additionalShows){
        for (TVShow showToAdd: additionalShows){
            mShowList.add(showToAdd);
        }
        notifyDataSetChanged();
    }
    @Override
    public BrowseTVShowsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = null;
        if (viewMode == ViewType.CARD) {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_more_options_card, viewGroup, false);
        } else if (viewMode == ViewType.COMPACT_CARD){
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_more_options_compact_card, viewGroup, false);
        } else if (viewMode == ViewType.LIST){
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_more_options_list, viewGroup, false);
        } else {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_more_options_card, viewGroup, false);
        }


        return new BrowseTVShowsViewHolder(itemView);
    }

    @Override
    public long getItemId(int position){
        return mShowList.get(position).getId();
    }

    @Override
    public void onBindViewHolder(final BrowseTVShowsViewHolder holder, int position) {
        mShow = mShowList.get(position);

        holder.mWatchedLayout.setVisibility(View.GONE);
        holder.mWatchListLayout.setVisibility(View.GONE);
        String path = mShowList.get(position).getBackdropPath();

        holder.mMoreOptionsButton.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_more_vert).sizeDp(16).color(ContextCompat.getColor(mContext, R.color.button_grey)));

        Picasso.with(mContext)
                .load(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) +  path)
                .error(R.drawable.generic_movie_background)
                .into(holder.mShowImage);

        holder.mShowName.setText(mShowList.get(position).getName());
        // Check the case where the title is too long
        if (viewMode == ViewType.COMPACT_CARD || viewMode == ViewType.LIST) {
            final TextView title = holder.mShowName;
            final TextView description = holder.mShowDescription;

            holder.mShowName.post(new Runnable() {
                @Override
                public void run() {
                    if (title.getLineCount() > 1) {
                        description.setSingleLine();
                    }
                    // Perform any actions you want based on the line count here.
                }
            });
        } else {
            Log.d(TAG, "else");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/tv/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            final TVShowAPI service = retrofit.create(TVShowAPI.class);

            Call<TVShow> call = service.getTVShow(Integer.toString(mShow.getId()));
            call.enqueue(new Callback<TVShow>() {
                @Override
                public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                    Log.d(TAG, "onResponse");


                    if (response.isSuccessful()) {
                        Log.d(TAG, "isSuccessful");

                        mShow.setNetworks(response.body().getNetworks());
                        if (mShow.getNetworks() != null && !mShow.getNetworks().isEmpty()) {
                            holder.mChannelBadge.setImageDrawable(new BadgeDrawable(mContext, mShow.getNetworks().first().getName(), Color.WHITE));
                        }
                    }
                }
                @Override
                public void onFailure(Call<TVShow> call, Throwable t) {
                    Log.d(TAG, "onFailure");
                }
            });
        }
        holder.mShowDescription.setText(mShowList.get(position).getOverview());

        setActionButton(holder, position);
        setListeners(holder, position);
    }

    @Override
    public int getItemCount() {
        return mShowList.size();
    }

    public class BrowseTVShowsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_title)
        TextView mShowName;

        @BindView(R.id.card_image)
        ImageView mShowImage;

        @BindView(R.id.channel_badge)
        ImageView mChannelBadge;

        @BindView(R.id.card_text)
        TextView mShowDescription;

        @BindView(R.id.watched_layout)
        RelativeLayout mWatchedLayout;

        @BindView(R.id.watch_list_layout)
        RelativeLayout mWatchListLayout;

        @BindView(R.id.action_button)
        IconicsButton mActionButton;

        @BindView(R.id.more_button)
        ImageButton mMoreOptionsButton;

        @BindView(R.id.watched_icon)
        ImageView mWatchedIcon;

        @BindView(R.id.watched_overlay_text)
        TextView mWatchedOverlayText;

        @BindView(R.id.watchlist_overlay_text)
        TextView mWatchlistOverlayText;

        @BindView(R.id.watchlist_icon)
        ImageView mWatchlistIcon;

        public BrowseTVShowsViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mShow = mShowList.get(getAdapterPosition());
                    Context context = v.getContext();

                    if (TVShowRealmStaticHelper.isOnYourShows(mShow.getId(), mUiRealm)){
                        Intent intent = new Intent(mContext, YourShowsDetailActivity.class);
                        intent.putExtra(mContext.getString(R.string.showId), mShow.getId());
                        intent.putExtra(mContext.getString(R.string.showTitle), mShow.getName());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, TVShowBrowseDetailActivity.class);
                        intent.putExtra(mContext.getString(R.string.showId), mShow.getId());
                        intent.putExtra(mContext.getString(R.string.showTitle), mShow.getName());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    //HELPERS
    public boolean isOnWatchList(int position){
        return mUiRealm.where(TVShow.class).equalTo("onYourShows", true).equalTo("id", mShowList.get(position).getId()).count() > 0;
    }

    private void setListeners(final BrowseTVShowsViewHolder holder, final int position){
            holder.mMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mShow = mShowList.get(holder.getAdapterPosition());
                    mShowId = mShow.getId();

                    if (TVShowRealmStaticHelper.isOnYourShows(mShowId, mUiRealm)) {
                        Context wrapper = new ContextThemeWrapper(mContext, R.style.MyPopupMenu);
                        PopupMenu popup = new PopupMenu(wrapper, v);
                        popup.getMenuInflater().inflate(R.menu.menu_your_tv_shows_options, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_remove:
                                        mShow = mShowList.get(holder.getAdapterPosition());
                                        String showName = mShow.getName();
                                        int id = mShow.getId();

                                        mUiRealm.beginTransaction();
                                        TVShow TVShowResultsToRemove = mUiRealm.where(TVShow.class)
                                                .equalTo("id", id)
                                                .findFirst();
                                        TVShowResultsToRemove.deleteFromRealm();

                                        RealmResults<Episode> EpisodeResultsToRemove = mUiRealm.where(Episode.class)
                                                .equalTo("show_id", id)
                                                .findAll();
                                        for (int i = 0; i < EpisodeResultsToRemove.size(); i++) {
                                            EpisodeResultsToRemove.get(i).deleteFromRealm();
                                        }

                                        RealmResults<Season> SeasonResultsToRemove = mUiRealm.where(Season.class)
                                                .equalTo("show_id", id)
                                                .findAll();
                                        for (int i = 0; i < SeasonResultsToRemove.size(); i++) {
                                            SeasonResultsToRemove.get(i).deleteFromRealm();
                                        }

                                        mUiRealm.commitTransaction();

                                        Snackbar.make(v, showName + " removed from your shows", Snackbar.LENGTH_LONG).show();

                                        notifyDataSetChanged();

                                        return true;

                                    case R.id.action_mark_all_watched:
                                        mShow = mShowList.get(holder.getAdapterPosition());

                                        mUiRealm.beginTransaction();
                                        RealmList<Season> watchSeasons = mShow.getSeasons();
                                        for (int i = 0; i < watchSeasons.size(); i++) {
                                            RealmResults<Episode> episodes = mUiRealm.where(Episode.class).equalTo("show_id", mShow.getId()).findAll();
                                            for (int j = 0; j < episodes.size(); j++) {
                                                episodes.get(j).setIsWatched(true);
                                            }
                                        }
                                        mUiRealm.commitTransaction();

                                        Snackbar.make(v, "All episodes for " + mShow.getName() + " marked watched!",
                                                Snackbar.LENGTH_SHORT).show();

                                        notifyDataSetChanged();

                                        return true;

                                    case R.id.action_mark_all_unwatched:
                                        mShow = mShowList.get(holder.getAdapterPosition());

                                        mUiRealm.beginTransaction();
                                        RealmList<Season> unwatchSeasons = mShow.getSeasons();
                                        for (int i = 0; i < unwatchSeasons.size(); i++) {
                                            RealmResults<Episode> episodes = mUiRealm.where(Episode.class).equalTo("show_id", mShow.getId()).findAll();
                                            for (int j = 0; j < episodes.size(); j++) {
                                                episodes.get(j).setIsWatched(false);
                                            }
                                        }
                                        mUiRealm.commitTransaction();

                                        Snackbar.make(v, "All episodes for " + mShow.getName() + " marked unwatched!", Snackbar.LENGTH_SHORT).show();

                                        notifyDataSetChanged();

                                        return true;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }else {

                    }
                }
            });


        holder.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mShow = mShowList.get(holder.getAdapterPosition());
                mShowId = mShow.getId();
                if (TVShowRealmStaticHelper.isOnYourShows(mShowId, mUiRealm)){
                    Episode nextEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShow.getId(), mUiRealm);
                    if (nextEpisode != null) {
                        TVShowRealmStaticHelper.watchEpisode(nextEpisode, mUiRealm);
                        setActionButton(holder, position);
                        Snackbar.make(v, "Watched " + UniversalStaticHelper.formatEpisodeTitle(nextEpisode.getSeasonNumber(), nextEpisode.getEpisodeNumber()) + " " + nextEpisode.getName() + "!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(v, "Error Null Show", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    setWatchlistOverlay(holder);

                    mHolder = holder;
                    mPosition = position;

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(mContext.getString(R.string.tv_show_base_url))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    final TVShowAPI service = retrofit.create(TVShowAPI.class);

                    Call<TVShow> call = service.getTVShow(Integer.toString(mShowId));
                    call.enqueue(new Callback<TVShow>() {
                        @Override
                        public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                            if (response.isSuccessful()){
                                mShow = response.body();

                                Snackbar.make(v, mShow.getName() + " Added to your shows!",
                                        Snackbar.LENGTH_LONG).show();

                                Picasso.with(mContext)
                                        .load(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + mShow.getBackdropPath())
                                        .into(mTarget);
                            } else {
                                Snackbar.make(v, "Unable to load show...", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<TVShow> call, Throwable t) {
                            Snackbar.make(v, "Unable to connect to API..", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void setActionButton(BrowseTVShowsViewHolder holder, int position) {
        Log.d(TAG, "setActionButton()");
        if (isOnWatchList(position)) {
            Episode nextEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShow.getId(), mUiRealm);

            if (nextEpisode != null) {
                holder.mActionButton.setText("{gmd_remove_red_eye} " + UniversalStaticHelper.formatEpisodeTitle(nextEpisode.getSeasonNumber(), nextEpisode.getEpisodeNumber()) + " " + nextEpisode.getName());
                holder.mActionButton.setEnabled(true);
                holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
            }
            setWatchlistOverlay(holder);
        } else {
            setNoOverlay(holder);
            holder.mActionButton.setText(mContext.getString(R.string.add_to_your_shows_button));
            holder.mActionButton.setEnabled(true);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
        }
    }

    private void setWatchlistOverlay(BrowseTVShowsViewHolder holder){
        holder.mWatchListLayout.setVisibility(View.VISIBLE);
        holder.mWatchlistIcon.setImageDrawable(new IconicsDrawable(mContext).icon(CommunityMaterial.Icon.cmd_television_guide).sizeDp(24).color(Color.WHITE));
        holder.mWatchlistOverlayText.setText("On your shows!");
        holder.mWatchedLayout.setVisibility(View.INVISIBLE);
    }

    private void setNoOverlay(BrowseTVShowsViewHolder holder){
        holder.mWatchedLayout.setVisibility(View.INVISIBLE);
        holder.mWatchListLayout.setVisibility(View.INVISIBLE);
    }

    public void UpdateRealmSeasons(ArrayList<TVShowSeasonResult> seasons) {
        //add to realm
        Log.d("realm transaction","attempting to add");

        for (TVShowSeasonResult season: seasons) {
            if (season != null) {
                Season curSeason = new Season();

                curSeason.setAirDate(season.getAirDate());
                curSeason.setEpisodeCount(season.getEpisodes().size());
                curSeason.setId(season.getId());
                curSeason.setPosterPath(season.getPosterPath());
                curSeason.setShow_id(mShowId);
                curSeason.setSeasonNumber(season.getSeasonNumber());

                mUiRealm.beginTransaction();
                mUiRealm.copyToRealmOrUpdate(curSeason);

                RealmList<Episode> jsonEpisodeRealmList = season.getEpisodes();
                for (Episode episode : jsonEpisodeRealmList) {
                    episode.setShow_id(mShowId);
                    episode.setIsWatched(false);
                    episode.setSeasonNumber(curSeason.getSeasonNumber());
                    mUiRealm.copyToRealmOrUpdate(episode);
                }
                mUiRealm.commitTransaction();
            }
        }
        setActionButton(mHolder, mPosition);
        notifyDataSetChanged();
    }

    private class FetchSeasonsTask extends AsyncTask<Integer, Integer, ArrayList<TVShowSeasonResult>> {
        protected ArrayList<TVShowSeasonResult> doInBackground(Integer... params) {
            Integer showID = params[0];
            Integer numberOfSeasons = params[1];

            ExecutorService backgroundExecutor = Executors.newFixedThreadPool(numberOfSeasons);

            ArrayList<TVShowSeasonResult> seasons = new ArrayList<>();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/tv/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .callbackExecutor(backgroundExecutor)
                    .build();

            final TVShowAPI service = retrofit.create(TVShowAPI.class);

            for (int i = 1; i <= numberOfSeasons; i++) {
                Call<TVShowSeasonResult> call = service.getSeasons(Integer.toString(showID), Integer.toString(i));
                try {
                    seasons.add(call.execute().body());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return seasons;
        }

        protected void onPostExecute(ArrayList<TVShowSeasonResult> result) {
            UpdateRealmSeasons(result);
        }
    }

}