package com.example.matt.movieWatchList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.Cast;
import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.Crew;
import com.example.matt.movieWatchList.Models.POJO.MultiSearchResult;
import com.example.matt.movieWatchList.Models.POJO.movies.Movie;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.Models.Realm.JSONShow;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.MovieAPI;
import com.example.matt.movieWatchList.viewControllers.activities.movies.MovieBrowseDetailActivity;
import com.example.matt.movieWatchList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
import com.mikepenz.iconics.view.IconicsButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


public class MultiSearchAdapter extends RecyclerView.Adapter<MultiSearchAdapter.SearchViewHolder> {
    private static final String MOVIE_TYPE = "movie";
    private static final String SHOW_TYPE = "tv";

    private List<MultiSearchResult> multiSearchResults;
    private Context mContext;
    private Realm mUIrealm;

    public MultiSearchAdapter(List<MultiSearchResult> results, Context context, Realm uiRealm) {
        multiSearchResults = results;
        mContext = context;
        mUIrealm = uiRealm;
    }

    @Override
    public int getItemCount() {
        return multiSearchResults.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder searchViewHolder, int i) {
        MultiSearchResult result = multiSearchResults.get(i);

        if (result.getMediaType().equals(MOVIE_TYPE)) {
            searchViewHolder.mediaTitle.setText(result.getTitle());
        } else if (result.getMediaType().equals(SHOW_TYPE)) {
            searchViewHolder.mediaTitle.setText(result.getName());
        }

        if (result.getOverview() != null) {
            searchViewHolder.mediaDescription.setText(result.getOverview().toString());
        }

        searchViewHolder.watchedLayout.setVisibility(View.GONE);
        searchViewHolder.watchListLayout.setVisibility(View.GONE);

        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/w342/" + result.getBackdropPath())
                //.placeholder(R.drawable.unkown_person)
                .error(R.drawable.generic_movie_background)
                .into(searchViewHolder.mediaImage);
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_card, viewGroup, false);

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

        public SearchViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    MultiSearchResult result = multiSearchResults.get(getAdapterPosition());

                    if (result.getMediaType().equals(MOVIE_TYPE)) {
                        Intent intent = new Intent(context, MovieBrowseDetailActivity.class);
                        intent.putExtra("movieId", result.getId());
                        context.startActivity(intent);
                    } else if (result.getMediaType().equals(SHOW_TYPE)) {
                        Log.d("SHOWID", Integer.toString(result.getId()));
                        Intent intent = new Intent(context, TVShowBrowseDetailActivity.class);
                        intent.putExtra("showID", result.getId());
                        intent.putExtra("showName", result.getName());
                        context.startActivity(intent);
                    } else {
                        Log.d("CLICK", "ERROR");
                    }
                }
            });

            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    MultiSearchResult result = multiSearchResults.get(getAdapterPosition());

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
                            public void onResponse(retrofit.Response<Movie> response, Retrofit retrofit) {
                                Log.d("getMovie()", "Callback Success");
                                Movie movie = response.body();
                                movie.setBackdropPath("https://image.tmdb.org/t/p/w780/" + movie.getBackdropPath());
                                final JSONMovie realmMovie = movie.convertToRealm();

                                MovieAPI service = retrofit.create(MovieAPI.class);
                                Call<Credits> call = service.getCredits(Integer.toString(movieID));

                                call.enqueue(new Callback<Credits>() {
                                    @Override
                                    public void onResponse(retrofit.Response<Credits> response, Retrofit retrofit) {
                                        Log.d("GetCredits()", "Callback Success");
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
                                                realmMovie.setBackdropBitmap(stream.toByteArray());

                                                mUIrealm.beginTransaction();
                                                realmMovie.setOnWatchList(true);
                                                //JSONMovie movieToAdd = uiRealm.createObject(movie);
                                                mUIrealm.copyToRealm(realmMovie);
                                                mUIrealm.commitTransaction();

                                                watchListLayout.setVisibility(View.VISIBLE);

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
                                                .load(realmMovie.getBackdropURL())
                                                .into(target);

                                        realmMovie.setCrew(realmCrew);
                                        realmMovie.setCast(realmCast);


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
                }
            });
        }
    }
}

