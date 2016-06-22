package com.example.matt.movieWatchList.viewControllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.Cast;
import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.Crew;
import com.example.matt.movieWatchList.Models.POJO.shows.TVShow;
import com.example.matt.movieWatchList.Models.POJO.shows.TVShowSeasonResult;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONEpisode;
import com.example.matt.movieWatchList.Models.Realm.JSONSeason;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.MovieAPI;
import com.example.matt.movieWatchList.uitls.API.TVShowAPI;
import com.example.matt.movieWatchList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
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
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Matt on 6/12/2016.
 */
public class BrowseTVShowsAdapter extends RecyclerView.Adapter<BrowseTVShowsAdapter.BrowseTVShowsViewHolder> {
    private RealmList<JSONShow> showList;
    private Activity activity;
    private JSONShow realmShow;


    public BrowseTVShowsAdapter(RealmList<JSONShow> showList, Activity activity) {
        this.showList = showList;
        this.activity = activity;
    }

    @Override
    public BrowseTVShowsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_card, viewGroup, false);

        return new BrowseTVShowsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BrowseTVShowsViewHolder holder, final int position) {
        holder.progressSpinner.setVisibility(View.GONE);
        holder.watchedLayout.setVisibility(View.GONE);
        holder.watchListLayout.setVisibility(View.GONE);
        holder.showName.setVisibility(View.GONE);
        String path = showList.get(position).getBackdropPath();

        if (path != null) {
            Picasso.with(activity.getApplicationContext()).load(path).into(holder.showImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) holder.showImage.getDrawable()).getBitmap(); // Ew!
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    showList.get(position).setBackdropBitmap(stream.toByteArray());
                }

                @Override
                public void onError() {
                }
            });
            holder.showName.setVisibility(View.VISIBLE);
        }

        holder.showName.setText(showList.get(position).getName());
        holder.showDescription.setText(showList.get(position).getOverview());

        // Build the query looking at all users:
        Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();

        RealmResults<JSONShow> watchedShows = uiRealm.where(JSONShow.class).equalTo("isWatched", true).equalTo("id", showList.get(position).getId()).findAll();
        if (watchedShows.size() == 1) {
            holder.watchedLayout.setVisibility(View.VISIBLE);
            holder.actionButton.setVisibility(View.GONE);
        }

        RealmResults<JSONShow> watchListShows = uiRealm.where(JSONShow.class).equalTo("onWatchList", true).equalTo("id", showList.get(position).getId()).findAll();
        if (watchListShows.size() == 1) {
            holder.watchListLayout.setVisibility(View.VISIBLE);
            holder.actionButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    public class BrowseTVShowsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_title)
        TextView showName;

        @BindView(R.id.card_image)
        ImageView showImage;

        @BindView(R.id.card_text)
        TextView showDescription;

        @BindView(R.id.watched_layout)
        RelativeLayout watchedLayout;

        @BindView(R.id.watch_list_layout)
        RelativeLayout watchListLayout;

        @BindView(R.id.action_button)
        IconicsButton actionButton;

        @BindView(R.id.progress_spinner)
        ProgressBar progressSpinner;


        public BrowseTVShowsViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    JSONShow show = showList.get(getAdapterPosition());

                    Intent intent = new Intent(context, TVShowBrowseDetailActivity.class);
                    intent.putExtra("showID", show.getId());
                    intent.putExtra("showName", show.getName());
                    context.startActivity(intent);
                }
            });

            actionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    progressSpinner.setVisibility(View.VISIBLE);

                    final int showID = showList.get(getAdapterPosition()).getId();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://api.themoviedb.org/3/tv/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    TVShowAPI service = retrofit.create(TVShowAPI.class);
                    Call<TVShow> call = service.getTVShow(Integer.toString(showID));

                    call.enqueue(new Callback<TVShow>() {
                        @Override
                        public void onResponse(retrofit.Response<TVShow> response, Retrofit retrofit) {
                            TVShow show = response.body();
                            show.setBackdropPath("https://image.tmdb.org/t/p/w780/" + show.getBackdropPath());
                            realmShow = show.convertToRealm();

                            MovieAPI service = retrofit.create(MovieAPI.class);
                            Call<Credits> call = service.getCredits(Integer.toString(showID));

                            call.enqueue(new Callback<Credits>() {
                                @Override
                                public void onResponse(retrofit.Response<Credits> response, Retrofit retrofit) {
                                    List<Cast> cast = response.body().getCast();
                                    List<Crew> crew = response.body().getCrew();

                                    RealmList<JSONCast> realmCast = new RealmList<>();
                                    for (Cast castMember : cast) {
                                        realmCast.add(castMember.convertToRealm());
                                    }

                                    RealmList<JSONCast> realmCrew = new RealmList<>();
                                    for (Crew crewMember : crew) {
                                        realmCrew.add(crewMember.convertToRealm());
                                    }

                                    Target target = new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            realmShow.setBackdropBitmap(stream.toByteArray());

                                            Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();
                                            uiRealm.beginTransaction();
                                            realmShow.setOnWatchList(true);

                                            uiRealm.copyToRealm(realmShow);
                                            uiRealm.commitTransaction();

                                            watchListLayout.setVisibility(View.VISIBLE);

                                            FetchSeasonsTask fetchSeasonsTask = new FetchSeasonsTask();
                                            fetchSeasonsTask.execute(showID, realmShow.getNumberOfSeasons());

                                            Snackbar.make(v, "Added to your shows!",
                                                    Snackbar.LENGTH_LONG).show();

                                            progressSpinner.setVisibility(View.GONE);
                                            actionButton.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                            Log.d("onBitmapFailed()", realmShow.getBackdropPath());
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    };
                                    realmShow.setCrew(realmCrew);
                                    realmShow.setCast(realmCast);

                                    Picasso.with(activity.getApplicationContext())
                                            .load(realmShow.getBackdropPath())
                                            .into(target);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Log.d("GetCredits()", "Callback Failure");
                                }
                            });
                            //TODOgenre
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d("getMovie()", "Callback Failure");
                        }
                    });
                }
            });
        }
    }
    public void UpdateRealmSeasons(ArrayList<TVShowSeasonResult> seasons, Integer showID) {
        RealmList<JSONSeason> jsonSeasonRealmList = new RealmList<>();
        for (TVShowSeasonResult season: seasons) {
            JSONSeason realmSeason = season.convertToRealm();
            jsonSeasonRealmList.add(realmSeason);

            RealmList<JSONEpisode> jsonEpisodeRealmList = realmSeason.getEpisodes();
            for (JSONEpisode episode: jsonEpisodeRealmList) {
                episode.setShow_id(showID);
            }
        }

        Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();
        uiRealm.beginTransaction();
        realmShow.setSeasons(jsonSeasonRealmList);
        uiRealm.copyToRealmOrUpdate(realmShow);
        uiRealm.commitTransaction();
    }

    private class FetchSeasonsTask extends AsyncTask<Integer, Integer, ArrayList<TVShowSeasonResult>> {
        private Integer showID;
        protected ArrayList<TVShowSeasonResult> doInBackground(Integer... params) {
            showID = params[0];
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
                Call<TVShowSeasonResult> call = service.getSeasons(Integer.toString(showID), Integer.toString(i));
                try {
                    seasons.add(call.execute().body());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return seasons;
        }

        protected void onPostExecute(ArrayList<TVShowSeasonResult> result) {
            UpdateRealmSeasons(result, showID);
        }
    }
}