package com.example.matt.bingeList.viewControllers.adapters.movies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.NetflixRouletteResponse;
import com.example.matt.bingeList.models.movies.ArchivedMovies;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.API.NetflixAPI;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.uitls.BadgeDrawable;
import com.example.matt.bingeList.uitls.Enums.NetflixStreaming;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.activities.movies.BrowseMovieDetailActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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


public class BrowseMoviesAdapter extends RecyclerView.Adapter<BrowseMoviesAdapter.BrowseMoviesViewHolder> {
    private RealmList<Movie> mMovieList;
    private static final String TAG = BrowseMoviesAdapter.class.getSimpleName();
    private Context mContext;
    private Realm mUiRealm;
    private Movie mMovie;
    private int viewMode;
    private Credits mCredits;

    private Target browseToWatchedTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            mUiRealm.beginTransaction();
            mMovie.setBackdropBitmap(stream.toByteArray());
            mMovie.setOnWatchList(false);
            mMovie.setWatched(true);
            mMovie.setWatchedDate(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()));
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

    public BrowseMoviesAdapter(RealmList<Movie> movieList, Context context, Realm uiRealm) {
        mMovieList = movieList;
        mContext = context;
        mUiRealm = uiRealm;
        setHasStableIds(true);
        viewMode = PreferencesHelper.getRecyclerviewViewType(mContext);
    }

    public void addMoreMovies(RealmList<Movie> additionMovies){
        for (Movie movieToAdd: additionMovies){
            mMovieList.add(movieToAdd);
        }
        notifyDataSetChanged();
    }

    @Override
    public BrowseMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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

        return new BrowseMoviesViewHolder(itemView, mContext, mUiRealm, mMovieList);
    }

    @Override
    public void onBindViewHolder(final BrowseMoviesViewHolder holder, final int position) {
        holder.mWatchedLayout.setVisibility(View.GONE);
        holder.mWatchListLayout.setVisibility(View.GONE);
        holder.mMovieTitle.setVisibility(View.GONE);

        holder.mMoreOptionsButton.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_more_vert).sizeDp(16).color(ContextCompat.getColor(mContext, R.color.button_grey)));
        holder.mMovieTitle.setText(mMovieList.get(position).getTitle());

        // Check the case where the title is too long
        if (viewMode == ViewType.COMPACT_CARD || viewMode == ViewType.LIST) {
            final TextView title = holder.mMovieTitle;
            final TextView description = holder.mMovieDescription;

            final String titleString = mMovieList.get(position).getTitle();
            holder.mMovieTitle.post(new Runnable() {
                @Override
                public void run() {
                    if (title.getLineCount() > 1) {
                        description.setSingleLine();
                    }
                    // Perform any actions you want based on the line count here.
                }
            });
        } else {
            holder.mNetflixBadge.setVisibility(View.GONE);

            if (mMovieList.get(position).getNetflixStreaming() == NetflixStreaming.STREAMING) {
                holder.mNetflixBadge.setVisibility(View.VISIBLE);
            } else {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mContext.getString(R.string.movie_base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MovieAPI service = retrofit.create(MovieAPI.class);
                final Movie movie = mMovieList.get(position);
                movie.setNetflixStreaming(NetflixStreaming.NOT_STREAMING);

                Call<Movie> call = service.getMovie(Integer.toString(movie.getId()));
                call.enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (response.isSuccessful()) {
                            final String imdbID = response.body().getImdbId();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://netflixroulette.net/api/v2/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            NetflixAPI service = retrofit.create(NetflixAPI.class);

                            Call<NetflixRouletteResponse> netflixRouletteResponseCall = service.checkNetflix(imdbID);

                            netflixRouletteResponseCall.enqueue(new Callback<NetflixRouletteResponse>() {
                                @Override
                                public void onResponse(Call<NetflixRouletteResponse> call, Response<NetflixRouletteResponse> response) {
                                    if (response.isSuccessful()) {
                                        if (response.body().getNetflixId() != null && !response.body().getNetflixId().equals("null")) {
                                            Log.d(TAG, movie.getTitle());
                                            Log.d(TAG, response.raw().toString());
                                            movie.setNetflixStreaming(NetflixStreaming.STREAMING);
                                            holder.mNetflixBadge.setVisibility(View.VISIBLE);
                                            holder.mNetflixBadge.setImageDrawable(new BadgeDrawable(mContext, "Netflix", ContextCompat.getColor(mContext, R.color.lightColorPrimary)));
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<NetflixRouletteResponse> call, Throwable t) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                    }
                });
            }
        }

        holder.mMovieDescription.setText(mMovieList.get(position).getOverview());

        Picasso.with(mContext)
                .load(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) +  mMovieList.get(position).getBackdropPath())
                .error(R.drawable.generic_movie_background)
                .into(holder.mMovieImage);

        holder.mMovieTitle.setVisibility(View.VISIBLE);

        setActionButton(holder, position);
        setListeners(holder, position);
    }

    @Override
    public long getItemId(int position){
        return mMovieList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    // VIEW HOLDER
    public static class BrowseMoviesViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = BrowseMoviesViewHolder.class.getSimpleName();
        private Context mContext;
        private List<Movie> mMovieList;

        @BindView(R.id.card_title)
        TextView mMovieTitle;

        @BindView(R.id.card_image)
        ImageView mMovieImage;

        @BindView(R.id.card_text)
        TextView mMovieDescription;

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

        @BindView(R.id.netflix_badge)
        ImageView mNetflixBadge;


        public BrowseMoviesViewHolder(View v, Context context, final Realm uiRealm, final List<Movie> movieList) {
            super(v);
            mContext = context;
            mMovieList = movieList;

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Integer movieId = mMovieList.get(getAdapterPosition()).getId();
                    Intent intent = new Intent(context, BrowseMovieDetailActivity.class);
                    intent.putExtra(mContext.getString(R.string.movieId), movieId);
                    context.startActivity(intent);
                }
            });
        }
    }

    //HELPERS
    public boolean isOnWatchList(int position){
        return mUiRealm.where(Movie.class).equalTo("onWatchList", true).equalTo("id", mMovieList.get(position).getId()).count() > 0;
    }

    public boolean isWatched(int position){
        return mUiRealm.where(Movie.class).equalTo("isWatched", true).equalTo("id", mMovieList.get(position).getId()).count() > 0;
    }

    private void setListeners(final BrowseMoviesViewHolder holder, final int position){
        holder.mMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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
                                    moveFromBrowseToWatched(position, v, holder);
                                    return true;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            }
        });

        holder.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (isOnWatchList(position)) {
                    setWatchedOverlay(holder);
                    moveFromWatchlistToWatched(position);
                    Snackbar.make(v, "Watched!", Snackbar.LENGTH_LONG).show();

                } else if (!isWatched(position)){
                    setWatchlistOverlay(holder);
                    moveFromBrowseToWatchList(position, v);
                }
            }
        });
    }

    private void archiveHandler(final Movie movie, final int position, View v){
        archiveMovie(movie.getId(), position);

        Snackbar snackbar = Snackbar
                .make(v, movie.getTitle() + " hidden", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unArchiveMovie(movie, position);
                        Snackbar.make(view, movie.getTitle() + " is restored", Snackbar.LENGTH_SHORT).show();
                    }
                });

        snackbar.show();
    }

    private void moveFromWatchlistToWatched(int position){
        Movie movie = mUiRealm.where(Movie.class).equalTo("id", mMovieList.get(position).getId()).findFirst();

        mUiRealm.beginTransaction();
        movie.setOnWatchList(false);
        movie.setWatched(true);
        movie.setWatchedDate(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()));
        mUiRealm.copyToRealmOrUpdate(movie);
        mUiRealm.commitTransaction();

        notifyDataSetChanged();
    }

    private void moveFromWatchedToWatchList(int position){
        Movie movie = mUiRealm.where(Movie.class).equalTo("id", mMovieList.get(position).getId()).findFirst();

        mUiRealm.beginTransaction();
        movie.setOnWatchList(true);
        movie.setWatched(false);
        movie.setWatchedDate("");
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
        Call<Movie> call = service.getMovie(Integer.toString(mMovieList.get(position).getId()));

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

    private void moveFromBrowseToWatched(final int position, final View v, final BrowseMoviesViewHolder holder) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.movie_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI service = retrofit.create(MovieAPI.class);
        Call<Movie> call = service.getMovie(Integer.toString(mMovieList.get(position).getId()));

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

                                Snackbar.make(v, mMovie.getTitle() + " Watched!", Snackbar.LENGTH_LONG).show();

                                Picasso.with(mContext)
                                        .load(mMovie.getBackdropPath())
                                        .error(R.drawable.generic_movie_background)
                                        .placeholder(R.drawable.generic_movie_background)
                                        .into(browseToWatchedTarget);
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

    private void moveFromWatchListToBrowse(int movieId) {
        mUiRealm.beginTransaction();
        // Find and remove the movie
        Movie movie = mUiRealm.where(Movie.class).equalTo("id", movieId).findFirst();
        movie.deleteFromRealm();

        // Find and remove all credits
        RealmResults<Credits> creditsResults = mUiRealm.where(Credits.class)
                .equalTo("id", movieId)
                .findAll();

        for (int i = 0; i < creditsResults.size(); i++) {
            creditsResults.get(i).deleteFromRealm();
        }

        mUiRealm.commitTransaction();
        notifyDataSetChanged();
    }

    private void unArchiveMovie(Movie movie, int position) {
        mUiRealm.beginTransaction();
        ArchivedMovies archivedMovies = mUiRealm.where(ArchivedMovies.class).equalTo("movieId", movie.getId()).findFirst();
        archivedMovies.deleteFromRealm();
        mUiRealm.commitTransaction();

        mMovieList.add(position, movie);
        notifyDataSetChanged();
    }

    private void archiveMovie(Integer id, int position) {
        mUiRealm.beginTransaction();
        ArchivedMovies archivedMovies = new ArchivedMovies();
        archivedMovies.setMovieId(id);
        mUiRealm.copyToRealmOrUpdate(archivedMovies);
        mUiRealm.commitTransaction();

        mMovieList.remove(position);
        notifyDataSetChanged();
    }

    private void setActionButton(BrowseMoviesViewHolder holder, int position) {
        if (isOnWatchList(position)) {
            setWatchlistOverlay(holder);
            holder.mActionButton.setText(mContext.getString(R.string.watch_button));
            holder.mActionButton.setEnabled(true);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));

        } else if (isWatched(position)) {
            setWatchedOverlay(holder);
            String watchedDate = mUiRealm.where(Movie.class).equalTo("id", mMovieList.get(position).getId()).findFirst().getWatchedDate();
            holder.mActionButton.setText("Watched on " + watchedDate);
            holder.mActionButton.setEnabled(false);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));

        } else {
            setNoOverlay(holder);
            holder.mActionButton.setText(mContext.getString(R.string.add_to_watchlist_button));
            holder.mActionButton.setEnabled(true);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
        }
    }

    private void setWatchedOverlay(BrowseMoviesViewHolder holder){
        holder.mWatchedLayout.setVisibility(View.VISIBLE);
        holder.mWatchedIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
        holder.mWatchListLayout.setVisibility(View.INVISIBLE);
    }

    private void setWatchlistOverlay(BrowseMoviesViewHolder holder){
        holder.mWatchListLayout.setVisibility(View.VISIBLE);
        holder.mWatchlistIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));
        holder.mWatchedLayout.setVisibility(View.INVISIBLE);
    }

    private void setNoOverlay(BrowseMoviesViewHolder holder){
        holder.mWatchedLayout.setVisibility(View.INVISIBLE);
        holder.mWatchListLayout.setVisibility(View.INVISIBLE);
    }
}