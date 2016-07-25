package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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

import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.MultiSearchResult;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.Season;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.shows.TVShowSeasonResult;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.activities.movies.BrowseMovieDetailActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MultiSearchAdapter extends RecyclerView.Adapter<MultiSearchAdapter.SearchViewHolder> {
    private static final String TAG = MultiSearchAdapter.class.getName();
    private static final String MOVIE_TYPE = "movie";
    private static final String SHOW_TYPE = "tv";

    private List<MultiSearchResult> mMultiSearchResults;
    private Context mContext;
    private Realm mUiRealm;

    private Movie mMovie;
    private TVShow mShow;
    private Credits mCredits;

    private int viewMode;

    private Target browseToWatchlistTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            mUiRealm.beginTransaction();
            mMovie.setBackdropBitmap(stream.toByteArray());
            mMovie.setOnWatchList(true);
            mUiRealm.copyToRealmOrUpdate(mMovie);
            mUiRealm.copyToRealmOrUpdate(mCredits);
            mUiRealm.commitTransaction();

            notifyDataSetChanged();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    public MultiSearchAdapter(List<MultiSearchResult> results, Context context, Realm uiRealm) {
        mMultiSearchResults = results;
        mContext = context;
        mUiRealm = uiRealm;
        viewMode = PreferencesHelper.getRecyclerviewViewType(mContext);
    }

    @Override
    public int getItemCount() {
        return mMultiSearchResults.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder searchViewHolder, int position) {
        MultiSearchResult result = mMultiSearchResults.get(position);

        searchViewHolder.mWatchedLayout.setVisibility(View.GONE);
        searchViewHolder.mWatchListLayout.setVisibility(View.GONE);

        if (result.getMediaType().equals(MOVIE_TYPE)) {
            searchViewHolder.mMediaTitle.setText(result.getTitle());
            searchViewHolder.mMediaDescription.setText(result.getOverview());

            // Check the case where the title is too long
            if (viewMode == ViewType.COMPACT_CARD || viewMode == ViewType.LIST) {
                final TextView title = searchViewHolder.mMediaTitle;
                final TextView description = searchViewHolder.mMediaDescription;

                searchViewHolder.mMediaTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (title.getLineCount() > 1) {
                            description.setSingleLine();
                        }
                        // Perform any actions you want based on the line count here.
                    }
                });
            }
        } else if (result.getMediaType().equals(SHOW_TYPE)) {
            searchViewHolder.mMediaTitle.setText(result.getName());
            searchViewHolder.mMediaDescription.setText(result.getOverview());

            // Check the case where the title is too long
            if (viewMode == ViewType.COMPACT_CARD || viewMode == ViewType.LIST) {
                final TextView title = searchViewHolder.mMediaTitle;
                final TextView description = searchViewHolder.mMediaDescription;

                searchViewHolder.mMediaTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (title.getLineCount() > 1) {
                            description.setSingleLine();
                        }
                        // Perform any actions you want based on the line count here.
                    }
                });
            }
        }

        if (result.getOverview() != null) {
            searchViewHolder.mMediaDescription.setText(result.getOverview().toString());
        }

        Picasso.with(mContext)
                .load( mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + result.getBackdropPath())
                .error(R.drawable.generic_movie_background)
                .into(searchViewHolder.mMediaImage);

        setActionButton(searchViewHolder, position, result.getMediaType());
        setListeners(searchViewHolder, position);
    }


    private void setActionButton(SearchViewHolder holder, int position, String mediaType) {
        if (mediaType.equals(MOVIE_TYPE)) {
            if (mUiRealm.where(Movie.class).equalTo("onWatchList", true).equalTo("id", mMultiSearchResults.get(position).getId()).count() > 0) {
                holder.mWatchListLayout.setVisibility(View.VISIBLE);
                holder.mWatchlistIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));
                holder.mWatchedLayout.setVisibility(View.INVISIBLE);
                holder.mActionButton.setText(mContext.getString(R.string.watch_button));
                holder.mActionButton.setEnabled(true);
                holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.primary));

            } else if (mUiRealm.where(Movie.class).equalTo("isWatched", true).equalTo("id", mMultiSearchResults.get(position).getId()).count() > 0) {
                holder.mWatchedLayout.setVisibility(View.VISIBLE);
                holder.mWatchedIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
                holder.mWatchListLayout.setVisibility(View.INVISIBLE);
                String watchedDate = mUiRealm.where(Movie.class).equalTo("id", mMultiSearchResults.get(position).getId()).findFirst().getWatchedDate();
                holder.mActionButton.setText("Watched on " + watchedDate);
                holder.mActionButton.setEnabled(false);
                holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));

            } else {
                holder.mWatchedLayout.setVisibility(View.INVISIBLE);
                holder.mWatchListLayout.setVisibility(View.INVISIBLE);
                holder.mActionButton.setText(mContext.getString(R.string.add_to_watchlist_button));
                holder.mActionButton.setEnabled(true);
                holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
            }

        } else if (mediaType.equals(SHOW_TYPE)) {
            if (mUiRealm.where(TVShow.class).equalTo("onYourShows", true).equalTo("id", mMultiSearchResults.get(position).getId()).count() > 0) {
                holder.mWatchListLayout.setVisibility(View.VISIBLE);
                holder.mWatchlistIcon.setImageDrawable(new IconicsDrawable(mContext).icon(CommunityMaterial.Icon.cmd_television_guide).sizeDp(24).color(Color.WHITE));
                holder.mWatchedLayout.setVisibility(View.INVISIBLE);
                holder.mActionButton.setText(mContext.getString(R.string.your_shows_button));
                holder.mActionButton.setEnabled(false);
                holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));

            } else {
                holder.mWatchedLayout.setVisibility(View.INVISIBLE);
                holder.mWatchedLayout.setVisibility(View.INVISIBLE);
                holder.mActionButton.setText(mContext.getString(R.string.add_to_your_shows_button));
                holder.mActionButton.setEnabled(true);
                holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
            }
        }
    }
    private void moveFromWatchlistToWatched(int position){
        Movie movie = mUiRealm.where(Movie.class).equalTo("id", mMultiSearchResults.get(position).getId()).findFirst();

        mUiRealm.beginTransaction();
        movie.setOnWatchList(false);
        movie.setWatched(true);
        movie.setWatchedDate(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()));
        mUiRealm.copyToRealmOrUpdate(movie);
        mUiRealm.commitTransaction();

        notifyDataSetChanged();
    }
    private void moveFromBrowseToWatchList(final int position, final View v) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.movie_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);
        Call<Movie> call = service.getMovie(Integer.toString(mMultiSearchResults.get(position).getId()));

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()){
                    mMovie = response.body();
                    mMovie.setBackdropPath(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + mMovie.getBackdropPath());

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(mContext.getString(R.string.movie_base_url))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    MovieAPI service = retrofit.create(MovieAPI.class);
                    Call<Credits> creditsCall = service.getCredits(Integer.toString(mMovie.getId()));

                    creditsCall.enqueue(new Callback<Credits>() {
                        @Override
                        public void onResponse(Call<Credits> call, Response<Credits> response) {
                            if (response.isSuccessful()) {
                                mCredits = response.body();

                                Snackbar.make(v, mMovie.getTitle() + " Added to watchlist!", Snackbar.LENGTH_LONG).show();

                                Picasso.with(mContext)
                                        .load(mMovie.getBackdropPath())
                                        .into(browseToWatchlistTarget);
                            } else {
                                Snackbar.make(v, "Error making API call", Snackbar.LENGTH_LONG);
                            }
                        }

                        @Override
                        public void onFailure(Call<Credits> call, Throwable t) {
                            Snackbar.make(v, "Error accessing internet", Snackbar.LENGTH_LONG);
                        }
                    });
                } else {
                    Snackbar.make(v, "Error making API call", Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Snackbar.make(v, "Error accessing internet", Snackbar.LENGTH_LONG);
            }
        });
    }

    private void setListeners(final SearchViewHolder holder, final int position) {
        holder.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MultiSearchResult result = mMultiSearchResults.get(position);

                if (result.getMediaType().equals(MOVIE_TYPE)) {
                    Log.d("SEARCH VIEW", "MVOIE");

                    if (mUiRealm.where(Movie.class).equalTo("onWatchList", true).equalTo("id", mMultiSearchResults.get(position).getId()).count() > 0) {
                        Log.d("SEARCH VIEW", "WATCHLIST");

                        holder.mWatchedLayout.setVisibility(View.VISIBLE);
                        holder.mWatchedIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
                        holder.mWatchListLayout.setVisibility(View.INVISIBLE);
                        moveFromWatchlistToWatched(position);

                    } else {
                        Log.d("SEARCH VIEW", "SETTING WATCHED LAYOUT");
                        holder.mWatchListLayout.setVisibility(View.VISIBLE);
                        holder.mWatchlistIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));
                        holder.mWatchedLayout.setVisibility(View.INVISIBLE);
                        moveFromBrowseToWatchList(position, v);
                    }

                    /*final int movieID = result.getId();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://api.themoviedb.org/3/movie/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    MovieAPI service = retrofit.create(MovieAPI.class);
                    Call<Movie> call = service.getMovie(Integer.toString(movieID));

                    call.enqueue(new Callback<Movie>() {
                        @Override
                        public void onResponse(Call<Movie> call, Response<Movie> response) {
                            Log.d(TAG, "getMovie() Callback Success");
                            mMovie = response.body();
                            mMovie.setBackdropPath("https://image.tmdb.org/t/p/"  + mContext.getString(R.string.image_size_w500) + mMovie.getBackdropPath());

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://api.themoviedb.org/3/movie/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            MovieAPI service = retrofit.create(MovieAPI.class);
                            Call<Credits> creditsCall = service.getCredits(Integer.toString(movieID));

                            creditsCall.enqueue(new Callback<Credits>() {
                                @Override
                                public void onResponse(Call<Credits> call, Response<Credits> response) {
                                    Log.d(TAG, "GetCredits Callback Success");
                                    mCredits = response.body();

                                    Target target = new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            mMovie.setBackdropBitmap(stream.toByteArray());

                                            mUiRealm.beginTransaction();
                                            mMovie.setOnWatchList(true);
                                            mUiRealm.copyToRealmOrUpdate(mMovie);
                                            mUiRealm.copyToRealmOrUpdate(mCredits);
                                            mUiRealm.commitTransaction();

                                            holder.mWatchListLayout.setVisibility(View.VISIBLE);

                                            Snackbar.make(v, "Added to watchlist!",
                                                    Snackbar.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    };

                                    Picasso.with(mContext)
                                            .load(mMovie.getBackdropPath())
                                            .into(target);
                                }

                                @Override
                                public void onFailure(Call<Credits> call, Throwable t) {
                                    Log.d(TAG, "GetCredits() Callback Failure");
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Movie> call, Throwable t) {
                            Log.d(TAG, "getMovie() Callback Failure");
                        }
                    });*/
                } else {
                    final int showId = result.getId();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://api.themoviedb.org/3/tv/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    final TVShowAPI service = retrofit.create(TVShowAPI.class);

                    Call<TVShow> call = service.getTVShow(Integer.toString(showId));
                    call.enqueue(new Callback<TVShow>() {
                        @Override
                        public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                            if (response.isSuccessful()){
                                mShow = response.body();
                                Target target = new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        Log.d(TAG, "onBitmapLoaded()");

                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                                        mUiRealm.beginTransaction();
                                        mShow.setBackdropBitmap(stream.toByteArray());
                                        mShow.setOnYourShows(true);
                                        mUiRealm.copyToRealmOrUpdate(mShow);
                                        mUiRealm.commitTransaction();

                                        notifyDataSetChanged();

                                        FetchSeasonsTask fetchSeasonsTask = new FetchSeasonsTask();
                                        fetchSeasonsTask.execute(showId, mShow.getNumberOfSeasons());

                                        Snackbar.make(v, mShow.getName() + " Added to your shows!",
                                                Snackbar.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {
                                        Log.d(TAG, "onBitmapFailed()");
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        Log.d(TAG, "onPrepareLoad()");
                                    }
                                };

                                Picasso.with(mContext)
                                        .load("https://image.tmdb.org/t/p/w500/" + mShow.getBackdropPath())
                                        .into(target);
                            } else {
                                Snackbar.make(v, "Unable to load movie...", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<TVShow> call, Throwable t) {
                            Snackbar.make(v, "Unable to connect to API..", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        PreferencesHelper.printValues(mContext);

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

        return new SearchViewHolder(itemView);
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_title)
        TextView mMediaTitle;

        @BindView(R.id.card_text)
        TextView mMediaDescription;

        @BindView(R.id.card_image)
        ImageView mMediaImage;

        @BindView(R.id.action_button)
        IconicsButton mActionButton;

        @BindView(R.id.watched_layout)
        RelativeLayout mWatchedLayout;

        @BindView(R.id.watch_list_layout)
        RelativeLayout mWatchListLayout;

        @BindView(R.id.more_button)
        ImageButton mMoreOptionsButton;

        @BindView(R.id.watched_icon)
        ImageView mWatchedIcon;

        @BindView(R.id.watchlist_icon)
        ImageView mWatchlistIcon;

        @OnClick(R.id.more_button)
        public void setmMoreOptionsButton(View view) {
            Log.d(TAG, "moreOptionsButtonClick()");

            PopupMenu popup = new PopupMenu(mContext, mMoreOptionsButton);
            popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d(TAG, "More options clicked");
                    return true;
                }
            });
            popup.show();
        }

        public SearchViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    MultiSearchResult result = mMultiSearchResults.get(getAdapterPosition());

                    if (result.getMediaType().equals(MOVIE_TYPE)) {
                        Log.d(TAG, "movieId" + Integer.toString(result.getId()));
                        Intent intent = new Intent(context, BrowseMovieDetailActivity.class);
                        intent.putExtra(mContext.getString(R.string.movieId), result.getId());
                        context.startActivity(intent);
                    } else if (result.getMediaType().equals(SHOW_TYPE)) {
                        Log.d(TAG, "showId" + Integer.toString(result.getId()));
                        Intent intent = new Intent(context, TVShowBrowseDetailActivity.class);
                        intent.putExtra(mContext.getString(R.string.showId), result.getId());
                        intent.putExtra(mContext.getString(R.string.showTitle), result.getName());
                        context.startActivity(intent);
                    } else {
                        Log.d(TAG, "ERROR");
                    }
                }
            });
        }
    }

    public void UpdateRealmSeasons(ArrayList<TVShowSeasonResult> seasons, int showId) {
        //add to realm
        Log.d("realm transaction","attempting to add");

        for (TVShowSeasonResult season: seasons) {
            Season curSeason = new Season();
            curSeason.setAirDate(season.getAirDate());
            curSeason.setEpisodeCount(season.getEpisodes().size());
            curSeason.setId(season.getId());
            curSeason.setPosterPath(season.getPosterPath());
            curSeason.setShow_id(showId);
            curSeason.setSeasonNumber(season.getSeasonNumber());

            mUiRealm.beginTransaction();
            mUiRealm.copyToRealmOrUpdate(curSeason);
            mUiRealm.commitTransaction();

            RealmList<Episode> jsonEpisodeRealmList = season.getEpisodes();
            for (Episode episode: jsonEpisodeRealmList) {
                mUiRealm.beginTransaction();
                episode.setShow_id(showId);
                episode.setIsWatched(false);
                Log.d(TAG, "Current season number: " + curSeason.getSeasonNumber());
                episode.setSeasonNumber(curSeason.getSeasonNumber());
                mUiRealm.copyToRealmOrUpdate(episode);
                mUiRealm.commitTransaction();
            }

            Log.d(TAG, "Number of episodes in show: " + mUiRealm.where(Episode.class).equalTo("show_id", showId).count());
            Log.d(TAG, "Number of episodes in Season 1: " + mUiRealm.where(Episode.class).equalTo("show_id", showId).equalTo("seasonNumber", 1).findAll().size());
        }
    }

    private class FetchSeasonsTask extends AsyncTask<Integer, Integer, ArrayList<TVShowSeasonResult>> {
        private int mShowId;

        protected ArrayList<TVShowSeasonResult> doInBackground(Integer... params) {
            mShowId = params[0];
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
                Call<TVShowSeasonResult> call = service.getSeasons(Integer.toString(mShowId), Integer.toString(i));
                try {
                    seasons.add(call.execute().body());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return seasons;
        }

        protected void onPostExecute(ArrayList<TVShowSeasonResult> result) {
            UpdateRealmSeasons(result, mShowId);
        }
    }
}
