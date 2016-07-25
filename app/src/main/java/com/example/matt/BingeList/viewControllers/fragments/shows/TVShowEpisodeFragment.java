package com.example.matt.bingeList.viewControllers.fragments.shows;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.matt.bingeList.models.Crew;
import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.TVShowRealmStaticHelper;
import com.example.matt.bingeList.viewControllers.adapters.CrewAdapter;
import com.mikepenz.iconics.view.IconicsButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Matt on 6/14/2016.
 */
public class TVShowEpisodeFragment extends Fragment {
    private static final String TAG = TVShowEpisodeFragment.class.getSimpleName();
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 10;

    private FragmentActivity listener;
    private int mShowId;
    private int vibrantColor;
    private int mutedColor;
    private Realm mUiRealm;
    private Episode mEpisode;
    private Context mContext;
    private Boolean mIsCaughtUp;
    private RealmList<Crew> mCrew = new RealmList<>();
    private CrewAdapter mCrewAdapter;

    @BindView(R.id.scroll_view)
    NestedScrollView scroll_view;

    @BindView(R.id.episode_title)
    TextView mEpisodeTitle;

    @BindView(R.id.episode_image)
    ImageView mEpisodeImage;

    @BindView(R.id.action_button)
    IconicsButton mActionButton;

    @BindView(R.id.expand_text_view)
    ExpandableTextView mPlot;

    @BindView(R.id.air_date)
    TextView mAirDate;

    @BindView(R.id.user_rating)
    TextView userRating;

    @BindView(R.id.rating)
    RatingBar stars;

    @BindView(R.id.crew_title)
    TextView crewTitle;

    @BindView(R.id.crew_recycler_view)
    RecyclerView crewRecyclerView;

    @BindView(R.id.see_more_crew)
    Button seeMoreCrewButton;


    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowId = getArguments().getInt(getContext().getString(R.string.showId), 0);
        vibrantColor = getArguments().getInt("vibrantColor", 0);
        mutedColor = getArguments().getInt("mutedColor", 0);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.tvshow_episode_overview, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getContext();
        mUiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();
        mEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShowId, mUiRealm);

        setAdapters();

        mIsCaughtUp = false;
        if (mEpisode == null) {
            mIsCaughtUp = true;
            mEpisode = TVShowRealmStaticHelper.getLastEpisode(mShowId, mUiRealm);
        }
        updateUI();
    }

    private void setAdapters() {
        // PersonCast recycler view
        if (mCrew.isEmpty()) {
            crewTitle.setVisibility(View.GONE);
            crewRecyclerView.setVisibility(View.GONE);
            seeMoreCrewButton.setVisibility(View.GONE);
        } else {
            mCrewAdapter = new CrewAdapter(mCrew, mContext, NUMBER_OF_CREW_TO_DISPLAY);
            RecyclerView.LayoutManager crewLayoutManager = new LinearLayoutManager(mContext);
            crewRecyclerView.setLayoutManager(crewLayoutManager);
            crewRecyclerView.setItemAnimator(new DefaultItemAnimator());
            crewRecyclerView.setAdapter(mCrewAdapter);
        }
    }

    private void updateUI() {
        Log.d(TAG, "updateUI()");
        setData();
        setColors();
        setListeners();
    }

    private void setListeners() {
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TVShowRealmStaticHelper.watchEpisode(mEpisode, mUiRealm);
                Snackbar.make(scroll_view, "Watched " + formatEpisodeTitle(mEpisode.getSeasonNumber(), mEpisode.getEpisodeNumber()) + ": " + mEpisode.getName() + "!", Snackbar.LENGTH_SHORT).show();
                mEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShowId, mUiRealm);
                if (mEpisode == null) {
                    mIsCaughtUp = true;
                    mEpisode = TVShowRealmStaticHelper.getLastEpisode(mShowId, mUiRealm);
                }
                setData();
            }
        });
    }

    //HELPER METHODS
    private void setColors() {
        Log.d(TAG, "setColors()");
        //Color titles
        mEpisodeTitle.setTextColor(vibrantColor);
        crewTitle.setTextColor(vibrantColor);
        seeMoreCrewButton.setTextColor(mutedColor);

        LayerDrawable starProgressDrawable = (LayerDrawable) stars.getProgressDrawable();
        starProgressDrawable.getDrawable(2).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);
        starProgressDrawable.getDrawable(1).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);

    }

    private void setData() {
        // Add data
        if (mEpisode.isValid()) {
            mPlot.setText(mEpisode.getOverview());
            mEpisodeTitle.setText(formatEpisodeTitle(mEpisode.getSeasonNumber(), mEpisode.getEpisodeNumber()) + ": " + mEpisode.getName());
            mAirDate.setText(formatAirDate(mEpisode.getAirDate()));
            stars.setRating(mEpisode.getVoteAverage().floatValue());
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.UP);
            userRating.setText(df.format(mEpisode.getVoteAverage()) + "/10");

            mCrew = mEpisode.getCrew();
            Integer crewSize = Math.min(NUMBER_OF_CREW_TO_DISPLAY, mCrew.size());
            crewRecyclerView.setAdapter(new CrewAdapter(mCrew, mContext, crewSize));

            setActionButton();
            Picasso.with(mContext)
                    .load("https://image.tmdb.org/t/p/w500/" + mEpisode.getStillPath())
                    .into(mEpisodeImage);
        }
    }

    private void setActionButton(){
        if (mIsCaughtUp) {
            mActionButton.setEnabled(false);
            mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.button_grey));
            mActionButton.setText("{gmd_done_all} all caught up");
        } else {
            mActionButton.setEnabled(true);
            mActionButton.setTextColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
            mActionButton.setText("{gmd_remove_red_eye} watch episode!");
        }
    }

    private String formatEpisodeTitle(Integer seasonNumber, Integer episodeNumber) {
        String seasonText = "";
        String episodeText = "";

        if (seasonNumber >= 10) {
            seasonText = "S" + Integer.toString(seasonNumber);
        } else {
            seasonText = "S0" + Integer.toString(seasonNumber);
        }

        if (episodeNumber >= 10) {
            episodeText = "E" + Integer.toString(episodeNumber);
        } else {
            episodeText = "E0" + Integer.toString(episodeNumber);
        }
        Log.d("Episode Number", seasonText + episodeText);
        return seasonText + episodeText;
    }

    private String formatAirDate(String airDate) {
        if ( airDate == null || airDate.isEmpty()){
            return "";
        }
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        String date = null;
        try {
            newDate = dateFormater.parse(airDate);
            dateFormater = new SimpleDateFormat("MM/dd/yy");
            date = dateFormater.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
            date = airDate;
        }

        return date;
    }

    public void update() {
        if (mEpisode.equals(TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShowId, mUiRealm))) {
            return;
        }
        mEpisode = TVShowRealmStaticHelper.getNextUnwatchedEpisode(mShowId, mUiRealm);

        setAdapters();

        mIsCaughtUp = false;
        if (mEpisode == null) {
            mIsCaughtUp = true;
            mEpisode = TVShowRealmStaticHelper.getLastEpisode(mShowId, mUiRealm);
        }
        updateUI();
    }
}
