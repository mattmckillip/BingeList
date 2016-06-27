package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.viewControllers.activities.movies.MovieBrowseDetailActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SimilarMoviesAdapter extends RecyclerView.Adapter<SimilarMoviesAdapter.SimilarMovieViewHolder> {

    private List<MovieResult> mSimilarMovieList;
    private Context mContext;

    public SimilarMoviesAdapter(List<MovieResult> similarMovieList, Context context, int numberToDisplay) {
        mSimilarMovieList = new ArrayList<>();
        mContext = context;

        int castNumber = Math.min(numberToDisplay, similarMovieList.size());
        for (int i = 0; i < castNumber; i++) {
            this.mSimilarMovieList.add(similarMovieList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mSimilarMovieList.size();
    }

    @Override
    public void onBindViewHolder(SimilarMovieViewHolder contactViewHolder, int i) {
        MovieResult similarMovie = mSimilarMovieList.get(i);
        contactViewHolder.mMovieTitle.setText(similarMovie.getTitle());
        contactViewHolder.mMovieDescription.setText(similarMovie.getOverview());

        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/"+ mContext.getString(R.string.image_size_w92) + "/" + similarMovie.getPosterPath())
                .placeholder(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_person_outline).sizeDp(16).color(Color.GRAY))
                .error(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_person_outline).sizeDp(16).color(Color.GRAY))
                .into(contactViewHolder.mMoviePoster);
    }

    @Override
    public SimilarMovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.similar_movies_list, viewGroup, false);

        return new SimilarMovieViewHolder(itemView);
    }

    public class SimilarMovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_title)
        TextView mMovieTitle;

        @BindView(R.id.list_desc)
        TextView mMovieDescription;

        @BindView(R.id.list_image)
        ImageView mMoviePoster;

        public SimilarMovieViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieResult movie = mSimilarMovieList.get(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), MovieBrowseDetailActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    v.getContext().startActivity(intent);
                }
            });


        }
    }
}

