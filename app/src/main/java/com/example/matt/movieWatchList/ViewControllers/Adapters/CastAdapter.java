package com.example.matt.movieWatchList.ViewControllers.Adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.JSONCast;
import com.example.matt.movieWatchList.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import io.realm.RealmList;

/**
 * Created by Matt on 6/2/2016.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ContactViewHolder> {

        private RealmList<JSONCast> contactList;

        public CastAdapter( RealmList<JSONCast> contactList) {
            this.contactList = contactList;
            Log.d("Cast Adapter", Integer.toString(contactList.size()));
        }

        @Override
        public int getItemCount() {
            Log.d("Cast Adapter", "getItemCount()");
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
            Log.d("Cast Adapter", "onBindViewHolder()");
            JSONCast castMember = contactList.get(i);
            Log.d("Cast member", castMember.getActorName());
            contactViewHolder.characterTextView.setText(castMember.getCharacterName());
            contactViewHolder.actorTextView.setText(castMember.getActorName());

            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            imageLoader.displayImage(castMember.getImagePath(), contactViewHolder.actorImageView);
            // Load image, decode it to Bitmap and return Bitmap to callback
            imageLoader.loadImage(castMember.getImagePath(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Do whatever you want with Bitmap
                }
            });
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Log.d("Cast Adapter", "onCreateViewHolder()");
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
                Log.d("ContactViewHolder", "ContactViewHolder()");

                characterTextView =  (TextView) v.findViewById(R.id.list_title);
                actorTextView = (TextView)  v.findViewById(R.id.list_desc);
                actorImageView = (ImageView)  v.findViewById(R.id.list_avatar);
            }
        }
    }

