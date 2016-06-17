package com.example.matt.movieWatchList.viewControllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.Cast;
import com.example.matt.movieWatchList.Models.POJO.Credits;
import com.example.matt.movieWatchList.Models.POJO.Crew;
import com.example.matt.movieWatchList.Models.POJO.movies.Movie;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.MyApplication;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.API.MovieAPI;
import com.example.matt.movieWatchList.viewControllers.activities.movies.BrowseMoviesDetailActivity;
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
 * Created by Matt on 6/12/2016.
 */
public class BrowseMoviesAdapter extends RecyclerView.Adapter<BrowseMoviesAdapter.BrowseMoviesViewHolder> {
    private RealmList<JSONMovie> movieList;
    private Activity activity;

    public BrowseMoviesAdapter(RealmList<JSONMovie> movieList, Activity activity) {
        this.movieList = movieList;
        this.activity = activity;
    }

    @Override
    public BrowseMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_card, viewGroup, false);

        return new  BrowseMoviesViewHolder(itemView, activity, movieList);
    }

    @Override
    public void onBindViewHolder(BrowseMoviesViewHolder holder, final int position) {
        final TextView title = (TextView) holder.itemView.findViewById(R.id.card_title);
        TextView overview = (TextView) holder.itemView.findViewById(R.id.card_text);
        final ImageView coverArt = (ImageView) holder.itemView.findViewById(R.id.card_image);
        title.setVisibility(View.GONE);
        holder.itemView.findViewById(R.id.watched_layout).setVisibility(View.GONE);
        holder.itemView.findViewById(R.id.watch_list_layout).setVisibility(View.GONE);

        String path = movieList.get(position).getBackdropURL();

        if (path != null) {
            Picasso.with(activity.getApplicationContext()).load(path).into(coverArt, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) coverArt.getDrawable()).getBitmap(); // Ew!
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    movieList.get(position).setBackdropBitmap(stream.toByteArray());
                }

                @Override
                public void onError() {
                }
            });
            title.setVisibility(View.VISIBLE);
        }

        title.setText(movieList.get(position).getTitle());
        Typeface type = Typeface.createFromAsset(this.activity.getAssets(), "fonts/Lobster-Regular.ttf");
        title.setTypeface(type);
        overview.setText(movieList.get(position).getOverview());

        // Build the query looking at all users:
        Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();

        RealmQuery<JSONMovie> watchedQuery = uiRealm.where(JSONMovie.class);
        RealmResults<JSONMovie> watchedMovies = watchedQuery.equalTo("isWatched", true).equalTo("id",movieList.get(position).getId()).findAll();
        if (watchedMovies.size() == 1) {
            holder.itemView.findViewById(R.id.watched_layout).setVisibility(View.VISIBLE);
        }

        RealmQuery<JSONMovie> watchListQuery = uiRealm.where(JSONMovie.class);
        RealmResults<JSONMovie> watchListMovies = watchListQuery.equalTo("onWatchList", true).equalTo("id",movieList.get(position).getId()).findAll();

        if (watchListMovies.size() == 1) {
            holder.itemView.findViewById(R.id.watch_list_layout).setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class BrowseMoviesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_title)
        TextView movieTitle;

        @BindView(R.id.card_image)
        ImageView movieImage;

        @BindView(R.id.card_text)
        TextView movieDescription;

        @BindView(R.id.watched_layout)
        RelativeLayout watchedLayout;

        @BindView(R.id.watch_list_layout)
        RelativeLayout watchListLayout;

        @BindView(R.id.more_button)
        ImageButton moreOptionsButton;


        public BrowseMoviesViewHolder(View v, final Activity activity, final List<JSONMovie> movieList) {
            super(v);

            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    JSONMovie movie = movieList.get(getAdapterPosition());

                    Intent intent = new Intent(context, BrowseMoviesDetailActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    context.startActivity(intent);
                }
            });

            moreOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(activity, moreOptionsButton);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Log.d("More options", "clicked");
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });//closing the setOnClickListener method


            Button button = (Button)itemView.findViewById(R.id.action_button);
            button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(final View v) {
                    final int movieID = movieList.get(getAdapterPosition()).getId();


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

                                            Realm uiRealm = ((MyApplication) activity.getApplication()).getUiRealm();
                                            uiRealm.beginTransaction();
                                            realmMovie.setOnWatchList(true);
                                            //JSONMovie movieToAdd = uiRealm.createObject(movie);
                                            uiRealm.copyToRealm(realmMovie);
                                            uiRealm.commitTransaction();

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

                                    Picasso.with(activity.getApplicationContext())
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
            });
        }
    }
}