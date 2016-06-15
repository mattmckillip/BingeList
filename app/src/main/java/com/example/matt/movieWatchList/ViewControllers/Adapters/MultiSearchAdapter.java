package com.example.matt.movieWatchList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.MultiSearchResult;
import com.example.matt.movieWatchList.Models.POJO.movies.MovieResult;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.viewControllers.activities.movies.BrowseMoviesDetailActivity;
import com.example.matt.movieWatchList.viewControllers.activities.shows.BrowseTVShowsDetailActivity;
import com.example.matt.movieWatchList.viewControllers.activities.shows.TVShowWatchListDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Matt on 6/11/2016.
 */
public class MultiSearchAdapter extends RecyclerView.Adapter<MultiSearchAdapter.SearchViewHolder> {

    private List<MultiSearchResult> results;
    private Context context;

    public MultiSearchAdapter(List<MultiSearchResult> results, Context context) {
        this.results = results;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder searchViewHolder, int i) {
        MultiSearchResult result = results.get(i);
        searchViewHolder.movieTitle.setText(result.getTitle());
        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/Lobster-Regular.ttf");
        searchViewHolder.movieTitle.setTypeface(type);
        if (result.getOverview() != null) {
            searchViewHolder.movieDescription.setText(result.getOverview().toString());
        }
        searchViewHolder.watchedLayout.setVisibility(View.GONE);
        searchViewHolder.watchListLayout.setVisibility(View.GONE);

        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w342/" + result.getBackdropPath())
                //.placeholder(R.drawable.unkown_person)
                .error(R.drawable.generic_movie_background)
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

            movieTitle =  (TextView) v.findViewById(R.id.card_title);
            movieDescription = (TextView)  v.findViewById(R.id.card_text);
            movieImage = (ImageView)  v.findViewById(R.id.card_image);

            watchedLayout = (RelativeLayout)  v.findViewById(R.id.watched_layout);
            watchListLayout = (RelativeLayout)  v.findViewById(R.id.watch_list_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    MultiSearchResult result = results.get(getAdapterPosition());

                    if (result.getMediaType().equals("movie")) {
                        Intent intent = new Intent(context, BrowseMoviesDetailActivity.class);
                        intent.putExtra("movieId", result.getId());
                        context.startActivity(intent);
                    } else if (result.getMediaType().equals("tv")) {
                        Intent intent = new Intent(context, BrowseTVShowsDetailActivity.class);
                        intent.putExtra("tvShowID", result.getId());
                        context.startActivity(intent);
                    }
                    else {
                        Log.d("CLICK", "ERROR");
                    }
                }
            });;
        }
    }
}

