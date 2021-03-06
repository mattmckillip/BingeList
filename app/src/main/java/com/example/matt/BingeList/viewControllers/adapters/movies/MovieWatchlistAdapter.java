package com.example.matt.bingeList.viewControllers.adapters.movies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import com.example.matt.bingeList.models.NetflixRouletteResponse;
import com.example.matt.bingeList.models.movies.ArchivedMovies;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.API.NetflixAPI;
import com.example.matt.bingeList.uitls.BadgeDrawable;
import com.example.matt.bingeList.uitls.Enums.MovieSort;
import com.example.matt.bingeList.uitls.Enums.NetflixStreaming;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.activities.movies.WatchlistDetailActivity;
import com.mikepenz.iconics.view.IconicsButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieWatchlistAdapter extends RecyclerView.Adapter<MovieWatchlistAdapter.WatchlistViewHolder> {
    private RealmList<Movie> mMovieList;
    private static final String TAG = MovieWatchlistAdapter.class.getSimpleName();
    private Context mContext;
    private Realm mUiRealm;
    private Movie mMovie;
    private int viewMode;

    public MovieWatchlistAdapter(RealmList movieList, Context context, Realm uiRealm) {
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
    public WatchlistViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        PreferencesHelper.printValues(mContext);

        View itemView = null;
        if (viewMode == ViewType.CARD) {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_two_button_card, viewGroup, false);
        } else if (viewMode == ViewType.COMPACT_CARD){
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_two_button_compact_card, viewGroup, false);
        } else if (viewMode == ViewType.LIST){
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_two_button_list, viewGroup, false);
        } else {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_two_button_card, viewGroup, false);
        }

        return new WatchlistViewHolder(itemView, mContext, mMovieList);
    }

    @Override
    public void onBindViewHolder(final WatchlistViewHolder holder, final int position) {
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
            holder.mRemoveButton.setText(mContext.getString(R.string.unwatch_button));
            holder.mWatchButton.setVisibility(View.INVISIBLE);
        } else {
            holder.mRemoveButton.setText(mContext.getString(R.string.remove_button));
            holder.mWatchButton.setText(mContext.getString(R.string.watch_button));
            holder.mWatchButton.setVisibility(View.VISIBLE);

        }
        holder.mMovieTitle.setText(mMovieList.get(position).getTitle());
        holder.mMovieDescription.setText(mMovieList.get(position).getOverview());

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
                holder.mNetflixBadge.setImageDrawable(new BadgeDrawable(mContext, "Netflix", ContextCompat.getColor(mContext, R.color.lightColorPrimary)));
            }
        }

        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = mMovieList.get(position);
                String movieTitle = movie.getTitle();
                Integer movieID = movie.getId();

                mUiRealm.beginTransaction();
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
                mMovieList.remove(position);
                notifyDataSetChanged();
                Snackbar.make(v, "Removed " + movieTitle + " from watchlist", Snackbar.LENGTH_LONG).show();
            }
        });

        holder.mWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());

                Movie movie = mMovieList.get(position);
                String movieTitle = movie.getTitle();
                mUiRealm.beginTransaction();
                movie.setWatched(true);
                movie.setOnWatchList(false);
                movie.setWatchedDate(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()));
                mUiRealm.commitTransaction();
                mMovieList.remove(position);

                notifyDataSetChanged();

                Snackbar.make(v, "Watched " + movieTitle + "!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public static class WatchlistViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = WatchlistViewHolder.class.getSimpleName();
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

        @BindView(R.id.netflix_badge)
        ImageView mNetflixBadge;

        public WatchlistViewHolder(View v, Context context, final List<Movie> movieList) {
            super(v);
            mContext = context;
            mMovieList = movieList;

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = mMovieList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, WatchlistDetailActivity.class);
                    intent.putExtra(mContext.getString(R.string.movieId), movie.getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    //HELPERS

    public void sort(int sortType) {
        Log.d(TAG, "sort()");

        RealmResults<Movie> movieRealmResults = null;
        if (sortType == MovieSort.DATE_ADDED) {
            movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAll();
        } else if (sortType == MovieSort.RATING){
            movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAllSorted("voteAverage", Sort.DESCENDING);
        } else if (sortType == MovieSort.RUNTIME){
            movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAllSorted("runtime", Sort.DESCENDING);
        } else if (sortType == MovieSort.ALPHABETICAL){
            movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAllSorted("title", Sort.ASCENDING);
        }else {
            movieRealmResults = mUiRealm.where(Movie.class).equalTo("onWatchList", true).findAll();
        }
        Log.d(TAG, "movieRealmResults size: " + Integer.toString(movieRealmResults.size()));

        mMovieList = new RealmList<>();
        for (Movie movieResult : movieRealmResults) {
            if(mUiRealm.where(ArchivedMovies.class).equalTo(mContext.getString(R.string.movieId), movieResult.getId()).count() == 0) {
                mMovieList.add(movieResult);
            }
        }
        Log.d(TAG, "mMovieList size: " + Integer.toString(mMovieList.size()));

        notifyDataSetChanged();
    }
}