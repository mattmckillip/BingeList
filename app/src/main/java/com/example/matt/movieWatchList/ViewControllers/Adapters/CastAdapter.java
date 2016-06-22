package com.example.matt.movieWatchList.viewControllers.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matt.movieWatchList.Models.POJO.movies.MovieResult;
import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import io.realm.RealmList;

/**
 * Created by Matt on 6/2/2016.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ContactViewHolder> {

    private RealmList<JSONCast> contactList;
    private Context mContext;

    public CastAdapter(RealmList<JSONCast> contactList, Context context, int numberToDisplay) {
        this.contactList = new RealmList<>();

        int castNumber = Math.min(numberToDisplay, contactList.size());
        for (int i = 0; i < castNumber; i++) {
            this.contactList.add(contactList.get(i));
        }
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        JSONCast castMember = contactList.get(i);
        contactViewHolder.characterTextView.setText(castMember.getActorName());
        contactViewHolder.actorTextView.setText("as " + castMember.getCharacterName());

        Picasso.with(mContext)
                .load(castMember.getImagePath())
                .placeholder(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_person).sizeDp(16).color(Color.GRAY))
                .error(new IconicsDrawable(mContext).icon(GoogleMaterial.Icon.gmd_person).sizeDp(16).color(Color.GRAY))
                .into(contactViewHolder.actorImageView);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_list, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView characterTextView;
        protected TextView actorTextView;
        protected ImageView actorImageView;

        public ContactViewHolder(View v) {
            super(v);

            characterTextView = (TextView) v.findViewById(R.id.list_title);
            actorTextView = (TextView) v.findViewById(R.id.list_desc);
            actorImageView = (ImageView) v.findViewById(R.id.list_avatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONCast castMember = contactList.get(getAdapterPosition());
                    Toast.makeText(mContext, castMember.getCharacterName(), Toast.LENGTH_SHORT).show();

                    /*Intent intent = new Intent(context, MovieBrowseDetailActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    context.startActivity(intent);*/
                }
            });
        }
    }
}

