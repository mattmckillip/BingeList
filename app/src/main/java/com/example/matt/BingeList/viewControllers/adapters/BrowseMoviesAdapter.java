package com.example.matt.bingeList.viewControllers.adapters;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.movies.ArchivedMovies;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MovieAPI;
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
        Log.d(TAG, "onCreateViewHolder()");
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
    public void onBindViewHolder(BrowseMoviesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder()");
        holder.mProgressSpinner.setVisibility(View.GONE);
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
                    Log.d(TAG, "Number of lines in " + titleString + ": " + Integer.toString(title.getLineCount()));
                    if (title.getLineCount() > 1) {
                        description.setSingleLine();
                    }
                    // Perform any actions you want based on the line count here.
                }
            });
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

        @BindView(R.id.progress_spinner)
        ProgressBar mProgressSpinner;

        @BindView(R.id.watched_icon)
        ImageView mWatchedIcon;

        @BindView(R.id.watchlist_icon)
        ImageView mWatchlistIcon;


        public BrowseMoviesViewHolder(View v, Context context, final Realm uiRealm, final List<Movie> movieList) {
            super(v);
            mContext = context;
            mMovieList = movieList;

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "itemViewClick()");
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
                                    holder.mProgressSpinner.setVisibility(View.VISIBLE);
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
                holder.mProgressSpinner.setVisibility(View.VISIBLE);

                if (isOnWatchList(position)) {
                    moveFromWatchlistToWatched(position);
                    Snackbar.make(v, "Watched!", Snackbar.LENGTH_LONG).show();

                } else if (!isWatched(position)){
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
        Log.d(TAG, "moveFromBrowseToWatchList()");

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
                    Log.d(TAG, "Movie Success");
                    final Movie movie = response.body();
                    movie.setBackdropPath(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + movie.getBackdropPath());

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(mContext.getString(R.string.movie_base_url))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    MovieAPI service = retrofit.create(MovieAPI.class);
                    Call<Credits> creditsCall = service.getCredits(Integer.toString(movie.getId()));

                    creditsCall.enqueue(new Callback<Credits>() {
                        @Override
                        public void onResponse(Call<Credits> call, Response<Credits> response) {
                            if (response.isSuccessful()) {
                                final Credits credits = response.body();
                                Target target = new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        Log.d(TAG, "onBitmapLoaded()");

                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        Log.d(TAG, "byte array " + Integer.toString(stream.toByteArray().length));

                                        mUiRealm.beginTransaction();
                                        movie.setBackdropBitmap(stream.toByteArray());
                                        movie.setOnWatchList(true);
                                        mUiRealm.copyToRealmOrUpdate(movie);
                                        mUiRealm.copyToRealmOrUpdate(credits);
                                        mUiRealm.commitTransaction();

                                        //setWatchedOverlay(holder);
                                        //setActionButton(holder, position, movie.getWatchedDate());
                                        notifyDataSetChanged();
                                        Snackbar.make(v, "Added to watchlist!", Snackbar.LENGTH_LONG).show();

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
                                        .load(movie.getBackdropPath())
                                        .into(target);
                            } else {
                                Log.d(TAG, "Bad credits call");
                            }
                        }

                        @Override
                        public void onFailure(Call<Credits> call, Throwable t) {
                            Snackbar.make(v, "Error accessing internet", Snackbar.LENGTH_LONG);
                            Log.d(TAG, "Credits - Failure");
                        }
                    });
                } else {
                    Log.d(TAG, "Movie Bad Call");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Snackbar.make(v, "Error accessing internet", Snackbar.LENGTH_LONG);
                Log.d(TAG, "Movie - Failure");
            }
        });
    }

    private void moveFromBrowseToWatched(final int position, final View v, final BrowseMoviesViewHolder holder) {
        Log.d(TAG, "moveFromBrowseToWatchList()");

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
                    Log.d(TAG, "Movie Success");
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
                                final Credits credits = response.body();
                                Target target = new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        Log.d(TAG, "onBitmapLoaded()");

                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        Log.d(TAG, "byte array " + Integer.toString(stream.toByteArray().length));

                                        mUiRealm.beginTransaction();
                                        mMovie.setBackdropBitmap(stream.toByteArray());
                                        mMovie.setOnWatchList(false);
                                        mMovie.setWatched(true);
                                        mMovie.setWatchedDate(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()));
                                        mUiRealm.copyToRealmOrUpdate(mMovie);
                                        mUiRealm.copyToRealmOrUpdate(credits);
                                        mUiRealm.commitTransaction();

                                        notifyDataSetChanged();
                                        Snackbar.make(v, mMovie.getTitle() + " Watched!", Snackbar.LENGTH_LONG).show();
                                        holder.mProgressSpinner.setVisibility(View.GONE);

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
                                        .load(mMovie.getBackdropPath())
                                        .into(target);
                            } else {
                                Log.d(TAG, "Bad credits call");
                            }
                        }

                        @Override
                        public void onFailure(Call<Credits> call, Throwable t) {
                            Snackbar.make(v, "Error accessing internet", Snackbar.LENGTH_LONG);
                            Log.d(TAG, "Credits - Failure");
                        }
                    });
                } else {
                    Log.d(TAG, "Movie Bad Call");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Snackbar.make(v, "Error accessing internet", Snackbar.LENGTH_LONG);
                Log.d(TAG, "Movie - Failure");
            }
        });
    }

    private void moveFromWatchListToBrowse(int movieId) {
        Log.d(TAG, "moveFromWatchListToBrowse()");

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
        Log.d(TAG, "unArchiveMovie()");

        mUiRealm.beginTransaction();
        ArchivedMovies archivedMovies = mUiRealm.where(ArchivedMovies.class).equalTo("movieId", movie.getId()).findFirst();
        archivedMovies.deleteFromRealm();
        mUiRealm.commitTransaction();

        mMovieList.add(position, movie);
        notifyDataSetChanged();
    }

    private void archiveMovie(Integer id, int position) {
        Log.d(TAG, "archiveMovie()");

        mUiRealm.beginTransaction();
        ArchivedMovies archivedMovies = new ArchivedMovies();
        archivedMovies.setMovieId(id);
        mUiRealm.copyToRealmOrUpdate(archivedMovies);
        mUiRealm.commitTransaction();

        mMovieList.remove(position);
        notifyDataSetChanged();
    }

    private void setActionButton(BrowseMoviesViewHolder holder, int position) {
        Log.d(TAG, "setActionButton()");

        if (isOnWatchList(position)) {
            setWatchlistOverlay(holder);
            holder.mActionButton.setText(mContext.getString(R.string.watch_button));
            holder.mActionButton.setEnabled(true);
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.primary));

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
            holder.mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        }
    }

    private void setWatchedOverlay(BrowseMoviesViewHolder holder){
        holder.mWatchedLayout.setVisibility(View.VISIBLE);
        holder.mWatchedIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
        holder.mWatchListLayout.setVisibility(View.INVISIBLE);
        holder.mProgressSpinner.setVisibility(View.GONE);
    }

    private void setWatchlistOverlay(BrowseMoviesViewHolder holder){
        holder.mWatchListLayout.setVisibility(View.VISIBLE);
        holder.mWatchlistIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));
        holder.mWatchedLayout.setVisibility(View.INVISIBLE);
        holder.mProgressSpinner.setVisibility(View.GONE);
    }

    private void setNoOverlay(BrowseMoviesViewHolder holder){
        holder.mWatchedLayout.setVisibility(View.INVISIBLE);
        holder.mWatchListLayout.setVisibility(View.INVISIBLE);
        holder.mProgressSpinner.setVisibility(View.GONE);
    }
}