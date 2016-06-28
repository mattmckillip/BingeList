package com.example.matt.bingeList.viewControllers.adapters;

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

import com.example.matt.bingeList.models.Cast;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.Crew;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.models.shows.TVShowSeasonResult;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
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
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Matt on 6/12/2016.
 */
public class BrowseTVShowsAdapter extends RecyclerView.Adapter<BrowseTVShowsAdapter.BrowseTVShowsViewHolder> {
    private static final String TAG = BrowseTVShowsAdapter.class.getSimpleName();
    private RealmList<TVShow> mShowList;
    private Activity activity;
    private TVShow mShow;


    public BrowseTVShowsAdapter(RealmList<TVShow> showList, Activity activity) {
        this.mShowList = showList;
        this.activity = activity;
    }

    @Override
    public BrowseTVShowsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_more_options_card, viewGroup, false);

        return new BrowseTVShowsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BrowseTVShowsViewHolder holder, final int position) {
        holder.progressSpinner.setVisibility(View.GONE);
        holder.watchedLayout.setVisibility(View.GONE);
        holder.watchListLayout.setVisibility(View.GONE);
        holder.showName.setVisibility(View.GONE);
        String path = mShowList.get(position).getBackdropPath();

        if (path != null) {
            Picasso.with(activity.getApplicationContext()).load(path).into(holder.showImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) holder.showImage.getDrawable()).getBitmap(); // Ew!
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    mShowList.get(position).setBackdropBitmap(stream.toByteArray());
                }

                @Override
                public void onError() {
                }
            });
            holder.showName.setVisibility(View.VISIBLE);
        }

        holder.showName.setText(mShowList.get(position).getName());
        holder.showDescription.setText(mShowList.get(position).getOverview());

        // Build the query looking at all users:
        Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();

        RealmResults<TVShow> watchedShows = uiRealm.where(TVShow.class).equalTo("isWatched", true).equalTo("id", mShowList.get(position).getId()).findAll();
        if (watchedShows.size() == 1) {
            holder.watchedLayout.setVisibility(View.VISIBLE);
            holder.actionButton.setVisibility(View.GONE);
        }

        RealmResults<TVShow> watchListShows = uiRealm.where(TVShow.class).equalTo("onWatchList", true).equalTo("id", mShowList.get(position).getId()).findAll();
        if (watchListShows.size() == 1) {
            holder.watchListLayout.setVisibility(View.VISIBLE);
            holder.actionButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mShowList.size();
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
                    TVShow selectedShow = mShowList.get(getAdapterPosition());

                    Intent intent = new Intent(context, TVShowBrowseDetailActivity.class);
                    intent.putExtra("showID", selectedShow.getId());
                    intent.putExtra("showName", selectedShow.getName());
                    context.startActivity(intent);
                }
            });

            actionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    progressSpinner.setVisibility(View.VISIBLE);

                    final int showID = mShowList.get(getAdapterPosition()).getId();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://api.themoviedb.org/3/tv/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    TVShowAPI service = retrofit.create(TVShowAPI.class);
                    Call<TVShow> call = service.getTVShow(Integer.toString(showID));

                    call.enqueue(new Callback<TVShow>() {
                        @Override
                        public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                            mShow = response.body();
                            mShow.setBackdropPath("https://image.tmdb.org/t/p/w780" + mShow.getBackdropPath());

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://api.themoviedb.org/3/movie/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            MovieAPI service = retrofit.create(MovieAPI.class);
                            Call<Credits> creditsCall = service.getCredits(Integer.toString(showID));

                            creditsCall.enqueue(new Callback<Credits>() {
                                @Override
                                public void onResponse(Call<Credits> call, Response<Credits> response) {
                                    List<Cast> cast = response.body().getCast();
                                    List<Crew> crew = response.body().getCrew();

                                    Target target = new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            mShow.setBackdropBitmap(stream.toByteArray());

                                            Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();
                                            uiRealm.beginTransaction();
                                            mShow.setOnWatchList(true);

                                            uiRealm.copyToRealm(mShow);
                                            uiRealm.commitTransaction();

                                            watchListLayout.setVisibility(View.VISIBLE);

                                            FetchSeasonsTask fetchSeasonsTask = new FetchSeasonsTask();
                                            fetchSeasonsTask.execute(showID, mShow.getNumberOfSeasons());

                                            Snackbar.make(v, "Added to your shows!",
                                                    Snackbar.LENGTH_LONG).show();

                                            progressSpinner.setVisibility(View.GONE);
                                            actionButton.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                            Log.d("onBitmapFailed()", mShow.getBackdropPath());
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    };
                                    /*realmShow.setCrew(realmCrew);
                                    realmShow.setCast(realmCast);*/

                                    Picasso.with(activity.getApplicationContext())
                                            .load(mShow.getBackdropPath())
                                            .into(target);
                                }

                                @Override
                                public void onFailure(Call<Credits> call, Throwable t) {
                                    Log.d("GetCredits()", "Callback Failure");
                                }
                            });
                            //TODOgenre
                        }

                        @Override
                        public void onFailure(Call<TVShow> call, Throwable t) {
                            Log.d("getMovie()", "Callback Failure");
                        }
                    });
                }
            });
        }
    }
    public void UpdateRealmSeasons(ArrayList<TVShowSeasonResult> seasons, Integer showID) {
        /*RealmList<JSONSeason> jsonSeasonRealmList = new RealmList<>();
        for (TVShowSeasonResult season: seasons) {
            JSONSeason realmSeason = season.convertToRealm();
            jsonSeasonRealmList.add(realmSeason);

            RealmList<JSONEpisode> jsonEpisodeRealmList = realmSeason.getEpisodes();
            for (JSONEpisode episode: jsonEpisodeRealmList) {
                episode.setShow_id(showID);
            }
        }

        Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();*/
        /*uiRealm.beginTransaction();
        realmShow.setSeasons(jsonSeasonRealmList);
        uiRealm.copyToRealmOrUpdate(realmShow);
        uiRealm.commitTransaction();*/
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