package com.example.matt.bingeList.viewControllers.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.Person;
import com.example.matt.bingeList.models.PersonCredits;
import com.example.matt.bingeList.uitls.API.PersonAPI;
import com.example.matt.bingeList.uitls.PaletteTransformation;
import com.example.matt.bingeList.viewControllers.adapters.KnownForAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Matt on 6/28/2016.
 */
public class PersonActivity  extends AppCompatActivity {
    private static final String TAG = PersonActivity.class.getName();
    private static final int DEFAULT_COLOR = 0;
    private static final int NUMBER_KNOWN_FOR_TO_DISPLAY = 5;

    private Integer mPersonId;
    private Person mPerson;
    private KnownForAdapter mKnownForAdapter;
    private Context mContext;

    @BindView(R.id.appbar)
    AppBarLayout mAppbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;

    @BindView(R.id.backdrop)
    ImageView mBackdrop;

    @BindView(R.id.loadingPanel)
    RelativeLayout mLoadingPanel;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.overview_title)
    TextView mOverviewTitle;

    @BindView(R.id.born)
    TextView mBorn;

    @BindView(R.id.died)
    TextView mDied;

    @BindView(R.id.died_header)
    TextView mDiedHeader;

    @BindView(R.id.imdb)
    IconicsButton imdbLink;

    @BindView(R.id.known_for_header)
    TextView mKnownForHeader;

    @BindView(R.id.known_for_recycler_view)
    RecyclerView mKnownForRecyclerView;

    @BindView(R.id.see_more_known_for)
    Button mKnownForButton;

    @BindView(R.id.expand_text_view)
    ExpandableTextView mBiography;

    @BindView(R.id.fab)
    FloatingActionButton fab;


    @OnClick(R.id.imdb)
    public void setImdbLink(View view) {
        Uri uri = Uri.parse("http://www.imdb.com/name/" + mPerson.getImdbId());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.see_more_known_for)
    public void seeMoreSimilarMovies(View view) {
        /*Intent intent = new Intent(getBaseContext(), SimilarMoviesActivity.class);
        intent.putExtra("movieID", movieID);
        startActivity(intent);*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.person_detail_activity);
        ButterKnife.bind(this);

        fab.setVisibility(View.GONE);

        mContext = getApplicationContext();
        mPersonId = getIntent().getIntExtra("personId", 0);

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true)
                .build();

        Slidr.attach(this, config);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setViewsInvisible();
        setAdapters();
        getPerson();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }

    private void updateUI() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "updateUI()");
        }

        mBiography.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        // Set title of Detail page
        loadMovieBackgroundImage();
        setData();
        loadSimilarMovies();
    }

    //HELPER METHODS
    private void loadSimilarMovies() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadSimilarMovies()");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/person/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PersonAPI service = retrofit.create(PersonAPI.class);

        final Call<PersonCredits> knownForCreditsCall = service.getPersonCombinedCredits(Integer.toString(mPersonId));

        knownForCreditsCall.enqueue(new Callback<PersonCredits>() {
            @Override
            public void onResponse(Call<PersonCredits> call, Response<PersonCredits> response) {
                if (response.isSuccessful()) {
                    PersonCredits personCredits = response.body();

                    mKnownForRecyclerView.setAdapter(new KnownForAdapter(personCredits, mContext, NUMBER_KNOWN_FOR_TO_DISPLAY));
                    mKnownForRecyclerView.setFocusable(false);
                } else {
                    Snackbar.make(mKnownForRecyclerView, "bad call", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PersonCredits> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "getSimilarMovies() No Response");
                }
            }
        });
    }

    private void loadMovieBackgroundImage() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadMovieBackgroundImage()");
        }

        Picasso.with(this)
                .load(mPerson.getProfilePath())
                .fit().centerCrop()
                .error(R.drawable.unkown_person)
                .transform(PaletteTransformation.instance())
                .into(mBackdrop, new PaletteTransformation.PaletteCallback(mBackdrop) {
                    @Override
                    public void onSuccess(Palette palette) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Palette Transform onSuccess()");
                        }

                        int vibrantColor = palette.getVibrantColor(DEFAULT_COLOR);
                        int mutedColor = palette.getMutedColor(DEFAULT_COLOR);

                        if (vibrantColor == DEFAULT_COLOR) {
                            vibrantColor = ContextCompat.getColor(mContext, R.color.colorAccent);
                        }
                        if (mutedColor == DEFAULT_COLOR) {
                            mutedColor = ContextCompat.getColor(mContext, R.color.colorPrimary);
                        }
                        setColors(vibrantColor, mutedColor);
                        setViewsVisible();
                    }

                    @Override
                    public void onError() {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Palette Transform onError()");
                        }

                        int vibrantColor = ContextCompat.getColor(mContext, R.color.colorAccent);
                        int mutedColor = ContextCompat.getColor(mContext, R.color.colorPrimary);

                        setColors(vibrantColor, mutedColor);
                        setViewsVisible();
                    }
                });
    }

    private void getPerson() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "getPerson()");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/person/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PersonAPI service = retrofit.create(PersonAPI.class);

        Call<Person> call = service.getPersonProfile(Integer.toString(mPersonId));
        Log.d(TAG, Integer.toString(mPersonId));

        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                mPerson = response.body();
                if (mPerson == null) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Null mPerson: " + response.raw().toString());
                    }
                }
                mPerson.setProfilePath(mContext.getString(R.string.image_base_url) +  mContext.getString(R.string.image_size_w500)  + mPerson.getProfilePath());
                updateUI();
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d("getPerson()", "Callback Failure");
                }
            }
        });
    }

    private void setAdapters() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setAdapters()");
        }

        // Similar Moves recycler view
        mKnownForAdapter = new KnownForAdapter(new PersonCredits(), mContext, NUMBER_KNOWN_FOR_TO_DISPLAY);
        RecyclerView.LayoutManager similaryMovieLayoutManager = new LinearLayoutManager(mContext);
        mKnownForRecyclerView.setLayoutManager(similaryMovieLayoutManager);
        mKnownForRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mKnownForRecyclerView.setAdapter(mKnownForAdapter);
    }

    private void setData() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setData()");
        }

        mCollapsingToolbar.setTitle(mPerson.getName());

        if (mPerson.getBirthday() == null || mPerson.getBirthday().isEmpty()){
            mBorn.setVisibility(View.GONE);
            //mDiedHeader.setVisibility(View.GONE);
        } else{
            mBorn.setText(formateDate(mPerson.getBirthday()));
        }

        if (mPerson.getDeathday() == null || mPerson.getDeathday().isEmpty()){
            mDied.setVisibility(View.GONE);
            mDiedHeader.setVisibility(View.GONE);
        } else{
            mDied.setText(formateDate(mPerson.getDeathday()));
        }

        mBiography.setText(mPerson.getBiography());
    }

    private void setColors(int vibrantColor, int mutedColor) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setColors()");
        }

        mOverviewTitle.setTextColor(vibrantColor);
        mKnownForHeader.setTextColor(vibrantColor);
        imdbLink.setTextColor(mutedColor);
        mKnownForButton.setTextColor(mutedColor);

        mCollapsingToolbar.setBackgroundColor(vibrantColor);
        mCollapsingToolbar.setContentScrimColor(vibrantColor);
        mCollapsingToolbar.setStatusBarScrimColor(vibrantColor);
    }

    private void setViewsVisible(){
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setViewsVisible()");
        }

        mAppbar.setVisibility(View.VISIBLE);
        mCollapsingToolbar.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);
    }

    private void setViewsInvisible(){
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setViewsInvisible()");
        }

        mAppbar.setVisibility(View.INVISIBLE);
        mToolbar.setVisibility(View.INVISIBLE);
        mCollapsingToolbar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.INVISIBLE);
    }

    private String formateDate(String date) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        String retrunDate = null;
        try {
            newDate = dateFormater.parse(date);
            dateFormater = new SimpleDateFormat("MM/dd/yyyy");
            retrunDate = dateFormater.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
            retrunDate = date;
        }

        return retrunDate;
    }
}
