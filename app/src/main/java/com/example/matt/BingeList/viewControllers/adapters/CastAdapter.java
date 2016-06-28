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
import android.widget.Toast;

import com.example.matt.bingeList.models.Cast;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.viewControllers.activities.CastActivity;
import com.example.matt.bingeList.viewControllers.activities.PersonActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

/**
 * Created by Matt on 6/2/2016.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CrewViewHolder> {
    private RealmList<Cast> mCastList;
    private Context mContext;

    public CastAdapter(RealmList<Cast> castList, Context context, int numberToDisplay) {
        mCastList = new RealmList<>();
        mContext = context;

        int castNumber = Math.min(numberToDisplay, castList.size());
        for (int i = 0; i < castNumber; i++) {
            mCastList.add(castList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mCastList.size();
    }

    @Override
    public void onBindViewHolder(CrewViewHolder contactViewHolder, int i) {
        Cast castMember = mCastList.get(i);
        contactViewHolder.mActor.setText(castMember.getName());
        contactViewHolder.mCharacter.setText(castMember.getCharacter());

        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/" + mContext.getString(R.string.image_size_w185) + castMember.getProfilePath())
                .placeholder(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_person).sizeDp(16).color(Color.GRAY))
                .error(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_person).sizeDp(16).color(Color.GRAY))
                .into(contactViewHolder.mProfileImage);
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
        TextView mActor;

        @BindView(R.id.list_desc)
        TextView mCharacter;

        @BindView(R.id.list_avatar)
        ImageView mProfileImage;

        public CrewViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cast castMember = mCastList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, PersonActivity.class);
                    intent.putExtra("personId", castMember.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}

