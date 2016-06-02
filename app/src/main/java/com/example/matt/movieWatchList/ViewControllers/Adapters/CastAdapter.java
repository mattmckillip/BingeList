package com.example.matt.movieWatchList.ViewControllers.Adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.JSONCast;
import com.example.matt.movieWatchList.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import io.realm.RealmList;

/**
 * Created by Matt on 6/2/2016.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ContactViewHolder> {

        private RealmList<JSONCast> contactList;

        public CastAdapter( RealmList<JSONCast> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
            JSONCast castMember = contactList.get(i);
            contactViewHolder.characterTextView.setText(castMember.getCharacterName());
            contactViewHolder.actorTextView.setText(castMember.getActorName());
            Log.d("Cast Adapter", "Loading Image");
            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.unkown_person).build();
            imageLoader.displayImage(castMember.getImagePath(), contactViewHolder.actorImageView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                }
            });
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

