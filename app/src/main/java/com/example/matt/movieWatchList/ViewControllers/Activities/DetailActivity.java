/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.matt.movieWatchList.ViewControllers.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.Movie;
import com.example.matt.movieWatchList.R;

import io.realm.Realm;

/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class DetailActivity extends AppCompatActivity {
    Movie movie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer movieId = getIntent().getIntExtra("movieId",0);

        //MyApplication myApp = (MyApplication) getParent().getApplication();
        Realm uiRealm = Realm.getDefaultInstance();
        //Realm uiRealm = myApp.getUiRealm();
        this.movie =  uiRealm.where(Movie.class).equalTo("id",movieId).findFirst();

        System.out.print(movieId);

        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        collapsingToolbar.setTitle(movie.getName());

        ImageView image = (ImageView) findViewById(R.id.image);

        Bitmap bmp = BitmapFactory.decodeByteArray(movie.getImage(), 0, movie.getImage().length);
        image.setImageBitmap(bmp);

        LinearLayout layout = (LinearLayout) findViewById(R.id.more_info);
        TextView plot = (TextView) layout.findViewById(R.id.plot);
        TextView cast = (TextView) layout.findViewById(R.id.cast);


        plot.setText(movie.getPlot());
        cast.setText(movie.getCountry());

        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Added to watch list!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
