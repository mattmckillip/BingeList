/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.matt.movieWatchList.viewControllers.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.MovieResult;
import com.example.matt.movieWatchList.Models.POJO.MovieQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.Cast;
import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.Crew;
import com.example.matt.movieWatchList.Models.POJO.Movie;
import com.example.matt.movieWatchList.Models.POJO.TVShowQueryReturn;
import com.example.matt.movieWatchList.Models.POJO.TVShowResult;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.BrowseTVShowsAPI;
import com.example.matt.movieWatchList.viewControllers.activities.TmdbActivity;
import com.example.matt.movieWatchList.uitls.BrowseMovieType;
import com.example.matt.movieWatchList.uitls.MovieAPI;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Provides UI for the view with Cards.
 */
public class BrowseTVShowsFragment extends Fragment {
    private RealmList<JSONMovie> browseMoviesList;
    private static RecyclerView recyclerView;
    private ContentAdapter adapter;
    private static Realm uiRealm;

    private Integer movieType;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static JSONMovie realmMovie;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieType = getArguments().getInt("movieType");
        browseMoviesList = new RealmList<JSONMovie>();
        uiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        adapter = new ContentAdapter(browseMoviesList, getActivity());
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(castLayoutManager);
        this.recyclerView = recyclerView;

        loadData();

