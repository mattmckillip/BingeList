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
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.viewControllers.activities.movies.WatchlistDetailActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;


public class MovieWatchedAdapter extends RecyclerView.Adapter<MovieWatchedAdapter.WatchedMovieViewHolder> {
    private RealmResults<Movie> mMovieList;
    private static final String TAG = MoviesWatchListAdapter.class.getSimpleName();
    private Context mContext;
    private Realm mUiRealm;

    public MovieWatchedAdapter(RealmResults movieList, Context context, Realm uiRealm) {
        mContext = context;
        mUiRealm = uiRealm;
        mMovieList = movieList;
    }

    @Override
    public WatchedMovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_more_options_card, viewGroup, false);

        return new WatchedMovieViewHolder(itemView, mContext, mMovieList);
    }

    @Override
    public void onBindViewHolder(WatchedMovieViewHolder holder, final int position) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onBindViewHolder()");
        }

        holder.mProgressSpinner.setVisibility(View.GONE);
        holder.mWatchedLayout.setVisibility(View.GONE);
        holder.mWatchListLayout.setVisibility(View.GONE);

        if (mMovieList.get(position).getBackdropBitmap() != null) {
            Bitmap bmp;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            bmp = BitmapFactory.decodeByteArray(mMovieList.get(position).getBackdropBitmap(), 0, mMovieList.get(position).getBackdropBitmap().length, options);
            holder.mMovieImage.setImageBitmap(bmp);
        }

        holder.mMoreOptionsButton.setImageDrawable(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_more_vert).sizeDp(16));
        holder.mMovieTitle.setText(mMovieList.get(position).getTitle());
        holder.mMovieDescription.setText(mMovieList.get(position).getOverview());
        holder.mUnWatchButton.setText(mContext.getString(R.string.unwatch_button));
        holder.mUnWatchButton.setTextColor(ContextCompat.getColor(mContext, R.color.accent));

        holder.mUnWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = mMovieList.get(position);
                String movieTitle = movie.getTitle();

                mUiRealm.beginTransaction();
                movie.setOnWatchList(true);
                movie.setWatched(false);
                mUiRealm.copyToRealmOrUpdate(movie);
                mUiRealm.commitTransaction();

                notifyDataSetChanged();

                Snackbar.make(v, movieTitle + " moved to watchlist!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public static class WatchedMovieViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = WatchedMovieViewHolder.class.getSimpleName();
        private Context mContext;
        private List<Movie> mMovieList;

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


        public WatchedMovieViewHolder(View v, Context context, final List<Movie> movieList) {
            super(v);
            mContext = context;
            mMovieList = movieList;

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