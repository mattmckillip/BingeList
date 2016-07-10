package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.shows.TVShowResult;
import com.example.matt.bingeList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SimilarShowsAdapter extends RecyclerView.Adapter<SimilarShowsAdapter.SimilarshowsViewHolder> {

    private List<TVShowResult> mSimilarShowList;
    private Context mContext;

    public SimilarShowsAdapter(List<TVShowResult> similarShowList, Context context, int numberToDisplay) {
        mSimilarShowList = new ArrayList<>();
        mContext = context;

        int castNumber = Math.min(numberToDisplay, similarShowList.size());
        for (int i = 0; i < castNumber; i++) {
            this.mSimilarShowList.add(similarShowList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mSimilarShowList.size();
    }

    @Override
    public void onBindViewHolder(SimilarshowsViewHolder contactViewHolder, int i) {
        TVShowResult similarMovie = mSimilarShowList.get(i);
        contactViewHolder.mMovieTitle.setText(similarMovie.getName());
        contactViewHolder.mMovieDescription.setText(similarMovie.getOverview());

        Picasso.with(mContext)
                .load(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + similarMovie.getBackdropPath())
                .error(ContextCompat.getDrawable(mContext, R.drawable.generic_movie_background))
                .into(contactViewHolder.mMoviePoster);
    }

    @Override
    public SimilarshowsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.similar_movies_list, viewGroup, false);

        return new SimilarshowsViewHolder(itemView);
    }

    public class SimilarshowsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_title)
        TextView mMovieTitle;

        @BindView(R.id.list_desc)
        TextView mMovieDescription;

        @BindView(R.id.list_image)
        ImageView mMoviePoster;

        public SimilarshowsViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TVShowResult show = mSimilarShowList.get(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), TVShowBrowseDetailActivity.class);
                    intent.putExtra("showID", show.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}

