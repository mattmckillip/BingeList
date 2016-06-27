package com.example.matt.bingeList.viewControllers.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matt.bingeList.models.Crew;
import com.example.matt.bingeList.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

/**
 * Created by Matt on 6/2/2016.
 */
public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {
    private RealmList<Crew> mCrewList;
    private Context mContext;

    public CrewAdapter(RealmList<Crew> crewList, Context context, int numberToDisplay) {
        mCrewList = new RealmList<>();
        mContext = context;

        int castNumber = Math.min(numberToDisplay, crewList.size());
        for (int i = 0; i < castNumber; i++) {
            mCrewList.add(crewList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mCrewList.size();
    }

    @Override
    public void onBindViewHolder(CrewViewHolder contactViewHolder, int i) {
        Crew crewMember = mCrewList.get(i);
        contactViewHolder.crewMemberName.setText(crewMember.getName());
        contactViewHolder.crewMemberJob.setText(crewMember.getJob());

        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/" +  mContext.getString(R.string.image_size_w185) + crewMember.getProfilePath())
                .placeholder(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_person).sizeDp(16).color(Color.GRAY))
                .error(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_person).sizeDp(16).color(Color.GRAY))
                .into(contactViewHolder.crewMemberProfile);
    }

    @Override
    public CrewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_list, viewGroup, false);

        return new CrewViewHolder(itemView);
    }

    public class CrewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_title)
        TextView crewMemberName;

        @BindView(R.id.list_desc)
        TextView crewMemberJob;

        @BindView(R.id.list_avatar)
        ImageView crewMemberProfile;

        public CrewViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Crew crewMember = mCrewList.get(getAdapterPosition());
                    Toast.makeText(mContext, crewMember.getName(), Toast.LENGTH_SHORT).show();

                    /*Intent intent = new Intent(context, MovieBrowseDetailActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    context.startActivity(intent);*/
                }
            });
        }
    }
}

