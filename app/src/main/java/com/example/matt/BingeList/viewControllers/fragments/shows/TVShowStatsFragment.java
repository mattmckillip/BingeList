package com.example.matt.bingeList.viewControllers.fragments.shows;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.example.matt.bingeList.uitls.TVShowRealmStaticHelper;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Matt on 7/14/2016.
 */
public class TVShowStatsFragment extends Fragment{
    private static final String TAG = TVShowStatsFragment.class.getSimpleName();
    private FragmentActivity listener;
    private Context mContext;
    private Realm mUiRealm;

    @BindView(R.id.tv_shows_chart)
    HorizontalStackBarChartView mShowBarChart;

    @BindView(R.id.tv_episode_chart)
    HorizontalStackBarChartView mEpisodeBarChart;

    @BindView(R.id.caughtup_shows)
    TextView mCaughtupShows;

    @BindView(R.id.completed_shows_count)
    TextView mCompletedShowsCount;

    @BindView(R.id.uncaughtup_shows)
    TextView mUncaughtupShows;

    @BindView(R.id.uncompleted_shows_count)
    TextView mUncompletedShowsCount;

    @BindView(R.id.unwatched_episodes)
    TextView mUnwatchedEpisodes;

    @BindView(R.id.watched_episodes)
    TextView mWatchedEpisodes;

    @BindView(R.id.unwatched_episodes_count)
    TextView mUnwatchedEpisodeCount;

    @BindView(R.id.watched_episodes_count)
    TextView mWatchedEpisodeCount;

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
        return inflater.inflate(R.layout.tvshow_statistic_layout, parent, false);
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

    // HELPERS
    private void setData() {
        int caughtUpShows = TVShowRealmStaticHelper.getAllWatchedShows(mUiRealm).size();
        int incompletShows = TVShowRealmStaticHelper.getAllShows(mUiRealm).size() - caughtUpShows;

        int watchedEpisodes = TVShowRealmStaticHelper.getAllWatchedEpisodes(mUiRealm).size();
        int unwatchedEpisodes = TVShowRealmStaticHelper.getAllEpisodes(mUiRealm).size() - watchedEpisodes;

        mCompletedShowsCount.setText(String.format(Locale.getDefault(), "%d", caughtUpShows));
        mUncompletedShowsCount.setText(String.format(Locale.getDefault(), "%d", incompletShows));
        createShowBarChart(caughtUpShows, incompletShows);

        mWatchedEpisodeCount.setText(String.format(Locale.getDefault(), "%d", watchedEpisodes));
        mUnwatchedEpisodeCount.setText(String.format(Locale.getDefault(), "%d", unwatchedEpisodes));
        createEpisodeBarChart(watchedEpisodes, unwatchedEpisodes);
    }

    private void createShowBarChart(int completeCount, int inCompleteCount) {
        float [][] mValues = {{1},{0}};
        if (completeCount != 0 && inCompleteCount != 0){
            mValues[0][0] = completeCount;
            mValues[1][0] = inCompleteCount;
        }

        String[] mLabels= {""};

        BarSet barSet = new BarSet(mLabels, mValues[0]);
        barSet.setColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
        mShowBarChart.addData(barSet);

        barSet = new BarSet(mLabels, mValues[1]);
        barSet.setColor(ContextCompat.getColor(mContext, R.color.lightColorPrimary));
        mShowBarChart.addData(barSet);

        mShowBarChart.setBarSpacing(Tools.fromDpToPx(5));

        mShowBarChart.setBorderSpacing(Tools.fromDpToPx(5))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false);

        mShowBarChart.show();
    }

    private void createEpisodeBarChart(int completeCount, int inCompleteCount) {

        String[] mLabels= {""};
        float [][] mValues = {{1},{0}};
        if (completeCount != 0 && inCompleteCount != 0){
            mValues[0][0] = completeCount;
            mValues[1][0] = inCompleteCount;
        }

        BarSet barSet = new BarSet(mLabels, mValues[0]);
        barSet.setColor(ContextCompat.getColor(mContext, R.color.lightColorAccent));
        mEpisodeBarChart.addData(barSet);

        barSet = new BarSet(mLabels, mValues[1]);
        barSet.setColor(ContextCompat.getColor(mContext, R.color.lightColorPrimary));
        mEpisodeBarChart.addData(barSet);

        mEpisodeBarChart.setBarSpacing(Tools.fromDpToPx(5));

        mEpisodeBarChart.setBorderSpacing(Tools.fromDpToPx(5))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false);

        mEpisodeBarChart.show();
    }
}
