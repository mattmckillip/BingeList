package com.example.matt.bingeList.uitls;

import android.util.Log;

/**
 * Created by Matt on 7/25/2016.
 */
public class UniversalStaticHelper {

    public static String formatEpisodeTitle(Integer seasonNumber, Integer episodeNumber) {
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
}
