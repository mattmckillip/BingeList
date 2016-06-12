package com.example.matt.movieWatchList.viewControllers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.Realm.JSONCast;
import com.example.matt.movieWatchList.R;
import com.squareup.picasso.Picasso;

import io.realm.RealmList;

/**
 * Created by Matt on 6/2/2016.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ContactViewHolder> {

        private RealmList<JSONCast> contactList;
        private Context context;

        public CastAdapter( RealmList<JSONCast> contactList, Context context, int numberToDisplay) {
            this.contactList = new RealmList<>();

            int castNumber = Math.min(numberToDisplay, contactList.size());
            for (int i = 0; i < castNumber ; i++){
                this.contactList.add(contactList.get(i));
            }
            this.context = context;
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

            Picasso.with(context)
                    .load(castMember.getImagePath())
                    //.placeholder(R.drawable.unkown_person)
                    .error(R.drawable.ic_person_black_24dp)
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

                characterTextView =  (TextView) v.findViewById(R.id.list_title);
                actorTextView = (TextView)  v.findViewById(R.id.list_desc);
                actorImageView = (ImageView)  v.findViewById(R.id.list_avatar);
            }
        }
    }

