package com.example.matt.bingeList.viewControllers.fragments.movies;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.HorizontalStackBarChartView;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.MovieRealmStaticHelper;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Matt on 7/14/2016.
 */
public class MovieStatsFragment extends Fragment {
    private static final String TAG = MovieStatsFragment.class.getSimpleName();
    private FragmentActivity listener;
    private Context mContext;
    private Realm mUiRealm;

    @BindView(R.id.watchlist_chart)
    HorizontalStackBarChartView mChart;

    @BindView(R.id.watched_movies_number)
    TextView mWatchedMoviesCount;

    @BindView(R.id.unwatched_movies_number)
    TextView mUnwatchedMoviesCount;

    @BindView(R.id.total_movie_time)
    TextView mTotalMovieTime;



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
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.movie_statistic_layout, parent, false);
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

        setData();
    }

    private void setData() {
        int watchlistSize = MovieRealmStaticHelper.getAllWatchListMovies(mUiRealm).size();
        int watchedSize = MovieRealmStaticHelper.getAllWatchedMovies(mUiRealm).size();

        mWatchedMoviesCount.setText(String.format(Locale.getDefault(), "%d", watchedSize));
        mUnwatchedMoviesCount.setText(String.format(Locale.getDefault(), "%d", watchlistSize));
        mTotalMovieTime.setText(convertToReadableTime(MovieRealmStaticHelper.getTotalWatchedTime(mUiRealm)));

        String[] mLabels= {""};
        float [][] mValues = {{1},{0}};
        if (watchedSize != 0 && watchlistSize != 0){
            mValues[0][0] = watchedSize;
            mValues[1][0] = watchlistSize;
        }

        BarSet barSet = new BarSet(mLabels, mValues[0]);
        barSet.setColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
        mChart.addData(barSet);

        barSet = new BarSet(mLabels, mValues[1]);
        barSet.setColor(ContextCompat.getColor(mContext, R.color.lightColorPrimary));
        mChart.addData(barSet);

        mChart.setBarSpacing(Tools.fromDpToPx(5));

        mChart.setBorderSpacing(Tools.fromDpToPx(5))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false);

        mChart.show();
    }

    private String convertToReadableTime(int time) {
        int days = time / 1440;
        int hours = (time % 1440) / 60; //since both are ints, you get an int
        int minutes = time % 60;
        Log.d("convertToReadableTime()", String.format(Locale.getDefault(), "%d days %d hours %02d minutes",days, hours, minutes));
        return String.format(Locale.getDefault(), "%d days %d hours %02d minutes",days, hours, minutes);
    }
}
