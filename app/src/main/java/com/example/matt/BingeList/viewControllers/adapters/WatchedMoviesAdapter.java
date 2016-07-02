package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.movies.ArchivedMovies;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.activities.movies.WatchlistDetailActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class WatchedMoviesAdapter extends RecyclerView.Adapter<WatchedMoviesAdapter.WatchedMovieViewHolder> {
    private RealmList<Movie> mMovieList;
    private static final String TAG = WatchedMoviesAdapter.class.getSimpleName();
    private Context mContext;
    private Realm mUiRealm;
    private int viewMode;

    public WatchedMoviesAdapter(RealmList<Movie> movieList, Context context, Realm uiRealm) {
        mContext = context;
        mUiRealm = uiRealm;
        mMovieList = movieList;
        viewMode = PreferencesHelper.getRecyclerviewViewType(mContext);
    }

    public void UpdateData(RealmList<Movie> movieRealmList){
        mMovieList = movieRealmList;
        notifyDataSetChanged();
    }

    @Override
    public WatchedMovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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
        }else {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_more_options_card, viewGroup, false);
        }

        return new WatchedMovieViewHolder(itemView, mContext, mMovieList, mUiRealm);
    }

    @Override
    public void onBindViewHolder(WatchedMovieViewHolder holder, final int position) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onBindViewHolder()");
        }

        holder.mProgressSpinner.setVisibility(View.GONE);
        holder.mWatchedLayout.setVisibility(View.GONE);
        holder.mWatchListLayout.setVisibility(View.GONE);

        // hide archived items
        if(mUiRealm.where(ArchivedMovies.class).equalTo("movieId", mMovieList.get(position).getId()).count() > 0) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, mMovieList.get(position).getTitle() + " is archived");
            }
            mMovieList.remove(position);
            return;
        }

        if (mMovieList.get(position).getBackdropBitmap() != null) {
            Bitmap bmp;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            bmp = BitmapFactory.decodeByteArray(mMovieList.get(position).getBackdropBitmap(), 0, mMovieList.get(position).getBackdropBitmap().length, options);
            holder.mMovieImage.setImageBitmap(bmp);
        }

        holder.mMoreOptionsButton.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_more_vert).sizeDp(16).color(ContextCompat.getColor(mContext, R.color.button_grey)));
        holder.mMovieTitle.setText(mMovieList.get(position).getTitle());
        holder.mMovieDescription.setText(mMovieList.get(position).getOverview());
        holder.mUnWatchButton.setText("Watched on " + mMovieList.get(position).getWatchedDate());
        holder.mUnWatchButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));

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

        holder.mMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "moreOptionsButtonClick()");

                PopupMenu popup = new PopupMenu(mContext, v);
                popup.getMenuInflater().inflate(R.menu.menu_watched, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    Snackbar snackbar = null;
                    Movie movie = mMovieList.get(position);

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_archive:
                                archiveMovie(movie.getId(), position);

                                snackbar = Snackbar
                                        .make(v, movie.getTitle() + " hidden", Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                unArchiveMovie(movie, position);
                                                Snackbar.make(view, movie.getTitle() + " is restored", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });

                                snackbar.show();
                                return true;

                            case R.id.action_remove:
                                final Movie movie = mMovieList.get(position);
                                final String movieTitle = mMovieList.get(position).getTitle();
                                removeFromWatchList(movie, position);

                                snackbar = Snackbar
                                        .make(v, movieTitle + " removed from watchlist", Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                restoreToWatchList(movie, position);
                                                Snackbar.make(view, movieTitle + " is restored", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });

                                snackbar.show();

                                return true;

                            case R.id.action_move_to_watchlist:
                                final Movie movieToRemove = mMovieList.get(position);
                                moveToWatchList(movieToRemove, position);

                                snackbar = Snackbar
                                        .make(v, movieToRemove.getTitle() + " unwatched", Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                restoreToWatchList(movieToRemove, position);
                                                Snackbar.make(view, movieToRemove.getTitle() + " watched", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });

                                snackbar.show();
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    private void moveToWatchList(Movie movieToRemove, int position) {
        mUiRealm.beginTransaction();
        movieToRemove.setOnWatchList(true);
        movieToRemove.setWatched(false);
        mUiRealm.copyToRealmOrUpdate(movieToRemove);
        mUiRealm.commitTransaction();

        mMovieList.remove(position);
        notifyDataSetChanged();
    }

    private void restoreToWatchList(Movie movie, int position) {
        mUiRealm.beginTransaction();
        movie.setOnWatchList(false);
        movie.setWatched(true);
        mUiRealm.copyToRealmOrUpdate(movie);
        mUiRealm.commitTransaction();

        mMovieList.add(position, movie);
        notifyDataSetChanged();
    }

    private void removeFromWatchList(Movie movie, int position) {
        int movieId = movie.getId();

        mUiRealm.beginTransaction();
        movie.deleteFromRealm();

        RealmResults<Credits> creditsResults = mUiRealm.where(Credits.class)
                .equalTo("id", movieId)
                .findAll();

        for (int i = 0; i < creditsResults.size(); i++) {
            creditsResults.get(i).deleteFromRealm();
        }

        mUiRealm.commitTransaction();

        mMovieList.remove(position);
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
        Log.d(TAG, "archiveMovie()");

        mUiRealm.beginTransaction();
        ArchivedMovies archivedMovies = new ArchivedMovies();
        archivedMovies.setMovieId(id);
        mUiRealm.copyToRealmOrUpdate(archivedMovies);
        mUiRealm.commitTransaction();

        mMovieList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public static class WatchedMovieViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = WatchedMovieViewHolder.class.getSimpleName();
        private Context mContext;
        private List<Movie> mMovieList;
        private Realm mUiRealm;

        @BindView(R.id.card_title)
        TextView mMovieTitle;

        @BindView(R.id.card_image)
        ImageView mMovieImage;

        @BindView(R.id.card_text)
        TextView mMovieDescription;

        @BindView(R.id.more_button)
        ImageButton mMoreOptionsButton;

        @BindView(R.id.action_button)
        IconicsButton mUnWatchButton;

        @BindView(R.id.watched_layout)
        RelativeLayout mWatchedLayout;

        @BindView(R.id.watch_list_layout)
        RelativeLayout mWatchListLayout;

        @BindView(R.id.progress_spinner)
        ProgressBar mProgressSpinner;

        public WatchedMovieViewHolder(View v, Context context, final List<Movie> movieList, Realm uiRealm) {
            super(v);
            mContext = context;
            mMovieList = movieList;
            mUiRealm = uiRealm;

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = mMovieList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, WatchlistDetailActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}