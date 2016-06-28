package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.PersonCast;
import com.example.matt.bingeList.models.PersonCredits;
import com.example.matt.bingeList.models.PersonCrew;
import com.example.matt.bingeList.models.movies.MovieResult;
import com.example.matt.bingeList.uitls.API.MovieAPI;
import com.example.matt.bingeList.viewControllers.activities.movies.BrowseMovieDetailActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.TVShowBrowseDetailActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;


public class KnownForAdapter extends RecyclerView.Adapter<KnownForAdapter.SimilarMovieViewHolder> {
    private static final String TAG = KnownForAdapter.class.getName();
    private static final String MOVIE_TYPE = "movie";
    private static final String SHOW_TYPE = "tv";

    private Context mContext;
    private boolean isCast;
    private RealmList<PersonCast> mPersonCast = new RealmList<>();
    private RealmList<PersonCrew> mPersonCrew = new RealmList<>();
    private int mNumberToDisplay;

    public KnownForAdapter(PersonCredits credits, Context context, int numberToDisplay) {
        mContext = context;
        mNumberToDisplay = numberToDisplay;

        // TODO better way, maybe both
        int castSize = credits.getCast().size();
        int crewSize = credits.getCrew().size();

        if (castSize > crewSize){
            isCast = true;
            int castNumber = Math.min(castSize, numberToDisplay);
            for (int i = 0; i < castNumber; i++){
                mPersonCast.add(credits.getCast().get(i));
            }
        } else {
            isCast = false;
            int crewNumber = Math.min(crewSize, numberToDisplay);
            for (int i = 0; i < crewNumber; i++){
                mPersonCrew.add(credits.getCrew().get(i));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isCast) {
            return mPersonCast.size();
        } else {
            return mPersonCrew.size();
        }
    }

    @Override
    public void onBindViewHolder(SimilarMovieViewHolder contactViewHolder, int i) {
        String posterPath = "";
        if (isCast) {
            PersonCast cast = mPersonCast.get(i);
            contactViewHolder.mMovieTitle.setText(cast.getTitle());
            contactViewHolder.mMovieDescription.setText(cast.getCharacter());
            posterPath = "https://image.tmdb.org/t/p/"+ mContext.getString(R.string.image_size_w92) + "/" + cast.getPosterPath();
        } else {
            PersonCrew crew = mPersonCrew.get(i);
            contactViewHolder.mMovieTitle.setText(crew.getTitle());
            contactViewHolder.mMovieDescription.setText(crew.getJob());
            posterPath = "https://image.tmdb.org/t/p/"+ mContext.getString(R.string.image_size_w92) + "/" + crew.getPosterPath();
        }

        Picasso.with(mContext)
                .load(posterPath)
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
                    if (isCast) {
                        PersonCast cast = mPersonCast.get(getAdapterPosition());
                        if (cast.getMediaType().equals(MOVIE_TYPE)) {
                            Intent intent = new Intent(v.getContext(), BrowseMovieDetailActivity.class);
                            intent.putExtra("movieId", cast.getId());
                            v.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(v.getContext(), TVShowBrowseDetailActivity.class);
                            intent.putExtra("showId", cast.getId());
                            v.getContext().startActivity(intent);
                        }
                    } else {
                        PersonCrew crew = mPersonCrew.get(getAdapterPosition());
                        if (crew.getMediaType().equals(MOVIE_TYPE)) {
                            Intent intent = new Intent(v.getContext(), BrowseMovieDetailActivity.class);
                            intent.putExtra("movieId", crew.getId());
                            v.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(v.getContext(), TVShowBrowseDetailActivity.class);
                            intent.putExtra("showId", crew.getId());
                            v.getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}

