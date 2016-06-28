package com.example.matt.bingeList.viewControllers.fragments.shows;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.matt.bingeList.models.Cast;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.viewControllers.adapters.CastAdapter;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Matt on 6/14/2016.
 */
public class TVShowOverviewFragment extends Fragment {
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 3;
    private FragmentActivity listener;
    private int showID;
    private int vibrantColor;
    private int mutedColor;
    private Realm uiRealm;
    private TVShow show;
    private RealmList<Cast> castList = new RealmList<>();
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;
    private RealmList<Cast> crewList = new RealmList<>();
    private RecyclerView crewRecyclerView;
    private CastAdapter crewAdapter;

    @BindView(R.id.scroll_view)
    NestedScrollView scroll_view;

    @BindView(R.id.rating)
    RatingBar stars;

    @BindView(R.id.plot_title)
    TextView plotTitle;

    @BindView(R.id.cast_title)
    TextView castTitle;

    @BindView(R.id.crew_title)
    TextView crewTitle;

    @BindView(R.id.overview_title)
    TextView overviewTitle;

    @BindView(R.id.runtime)
    TextView runtime;

    @BindView(R.id.user_rating)
    TextView userRating;

    @BindView(R.id.more_info)
    LinearLayout layout;

    @BindView(R.id.expand_text_view)
    ExpandableTextView plot;

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
        showID = getArguments().getInt("showID", 0);
        vibrantColor = getArguments().getInt("vibrantColor", 0);
        mutedColor = getArguments().getInt("mutedColor", 0);

        /*ArrayList<Thing> things = new ArrayList<Thing>();
        adapter = new ThingsAdapter(getActivity(), things);*/
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.tv_show_overview, parent, false);
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

        uiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();
        show = uiRealm.where(TVShow.class).equalTo("id", showID).findFirst();

        if (show == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/tv/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TVShowAPI service = retrofit.create(TVShowAPI.class);

            Call<TVShow> call = service.getTVShow(Integer.toString(showID));

            call.enqueue(new Callback<TVShow>() {
                @Override
                public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                    show = response.body();
                    //show.setBackdropPath("https://image.tmdb.org/t/p/w500//" + show.getBackdropPath());
                    if (show != null) {
                        updateUI();
                    } else {
                        Snackbar.make(getView(), "Error loading data", Snackbar.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFailure(Call<TVShow> call, Throwable t) {
                    Log.d("getMovie()", "Callback Failure");
                }
            });
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        plot.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        //Color titles
        overviewTitle.setTextColor(vibrantColor);
        plotTitle.setTextColor(vibrantColor);
        castTitle.setTextColor(vibrantColor);
        crewTitle.setTextColor(vibrantColor);
        LayerDrawable starProgressDrawable = (LayerDrawable) stars.getProgressDrawable();
        starProgressDrawable.getDrawable(2).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);
        starProgressDrawable.getDrawable(1).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);

        // Add data
        plot.setText(show.getOverview());
        stars.setRating(show.getVoteAverage().floatValue());
        runtime.setText(Integer.toString(show.getNumberOfSeasons()) + " seasons");
        userRating.setText(Double.toString(show.getVoteAverage()) + "/10");

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/tv/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TVShowAPI service = retrofit.create(TVShowAPI.class);
        Call<Credits> call = service.getCredits(Integer.toString(showID));

        call.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(retrofit.Response<Credits> response, Retrofit retrofit) {
                Log.d("GetCredits()", "Callback Success");
                List<PersonCast> cast = response.body().getCast();
                List<PersonCrew> crew = response.body().getCrew();

                RealmList<JSONCast> realmCast = new RealmList<>();
                for( int i = 0; i <= 3; i++) {
                    realmCast.add(cast.get(i).convertToRealm());
                }

                RealmList<JSONCast> realmCrew = new RealmList<>();
                for( int i = 0; i <= 3; i++) {
                    realmCrew.add(crew.get(i).convertToRealm());
                }

                realmShow.setCrew(realmCrew);
                realmShow.setCast(realmCast);

                // Populate cast and crew recycler views
                castRecyclerView.setAdapter( new CastAdapter(realmShow.getCast(), getContext(), NUMBER_OF_CREW_TO_DISPLAY));
                crewRecyclerView.setAdapter( new CastAdapter(realmShow.getCrew(), getContext(), NUMBER_OF_CREW_TO_DISPLAY));
                castRecyclerView.setFocusable(false);
                crewRecyclerView.setFocusable(false);

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("GetCredits()", "Callback Failure");
            }
        });*/
    }

    private void addByteArray(byte[] image) {
        show.setBackdropBitmap(image);
    }
}
