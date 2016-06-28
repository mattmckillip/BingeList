package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
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
import com.example.matt.bingeList.models.MultiSearchResult;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.viewControllers.activities.movies.BrowseMovieDetailActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
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
    private Realm mUIrealm;

    private Movie mMovie;
    private TVShow mTvShow;
    private Credits mCredits;


    public MultiSearchAdapter(List<MultiSearchResult> results, Context context, Realm uiRealm) {
        mMultiSearchResults = results;
        mContext = context;
        mUIrealm = uiRealm;
    }

    @Override
    public int getItemCount() {
        return mMultiSearchResults.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder searchViewHolder, int i) {
        MultiSearchResult result = mMultiSearchResults.get(i);

        searchViewHolder.progressSpinner.setVisibility(View.GONE);
        searchViewHolder.watchedLayout.setVisibility(View.GONE);
        searchViewHolder.watchListLayout.setVisibility(View.GONE);

        if (result.getMediaType().equals(MOVIE_TYPE)) {
            searchViewHolder.mediaTitle.setText(result.getTitle());
            searchViewHolder.actionButton.setText("{gmd_add_to_queue} add to watchlist");

            if (mUIrealm.where(Movie.class).equalTo("id", result.getId()).equalTo("isWatched", true).findAll().size() == 1) {
                searchViewHolder.watchedLayout.setVisibility(View.VISIBLE);
                searchViewHolder.actionButton.setVisibility(View.GONE);
            } else if (mUIrealm.where(Movie.class).equalTo("id", result.getId()).equalTo("onWatchList", true).findAll().size() == 1) {
                searchViewHolder.watchedLayout.setVisibility(View.VISIBLE);
                searchViewHolder.actionButton.setVisibility(View.GONE);
            }
        } else if (result.getMediaType().equals(SHOW_TYPE)) {
            searchViewHolder.mediaTitle.setText(result.getName());
            searchViewHolder.actionButton.setText("{gmd_add_to_queue} add to your shows");

            if (mUIrealm.where(TVShow.class).equalTo("id", result.getId()).equalTo("isWatched", true).findAll().size() == 1) {
                searchViewHolder.watchedLayout.setVisibility(View.VISIBLE);
                searchViewHolder.actionButton.setVisibility(View.GONE);
            } else if (mUIrealm.where(TVShow.class).equalTo("id", result.getId()).equalTo("onWatchList", true).findAll().size() == 1) {
                searchViewHolder.watchedLayout.setVisibility(View.VISIBLE);
                searchViewHolder.actionButton.setVisibility(View.GONE);
            }
        }

        if (result.getOverview() != null) {
            searchViewHolder.mediaDescription.setText(result.getOverview().toString());
        }

        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/" + mContext.getString(R.string.image_size_w500) + result.getBackdropPath())
                .error(R.drawable.generic_movie_background)
                .into(searchViewHolder.mediaImage);
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_more_options_card, viewGroup, false);

        return new SearchViewHolder(itemView);
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_title)
        TextView mediaTitle;

        @BindView(R.id.card_text)
        TextView mediaDescription;

        @BindView(R.id.card_image)
        ImageView mediaImage;

        @BindView(R.id.action_button)
        IconicsButton actionButton;

        @BindView(R.id.watched_layout)
        RelativeLayout watchedLayout;

        @BindView(R.id.watch_list_layout)
        RelativeLayout watchListLayout;

        @BindView(R.id.progress_spinner)
        ProgressBar progressSpinner;

        @BindView(R.id.more_button)
        ImageButton mMoreOptionsButton;

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
                        intent.putExtra("movieId", result.getId());
                        context.startActivity(intent);
                    } else if (result.getMediaType().equals(SHOW_TYPE)) {
                        Log.d(TAG, "showId" + Integer.toString(result.getId()));
                        Intent intent = new Intent(context, TVShowBrowseDetailActivity.class);
                        intent.putExtra("showID", result.getId());
                        intent.putExtra("showName", result.getName());
                        context.startActivity(intent);
                    } else {
                        Log.d(TAG, "ERROR");
                    }
                }
            });

            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    progressSpinner.setVisibility(View.VISIBLE);
                    MultiSearchResult result = mMultiSearchResults.get(getAdapterPosition());

                    if (result.getMediaType().equals(MOVIE_TYPE)) {
                        final int movieID = result.getId();

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

                                                mUIrealm.beginTransaction();
                                                mMovie.setOnWatchList(true);
                                                mUIrealm.copyToRealmOrUpdate(mMovie);
                                                mUIrealm.copyToRealmOrUpdate(mCredits);
                                                mUIrealm.commitTransaction();

                                                watchListLayout.setVisibility(View.VISIBLE);
                                                progressSpinner.setVisibility(View.GONE);

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
                        });
                    }
                }
            });
        }
    }
}
