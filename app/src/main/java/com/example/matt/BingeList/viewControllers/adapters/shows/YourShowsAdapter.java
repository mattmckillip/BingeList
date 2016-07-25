package com.example.matt.bingeList.viewControllers.adapters.shows;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.BadgeDrawable;
import com.example.matt.bingeList.uitls.Enums.ShowSort;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.uitls.TVShowRealmStaticHelper;
import com.example.matt.bingeList.uitls.UniversalStaticHelper;
import com.example.matt.bingeList.viewControllers.activities.shows.YourShowsDetailActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Matt on 6/12/2016.
 */
public class YourShowsAdapter extends RecyclerView.Adapter<YourShowsAdapter.YourShowsViewHolder> {
    private static final String TAG = BrowseTVShowsAdapter.class.getSimpleName();
    private RealmList<TVShow> mShowList;
    private Context mContext;
    private Realm mUiRealm;
    private TVShow mShow;
    private int viewMode;
    private boolean mUnWatchedEpisodes;

    public YourShowsAdapter(RealmList<TVShow> showList, Context context, Realm uiRealm, boolean unWatchedEpisodes) {
        this.mShowList = showList;
        mContext = context;
        mUiRealm = uiRealm;
        mUnWatchedEpisodes = unWatchedEpisodes;
        setHasStableIds(true);
        viewMode = PreferencesHelper.getRecyclerviewViewType(mContext);
    }

    @Override
    public YourShowsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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

