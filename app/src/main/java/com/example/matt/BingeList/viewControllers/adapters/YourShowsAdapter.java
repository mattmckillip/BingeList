package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.bingeList.models.Cast;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.Crew;
import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.Season;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.models.shows.TVShowSeasonResult;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.uitls.Enums.ShowSort;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.uitls.TVShowRealmStaticHelper;
import com.example.matt.bingeList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.YourShowsDetailActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    public void onBindViewHolder(final YourShowsViewHolder holder, final int position) {
        mShow = mShowList.get(position);

        holder.mProgressSpinner.setVisibility(View.GONE);
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
        /*holder.mMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "moreOptionsButtonClick()");
                if (isOnWatchList(position)) { // Movie is on the users watchlist
                    PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.menu_watchlist, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_archive:
                                    archiveHandler(mMovieList.get(position), position, v);
                                    return true;

                                case R.id.action_remove:
                                    moveFromWatchListToBrowse(mMovieList.get(position).getId());
                                    Snackbar.make(v, mMovieList.get(position).getTitle() + " removed from watchlist", Snackbar.LENGTH_LONG).show();
                                    return true;
                            }
                            return false;
                        }
                    });
                    popup.show();

                } else if (isWatched(position)) { // Movie has been watched
                    PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.menu_watched, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_archive:
                                    archiveHandler(mMovieList.get(position), position, v);
                                    return true;

                                case R.id.action_remove:
                                    moveFromWatchListToBrowse(mMovieList.get(position).getId());
                                    Snackbar.make(v, mMovieList.get(position).getTitle() + " removed from watchlist", Snackbar.LENGTH_LONG).show();
                                    return true;

                                case R.id.action_move_to_watchlist:
                                    moveFromWatchedToWatchList(position);
                                    Snackbar.make(v, mMovieList.get(position).getTitle() + " unwatched", Snackbar.LENGTH_LONG).show();
                                    return true;
                            }
                            return false;
                        }
                    });
                    popup.show();
                } else { // Movie is not on watchlist or watched
                    PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.menu_browse, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_archive:
                                    archiveHandler(mMovieList.get(position), position, v);
                                    return true;

                                case R.id.action_watch:
                                    moveFromBrowseToWatched(position, v);
                                    return true;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            }
        });*/

        holder.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Episode nextEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShow.getId(), mUiRealm);

                if (nextEpisode != null) {
                    TVShowRealmStaticHelper.watchEpisode(nextEpisode, mUiRealm);
                    setActionButton(holder);
                    Snackbar.make(v, "Watched " + formatEpisodeTitle(nextEpisode.getSeasonNumber(), nextEpisode.getEpisodeNumber()) + " " + nextEpisode.getName() + "!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(v, "Error Null Show", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setActionButton(YourShowsViewHolder holder) {
        Episode nextEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShow.getId(), mUiRealm);

        if (nextEpisode != null) {
            Log.d(TAG, "setActionButton - Episode name: " + nextEpisode.getName());
            holder.mActionButton.setText("{gmd_remove_red_eye} " + formatEpisodeTitle(nextEpisode.getSeasonNumber(), nextEpisode.getEpisodeNumber()) + " " + nextEpisode.getName());
            holder.mActionButton.setEnabled(true);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
        } else {
            holder.mActionButton.setText("{gmd_done} all caught up");
            holder.mActionButton.setEnabled(false);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));
        }
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

    public class YourShowsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_title)
        TextView mShowName;

        @BindView(R.id.card_image)
        ImageView mShowImage;

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

        @BindView(R.id.progress_spinner)
        ProgressBar mProgressSpinner;

        @BindView(R.id.watched_icon)
        ImageView mWatchedIcon;

        @BindView(R.id.watchlist_icon)
        ImageView mWatchlistIcon;


        public YourShowsViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);

            mMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
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
                            TVShow show = null;

                            switch (item.getItemId()) {
                                case R.id.action_remove:
                                    show = mShowList.get(getAdapterPosition());
                                    Integer showId = show.getId();

                                    mUiRealm.beginTransaction();
                                    TVShow TVShowResultsToRemove = mUiRealm.where(TVShow.class)
                                            .equalTo("id", showId)
                                            .findFirst();
                                    TVShowResultsToRemove.deleteFromRealm();

                                    mShowList.remove(getAdapterPosition());
                                    notifyDataSetChanged();


                                    /*RealmResults<Episode> EpisodeResultsToRemove = mUiRealm.where(Episode.class)
                                            .equalTo("show_id", show.getId())
                                            .findAll();
                                    for (int i = 0; i < EpisodeResultsToRemove.size(); i++) {
                                        EpisodeResultsToRemove.get(i).deleteFromRealm();
                                    }

                                    RealmResults<Season> SeasonResultsToRemove = mUiRealm.where(Season.class)
                                            .equalTo("show_id", show.getId())
                                            .findAll();
                                    for (int i = 0; i < SeasonResultsToRemove.size(); i++) {
                                        SeasonResultsToRemove.get(i).deleteFromRealm();
                                    }*/

                                    mUiRealm.commitTransaction();
                                    //notifyDataSetChanged(); TODO breaking this

                                    /*Snackbar.make(v, "Removed from your shows",
                                            Snackbar.LENGTH_LONG).show();*/
                                    return true;

                                /*case R.id.action_mark_show_watched:
                                    show = mShowList.get(getAdapterPosition());

                                    RealmQuery<Episode> query = mUiRealm.where(Episode.class);
                                    RealmResults<Episode> episodes = null;

                                    episodes =  query.equalTo("show_id", show.getId()).findAll();

                                    mUiRealm.beginTransaction();
                                    for (int i = 0; i < episodes.size(); i++) {
                                        episodes.get(i).setIsWatched(true);
                                    }
                                    mUiRealm.commitTransaction();

                                    //TODO figure out how to notify

                                    Snackbar.make(v, "Show watched!", Snackbar.LENGTH_SHORT);
                                    return true;*/
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