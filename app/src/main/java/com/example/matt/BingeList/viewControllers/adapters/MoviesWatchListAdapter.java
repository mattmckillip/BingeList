package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.viewControllers.activities.movies.MovieWatchListDetailActivity;
import com.mikepenz.iconics.view.IconicsButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;


public class MoviesWatchListAdapter extends RecyclerView.Adapter<MoviesWatchListAdapter.BrowseMoviesViewHolder> {
    private RealmResults<Movie> mMovieList;
    private static final String TAG = MoviesWatchListAdapter.class.getSimpleName();
    private Context mContext;
    private Realm mUiRealm;

    public MoviesWatchListAdapter(RealmResults movieList, Context context, Realm uiRealm) {
        mContext = context;
        mUiRealm = uiRealm;
        mMovieList = movieList;
    }

    @Override
    public BrowseMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_two_button_card, viewGroup, false);

        return new BrowseMoviesViewHolder(itemView, mContext, mMovieList);
    }

    @Override
    public void onBindViewHolder(BrowseMoviesViewHolder holder, final int position) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onBindViewHolder()");
        }

        if (mMovieList.get(position).getBackdropBitmap() != null) {
            Bitmap bmp;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            bmp = BitmapFactory.decodeByteArray(mMovieList.get(position).getBackdropBitmap(), 0, mMovieList.get(position).getBackdropBitmap().length, options);
            holder.mMovieImage.setImageBitmap(bmp);
        }

        RealmResults<Movie> watchedMovie = mUiRealm.where(Movie.class)
                .equalTo("id", mMovieList.get(position).getId())
                .equalTo("isWatched", true)
                .findAll();

        if (watchedMovie.size() > 0){
            holder.mRemoveButton.setText("{gmd_undo} Unwatch");
            holder.mWatchButton.setVisibility(View.INVISIBLE);
        } else {
            holder.mRemoveButton.setText("{gmd_clear} Remove");
            holder.mWatchButton.setText("{gmd_done} Watch");
            holder.mWatchButton.setVisibility(View.VISIBLE);

        }
        holder.mMovieTitle.setText(mMovieList.get(position).getTitle());
        holder.mMovieDescription.setText(mMovieList.get(position).getOverview());


        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = mMovieList.get(position);
                String movieTitle = movie.getTitle();
                Integer movieID = movie.getId();

                RealmResults<Movie> watchedMovie = mUiRealm.where(Movie.class).equalTo("isWatched", true).equalTo("id", movieID).findAll();

                if (watchedMovie.size() == 1){
                    mUiRealm.beginTransaction();
                    movie.setOnWatchList(true);
                    movie.setWatched(false);
                    mUiRealm.copyToRealmOrUpdate(movie);
                    mUiRealm.commitTransaction();

                    notifyDataSetChanged();

                    Snackbar.make(v, movie.getTitle() + " moved to watchlist!",
                            Snackbar.LENGTH_LONG).show();
                } else {
                    mUiRealm.beginTransaction();
                    //Movie movieToAdd = uiRealm.createObject(movie);
                    RealmResults<Movie> movieResults = mUiRealm.where(Movie.class)
                            .equalTo("id", movieID)
                            .findAll();

                    for (int i = 0; i < movieResults.size(); i++) {
                        movieResults.get(i).deleteFromRealm();
                    }

                    RealmResults<Credits> creditsResults = mUiRealm.where(Credits.class)
                            .equalTo("id", movieID)
                            .findAll();

                    for (int i = 0; i < creditsResults.size(); i++) {
                        creditsResults.get(i).deleteFromRealm();
                    }

                    mUiRealm.commitTransaction();
                    notifyDataSetChanged();

                    Snackbar.make(v, "Removed " + movieTitle + " from watchlist", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        holder.mWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = mMovieList.get(position);
                String movieTitle = movie.getTitle();
                mUiRealm.beginTransaction();
                movie.setWatched(true);
                movie.setOnWatchList(false);
                mUiRealm.commitTransaction();

                notifyDataSetChanged();

                Snackbar.make(v, "Watched " + movieTitle + "!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

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

        @BindView(R.id.remove_button)
        IconicsButton mRemoveButton;

        @BindView(R.id.watch_button)
        IconicsButton mWatchButton;

        public BrowseMoviesViewHolder(View v, Context context, final List<Movie> movieList) {
            super(v);
            mContext = context;
            mMovieList = movieList;

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = mMovieList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, MovieWatchListDetailActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}