        return new YourShowsViewHolder(itemView);
    }

    @Override
    public long getItemId(int position){
        return mShowList.get(position).getId();
    }

    @Override
    public void onBindViewHolder(final YourShowsViewHolder holder, int position) {
        mShow = mShowList.get(position);

        holder.mWatchedLayout.setVisibility(View.GONE);
        holder.mWatchListLayout.setVisibility(View.GONE);
        holder.mShowName.setVisibility(View.GONE);

        holder.mMoreOptionsButton.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_more_vert).sizeDp(16).color(ContextCompat.getColor(mContext, R.color.button_grey)));

        Bitmap bmp;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        if (mShowList.get(position).getBackdropBitmap() != null) {
            bmp = BitmapFactory.decodeByteArray(mShowList.get(position).getBackdropBitmap(), 0, mShowList.get(position).getBackdropBitmap().length, options);
            holder.mShowImage.setImageBitmap(bmp);
        }

        holder.mShowName.setText(mShowList.get(position).getName());

        // Check the case where the title is too long
        if (viewMode == ViewType.COMPACT_CARD || viewMode == ViewType.LIST) {
            final TextView title = holder.mShowName;
            final TextView description = holder.mShowDecsiption;

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
            if (mShow.getNetworks() != null && !mShow.getNetworks().isEmpty()) {
                holder.mChannelBadge.setImageDrawable(new BadgeDrawable(mContext, mShow.getNetworks().first().getName(), Color.WHITE));
            }
        }

        holder.mShowDecsiption.setText(mShowList.get(position).getOverview());

        holder.mShowName.setVisibility(View.VISIBLE);

        setActionButton(holder);
        setListeners(holder, position);
    }

    @Override
    public int getItemCount() {
        return mShowList.size();
    }

    //HELPERS

    public void sort(int sortType) {
        if (mUnWatchedEpisodes){
            mShowList = TVShowRealmStaticHelper.getSortedShowsWithUnwatchedEpisodes(mUiRealm, sortType);
        } else {
            RealmResults<TVShow> tvShowRealmResults = null;
            if (sortType == ShowSort.RECENTLY_ADDED) {
                tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.DESCENDING);
            } else if (sortType == ShowSort.TOP_RATED) {
                tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("voteAverage", Sort.DESCENDING);
            } else if (sortType == ShowSort.ADDED_FIRST) {
                tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.ASCENDING);
            } else {
                tvShowRealmResults = mUiRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.DESCENDING);
            }

            mShowList = new RealmList<>();
            for (TVShow tvShowResult : tvShowRealmResults) {
                mShowList.add(tvShowResult);
            }
        }
        notifyDataSetChanged();
    }

    private void setListeners(final YourShowsViewHolder holder, final int position){
        holder.mMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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
                                        .equalTo("id",id)
                                        .findFirst();
                                TVShowResultsToRemove.deleteFromRealm();

                                mShowList.remove(holder.getAdapterPosition());

                                RealmResults<Episode> EpisodeResultsToRemove = mUiRealm.where(Episode.class)
                                        .equalTo("show_id",id)
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
            }
        });

        holder.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mShow = mShowList.get(holder.getAdapterPosition());

                Episode nextEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShow.getId(), mUiRealm);

                if (nextEpisode != null) {
                    TVShowRealmStaticHelper.watchEpisode(nextEpisode, mUiRealm);
                    setActionButton(holder);
                    Snackbar.make(v, "Watched " + UniversalStaticHelper.formatEpisodeTitle(nextEpisode.getSeasonNumber(), nextEpisode.getEpisodeNumber()) + " " + nextEpisode.getName() + "!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(v, "Error Null Show", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setActionButton(YourShowsViewHolder holder) {
        Episode nextEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShow.getId(), mUiRealm);

        if (nextEpisode != null) {
            holder.mActionButton.setText("{gmd_remove_red_eye} " + UniversalStaticHelper.formatEpisodeTitle(nextEpisode.getSeasonNumber(), nextEpisode.getEpisodeNumber()) + " " + nextEpisode.getName());
            holder.mActionButton.setEnabled(true);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
        } else {
            holder.mActionButton.setText("{gmd_done} all caught up");
            holder.mActionButton.setEnabled(false);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));
        }
    }



    public class YourShowsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_title)
        TextView mShowName;

        @BindView(R.id.card_image)
        ImageView mShowImage;

        @BindView(R.id.channel_badge)
        ImageView mChannelBadge;

        @BindView(R.id.card_text)
        TextView mShowDecsiption;

        @BindView(R.id.watched_layout)
        RelativeLayout mWatchedLayout;

        @BindView(R.id.watch_list_layout)
        RelativeLayout mWatchListLayout;

        @BindView(R.id.more_button)
        ImageButton mMoreOptionsButton;

        @BindView(R.id.action_button)
        IconicsButton mActionButton;

        @BindView(R.id.watched_icon)
        ImageView mWatchedIcon;

        @BindView(R.id.watchlist_icon)
        ImageView mWatchlistIcon;


        public YourShowsViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    TVShow movie = mShowList.get(getAdapterPosition());
                    Intent intent = new Intent(context, YourShowsDetailActivity.class);
                    intent.putExtra(context.getString(R.string.showId), movie.getId());
                    context.startActivity(intent);
                }
            });


        }
    }
    /*public void UpdateRealmSeasons(ArrayList<TVShowSeasonResult> seasons, Integer showID) {
        RealmList<JSONSeason> jsonSeasonRealmList = new RealmList<>();
        for (TVShowSeasonResult season: seasons) {
            JSONSeason realmSeason = season.convertToRealm();
            jsonSeasonRealmList.add(realmSeason);

            RealmList<JSONEpisode> jsonEpisodeRealmList = realmSeason.getEpisodes();
            for (JSONEpisode episode: jsonEpisodeRealmList) {
                episode.setShow_id(showID);
            }
        }

        Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();
        uiRealm.beginTransaction();
        realmShow.setSeasons(jsonSeasonRealmList);
        uiRealm.copyToRealmOrUpdate(realmShow);
        uiRealm.commitTransaction();
    }

    private class FetchSeasonsTask extends AsyncTask<Integer, Integer, ArrayList<TVShowSeasonResult>> {
        private Integer showID;
        protected ArrayList<TVShowSeasonResult> doInBackground(Integer... params) {
            showID = params[0];
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
            UpdateRealmSeasons(result, showID);
        }
    }*/
}