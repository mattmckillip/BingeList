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
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.viewControllers.activities.movies.BrowseMovieDetailActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;

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


public class MovieBrowseAdapter extends RecyclerView.Adapter<MovieBrowseAdapter.BrowseMoviesViewHolder> {
    private RealmList<Movie> movieList;
    private static final String TAG = MovieBrowseAdapter.class.getSimpleName();
    private Context mContext;
    private Realm mUiRealm;

    public MovieBrowseAdapter(RealmList<Movie> movieList, Context context, Realm uiRealm) {
        this.movieList = movieList;
        mContext = context;
        mUiRealm = uiRealm;
        setHasStableIds(true);
    }

    public void addMoreMovies(RealmList<Movie> additionMovies){
        for (Movie movieToAdd: additionMovies){
            movieList.add(movieToAdd);
        }
        notifyDataSetChanged();
    }

    @Override
    public BrowseMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_more_options_card, viewGroup, false);

        return new BrowseMoviesViewHolder(itemView, mContext, mUiRealm, movieList);
    }

    @Override
    public void onBindViewHolder(final BrowseMoviesViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder()");

        holder.mProgressSpinner.setVisibility(View.GONE);
        holder.mWatchedLayout.setVisibility(View.GONE);
        holder.mWatchListLayout.setVisibility(View.GONE);
        holder.mMovieTitle.setVisibility(View.GONE);
        holder.mMoreOptionsButton.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_more_vert).sizeDp(16));

        holder.mMovieTitle.setText(movieList.get(position).getTitle());
        holder.mMovieDescription.setText(movieList.get(position).getOverview());

        Picasso.with(mContext)
                .load(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) +  movieList.get(position).getBackdropPath())
                .error(R.drawable.generic_movie_background)
                .into(holder.mMovieImage);
        holder.mMovieTitle.setVisibility(View.VISIBLE);

        setActionButton(holder, movieList.get(position).getId());
    }

    private void setActionButton(BrowseMoviesViewHolder holder, Integer movieId) {
        long watchedMovies = mUiRealm.where(Movie.class).equalTo("isWatched", true).equalTo("id", movieId).count();
        long watchListMovies = mUiRealm.where(Movie.class).equalTo("onWatchList", true).equalTo("id", movieId).count();

        if (watchListMovies > 0) {
            holder.mWatchListLayout.setVisibility(View.VISIBLE);
            holder.mAddToWatchListButton.setText(mContext.getString(R.string.watch_button));
            holder.mWatchlistIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));

        } else if (watchedMovies > 0) {
            holder.mWatchedLayout.setVisibility(View.VISIBLE);
            holder.mWatchedIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
            holder.mAddToWatchListButton.setText(mContext.getString(R.string.watched_button));
            holder.mAddToWatchListButton.setEnabled(false);
            holder.mAddToWatchListButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));

        } else {
            holder.mWatchListLayout.setVisibility(View.GONE);
            holder.mWatchedLayout.setVisibility(View.GONE);
            holder.mAddToWatchListButton.setText(mContext.getString(R.string.add_to_watchlist_button));
            holder.mAddToWatchListButton.setEnabled(true);
            holder.mAddToWatchListButton.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        }
    }
    @Override
    public long getItemId(int position){
        return movieList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class BrowseMoviesViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = BrowseMoviesViewHolder.class.getSimpleName();
        private Movie mMovie;
        private Integer mMovieId;
        private Credits mCredits;
        private Context mContext;
        private Realm mUiRealm;
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
        IconicsButton mAddToWatchListButton;

        @BindView(R.id.progress_spinner)
        ProgressBar mProgressSpinner;

        @BindView(R.id.watched_icon)
        ImageView mWatchedIcon;

        @BindView(R.id.watchlist_icon)
        ImageView mWatchlistIcon;

        @OnClick(R.id.action_button)
        public void setmAddToWatchListButton(final View view) {
            Log.d(TAG, "actionButtonClick()");
            mProgressSpinner.setVisibility(View.VISIBLE);

            int movieId = mMovieList.get(getAdapterPosition()).getId();
            long movieIsWatched = mUiRealm.where(Movie.class).equalTo("onWatchList", true).equalTo("id", movieId).count();

            if (movieIsWatched > 0) {
                mMovie = mUiRealm.where(Movie.class).equalTo("id", mMovieList.get(getAdapterPosition()).getId()).findFirst();

                Log.d(TAG, Integer.toString(mMovie.getBackdropBitmap().length));
                mUiRealm.beginTransaction();
                mMovie.setOnWatchList(false);
                mMovie.setWatched(true);
                mUiRealm.copyToRealmOrUpdate(mMovie);
                mUiRealm.commitTransaction();

                mWatchedLayout.setVisibility(View.VISIBLE);
                mWatchedIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
                mWatchListLayout.setVisibility(View.INVISIBLE);
                mAddToWatchListButton.setText(mContext.getString(R.string.watched_button));
                mAddToWatchListButton.setEnabled(false);
                mAddToWatchListButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));
                mProgressSpinner.setVisibility(View.GONE);

                Snackbar.make(view, "Watched!", Snackbar.LENGTH_LONG).show();
            } else {
                mMovieId = mMovieList.get(getAdapterPosition()).getId();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mContext.getString(R.string.movie_base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MovieAPI service = retrofit.create(MovieAPI.class);
                Call<Movie> call = service.getMovie(Integer.toString(mMovieId));

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
                            Call<Credits> creditsCall = service.getCredits(Integer.toString(mMovieId));

                            creditsCall.enqueue(new Callback<Credits>() {
                                @Override
                                public void onResponse(Call<Credits> call, Response<Credits> response) {
                                    if (response.isSuccessful()) {
                                        mCredits = response.body();
                                        Target target = new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                Log.d(TAG, "onBitmapLoaded()");

                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                Log.d(TAG, "byte array " + Integer.toString(stream.toByteArray().length));

                                                mUiRealm.beginTransaction();
                                                mMovie.setBackdropBitmap(stream.toByteArray());
                                                mMovie.setOnWatchList(true);
                                                mUiRealm.copyToRealmOrUpdate(mMovie);
                                                mUiRealm.copyToRealmOrUpdate(mCredits);
                                                mUiRealm.commitTransaction();

                                                mWatchListLayout.setVisibility(View.VISIBLE);
                                                mWatchlistIcon.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));

                                                Snackbar.make(view, "Added to watchlist!",
                                                        Snackbar.LENGTH_LONG).show();

                                                mProgressSpinner.setVisibility(View.GONE);

                                                mAddToWatchListButton.setText(mContext.getString(R.string.watch_button));
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
                                    Snackbar.make(view, "Error accessing internet", Snackbar.LENGTH_LONG);
                                    Log.d(TAG, "Credits - Failure");
                                }
                            });
                        } else {
                            Log.d(TAG, "Movie Bad Call");
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        Snackbar.make(view, "Error accessing internet", Snackbar.LENGTH_LONG);
                        Log.d(TAG, "Movie - Failure");
                    }
                });
            }
        }

        @OnClick(R.id.more_button)
        public void setmMoreOptionsButton(View view) {
            Log.d(TAG, "moreOptionsButtonClick()");

            PopupMenu popup = new PopupMenu(mContext, mMoreOptionsButton);
            popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d("More options", "clicked");
                    return true;
                }
            });
            popup.show();
        }

        public BrowseMoviesViewHolder(View v, Context context, final Realm uiRealm, final List<Movie> movieList) {
            super(v);
            mContext = context;
            mUiRealm = uiRealm;
            mMovieList = movieList;

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "itemViewClick()");
                    Context context = v.getContext();
                    Integer movieId = mMovieList.get(getAdapterPosition()).getId();
                    Intent intent = new Intent(context, BrowseMovieDetailActivity.class);
                    intent.putExtra("movieId", movieId);
                    context.startActivity(intent);
                }
            });
        }
    }
}