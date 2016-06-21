package com.example.matt.movieWatchList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.movies.MovieResult;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.viewControllers.activities.movies.MovieBrowseDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Matt on 6/11/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<MovieResult> movies;
    private Context context;

    public SearchAdapter(List<MovieResult> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder searchViewHolder, int i) {
        MovieResult movie = movies.get(i);
        searchViewHolder.movieTitle.setText(movie.getTitle());
        searchViewHolder.movieDescription.setText(movie.getOverview());
        searchViewHolder.watchedLayout.setVisibility(View.GONE);
        searchViewHolder.watchListLayout.setVisibility(View.GONE);

        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w342/" + movie.getBackdropPath())
                //.placeholder(R.drawable.unkown_person)
                //.error(R.drawable.generic_movie_background)
                .into(searchViewHolder.movieImage);
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_card, viewGroup, false);

        return new SearchViewHolder(itemView);
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        protected TextView movieTitle;
        protected TextView movieDescription;
        protected ImageView movieImage;
        protected RelativeLayout watchedLayout;
        protected RelativeLayout watchListLayout;

        public SearchViewHolder(View v) {
            super(v);

            movieTitle = (TextView) v.findViewById(R.id.card_title);
            movieDescription = (TextView) v.findViewById(R.id.card_text);
            movieImage = (ImageView) v.findViewById(R.id.card_image);

            watchedLayout = (RelativeLayout) v.findViewById(R.id.watched_layout);
            watchListLayout = (RelativeLayout) v.findViewById(R.id.watch_list_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    MovieResult movie = movies.get(getAdapterPosition());

                    Intent intent = new Intent(context, MovieBrowseDetailActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    context.startActivity(intent);
                }
            });
            ;
        }
    }
}

