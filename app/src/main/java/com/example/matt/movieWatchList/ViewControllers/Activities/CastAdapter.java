package com.example.matt.movieWatchList.ViewControllers.Activities;

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

import java.util.ArrayList;

import io.realm.RealmList;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ContactViewHolder> {

    private RealmList<JSONCast> contactList;

    public CastAdapter( RealmList<JSONCast> contactList) {
        this.contactList = contactList;
        Log.d("Inside", "HERHERERE");
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
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cast_list, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView characterTextView;
        protected TextView actorTextView;
        protected ImageView actorImageView;

        public ContactViewHolder(View v) {
            super(v);
            characterTextView =  (TextView) v.findViewById(R.id.character);
            actorTextView = (TextView)  v.findViewById(R.id.actor);
            actorImageView = (ImageView)  v.findViewById(R.id.actor_image);
        }
    }
}