        return recyclerView;
    }

    public void loadData() {
        //return recyclerView;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/tv/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BrowseTVShowsAPI service = retrofit.create(BrowseTVShowsAPI.class);

        Call<TVShowQueryReturn> call;
        if (movieType == BrowseMovieType.POPULAR) {
            call = service.getPopularTVShows();
        } else if (movieType == BrowseMovieType.NOW_SHOWING) {
            call = service.getAiringTodayTVShows();
        } else if (movieType == BrowseMovieType.TOP_RATED) {
            call = service.getTopRatedTVShows();
        } else {
            call = null;
        }
        if (call != null) {
            call.enqueue(new Callback<TVShowQueryReturn>() {
                @Override
                public void onResponse(retrofit.Response<TVShowQueryReturn> response, Retrofit retrofit) {
                    Log.d("Browsetv()", response.raw().toString());
                    List<TVShowResult> movieResults = response.body().getResults();
                    browseMoviesList = new RealmList<>();
                    for (TVShowResult movie : movieResults){
                        JSONMovie jsonMove = new JSONMovie();
                        jsonMove.setTitle(movie.getName());
                        jsonMove.setId(movie.getId());
                        jsonMove.setOverview(movie.getOverview());
                        jsonMove.setBackdropURL("https://image.tmdb.org/t/p/w300" + movie.getBackdropPath());

                        browseMoviesList.add(jsonMove);
                    }
                    Log.d("BrowseMovies()", Integer.toString(browseMoviesList.size()));
                    recyclerView.setAdapter( new ContentAdapter(browseMoviesList, getActivity()));
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("BrowseMovies()", "Callback Failure");
                }
            });
        }
        else {
            Log.d("call", "null");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.watch_list_layout)
        RelativeLayout watchListLayout;

        @BindView(R.id.watch_list_blur)
        ImageView watch_list_blur;

        public ViewHolder(final LayoutInflater inflater, ViewGroup parent, final RealmList<JSONMovie> movieList, final MyApplication app) {
            super(inflater.inflate(R.layout.item_card, parent, false));
            View view = inflater.inflate(R.layout.item_card, parent, false);

            ButterKnife.bind(this, view);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    JSONMovie movie = movieList.get(getAdapterPosition());

                    Intent intent = new Intent(context, TmdbActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    context.startActivity(intent);
                }
            });

            Button button = (Button)itemView.findViewById(R.id.action_button);
            button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    final int movieID = movieList.get(getAdapterPosition()).getId();

                    watchListLayout.setVisibility(View.VISIBLE);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://api.themoviedb.org/3/movie/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    MovieAPI service = retrofit.create(MovieAPI.class);
                    Call<Movie> call = service.getMovie(Integer.toString(movieID));

                    call.enqueue(new Callback<Movie>() {
                        @Override
                        public void onResponse(retrofit.Response<Movie> response, Retrofit retrofit) {
                            Log.d("getMovie()", "Callback Success");
                            Movie movie = response.body();
                            movie.setBackdropPath("https://image.tmdb.org/t/p/w780//" + movie.getBackdropPath());
                            realmMovie = movie.convertToRealm();

                            MovieAPI service = retrofit.create(MovieAPI.class);
                            Call<Credits> call = service.getCredits(Integer.toString(movieID));

                            call.enqueue(new Callback<Credits>() {
                                @Override
                                public void onResponse(retrofit.Response<Credits> response, Retrofit retrofit) {
                                    Log.d("GetCredits()", "Callback Success");
                                    List<Cast> cast = response.body().getCast();
                                    List<Crew> crew = response.body().getCrew();

                                    RealmList<JSONCast> realmCast = new RealmList<>();
                                    for( Cast castMember : cast) {
                                        realmCast.add(castMember.convertToRealm());
                                    }

                                    RealmList<JSONCast> realmCrew = new RealmList<>();
                                    for( Crew crewMember : crew) {
                                        realmCrew.add(crewMember.convertToRealm());
                                    }

                                    Target target = new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            realmMovie.setBackdropBitmap(stream.toByteArray());
                                            uiRealm.beginTransaction();
                                            realmMovie.setOnWatchList(true);
                                            //JSONMovie movieToAdd = uiRealm.createObject(movie);
                                            uiRealm.copyToRealm(realmMovie);
                                            uiRealm.commitTransaction();
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    };

                                    Picasso.with(recyclerView.getContext())
                                            .load(realmMovie.getBackdropURL())
                                            .into(target);

                                    realmMovie.setCrew(realmCrew);
                                    realmMovie.setCast(realmCast);

                                    Snackbar.make(recyclerView, "Added to watchlist!",
                                            Snackbar.LENGTH_LONG).show();
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

    public static void addMovieToWatchList(JSONMovie movie) {
        if (movie == null){

            Snackbar.make(swipeRefreshLayout, "Error adding to watchlist",
                    Snackbar.LENGTH_LONG).show();
        } else {
            uiRealm.beginTransaction();
            movie.setOnWatchList(true);
            uiRealm.copyToRealm(movie);
            uiRealm.commitTransaction();

            Snackbar.make(swipeRefreshLayout, "Added to watch list!",
                    Snackbar.LENGTH_LONG).show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        RealmList<JSONMovie> popularMovies;
        Activity activity;

        public ContentAdapter(RealmList<JSONMovie> movieList, Activity activity) {

            popularMovies = movieList;
            this.activity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, popularMovies, ((MyApplication) activity.getApplication()));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final TextView title = (TextView) holder.itemView.findViewById(R.id.card_title);
            TextView overview = (TextView) holder.itemView.findViewById(R.id.card_text);
            final ImageView coverArt = (ImageView) holder.itemView.findViewById(R.id.card_image);
            title.setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.watched_layout).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.watch_list_layout).setVisibility(View.GONE);


            String path = popularMovies.get(position).getBackdropURL();

            if (path != null) {
                Picasso.with(activity.getApplicationContext()).load(path).into(coverArt, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) coverArt.getDrawable()).getBitmap(); // Ew!
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        popularMovies.get(position).setBackdropBitmap(stream.toByteArray());
                    }

                    @Override
                    public void onError() {

                    }
                });
                title.setVisibility(View.VISIBLE);
            }

            title.setText(popularMovies.get(position).getTitle());
            Typeface type = Typeface.createFromAsset(this.activity.getAssets(), "fonts/Lobster-Regular.ttf");
            title.setTypeface(type);
            overview.setText(popularMovies.get(position).getOverview());

            // Build the query looking at all users:
            Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();

            RealmQuery<JSONMovie> watchedQuery = uiRealm.where(JSONMovie.class);
            RealmResults<JSONMovie> watchedMovies = watchedQuery.equalTo("isWatched", true).equalTo("id",popularMovies.get(position).getId()).findAll();
            if (watchedMovies.size() == 1) {
                holder.itemView.findViewById(R.id.watched_layout).setVisibility(View.VISIBLE);
            }

            RealmQuery<JSONMovie> watchListQuery = uiRealm.where(JSONMovie.class);
            RealmResults<JSONMovie> watchListMovies = watchListQuery.equalTo("onWatchList", true).equalTo("id",popularMovies.get(position).getId()).findAll();

            if (watchListMovies.size() == 1) {
                holder.itemView.findViewById(R.id.watch_list_layout).setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return popularMovies.size();
        }
    }
}
