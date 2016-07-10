package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.Season;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.shows.TVShowSeasonResult;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.viewControllers.activities.movies.BrowseMovieDetailActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
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


    public MultiSearchAdapter(List<MultiSearchResult> results, Context context, Realm uiRealm) {
        mMultiSearchResults = results;
        mContext = context;
        mUiRealm = uiRealm;
    }

    @Override
    public int getItemCount() {
        return mMultiSearchResults.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder searchViewHolder, int position) {
        MultiSearchResult result = mMultiSearchResults.get(position);

        searchViewHolder.progressSpinner.setVisibility(View.GONE);
        searchViewHolder.watchedLayout.setVisibility(View.GONE);
        searchViewHolder.watchListLayout.setVisibility(View.GONE);

        if (result.getMediaType().equals(MOVIE_TYPE)) {
            searchViewHolder.mediaTitle.setText(result.getTitle());
            searchViewHolder.actionButton.setText("{gmd_add} add to watchlist");

            if (mUiRealm.where(Movie.class).equalTo("id", result.getId()).equalTo("isWatched", true).findAll().size() == 1) {
                searchViewHolder.watchedLayout.setVisibility(View.VISIBLE);
                searchViewHolder.actionButton.setVisibility(View.GONE);
            } else if (mUiRealm.where(Movie.class).equalTo("id", result.getId()).equalTo("onWatchList", true).findAll().size() == 1) {
                searchViewHolder.watchedLayout.setVisibility(View.VISIBLE);
                searchViewHolder.actionButton.setVisibility(View.GONE);
            }
        } else if (result.getMediaType().equals(SHOW_TYPE)) {
            searchViewHolder.mediaTitle.setText(result.getName());
            searchViewHolder.actionButton.setText("{gmd_add} add to your shows");

            if (mUiRealm.where(TVShow.class).equalTo("id", result.getId()).equalTo("onYourShows", true).findAll().size() == 1) {
                searchViewHolder.watchedLayout.setVisibility(View.VISIBLE);
                searchViewHolder.actionButton.setVisibility(View.GONE);
            }
        }

        if (result.getOverview() != null) {
            searchViewHolder.mediaDescription.setText(result.getOverview().toString());
        }

        Picasso.with(mContext)
                .load( mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + result.getBackdropPath())
                .error(R.drawable.generic_movie_background)
                .into(searchViewHolder.mediaImage);

        setListeners(searchViewHolder, position);
    }

    private void setListeners(final SearchViewHolder holder, final int position) {
        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                holder.progressSpinner.setVisibility(View.VISIBLE);
                MultiSearchResult result = mMultiSearchResults.get(position);

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

                                            mUiRealm.beginTransaction();
                                            mMovie.setOnWatchList(true);
                                            mUiRealm.copyToRealmOrUpdate(mMovie);
                                            mUiRealm.copyToRealmOrUpdate(mCredits);
                                            mUiRealm.commitTransaction();

                                            holder.watchListLayout.setVisibility(View.VISIBLE);
                                            holder.progressSpinner.setVisibility(View.GONE);

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
                                        Log.d(TAG, "byte array " + Integer.toString(stream.toByteArray().length));

                                        mUiRealm.beginTransaction();
                                        mShow.setBackdropBitmap(stream.toByteArray());
                                        mShow.setOnYourShows(true);
                                        mUiRealm.copyToRealmOrUpdate(mShow);
                                        mUiRealm.commitTransaction();

                                        notifyDataSetChanged();

                                        FetchSeasonsTask fetchSeasonsTask = new FetchSeasonsTask();
                                        Log.d(TAG, Integer.toString(showId));
                                        Log.d(TAG, Integer.toString(mShow.getNumberOfSeasons()));
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
            }
        });
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
                        intent.putExtra("mShowName", result.getName());
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
