package com.example.matt.bingeList.uitls;

import android.util.Log;

import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.uitls.Enums.ShowSort;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Matt on 7/10/2016.
 */
public class TVShowRealmStaticHelper {
    public static Episode getEpisode(int showId, int seasonNumber, int episodeNumber, Realm UIRealm) {
        return  UIRealm.where(Episode.class).equalTo("show_id", showId).equalTo("seasonNumber", seasonNumber).equalTo("episodeNumber", episodeNumber).findFirst();
    }

    public static Episode getNextUnwatchedEpisode(int showId, Realm UIRealm) {
        return UIRealm.where(Episode.class).equalTo("show_id", showId).equalTo("isWatched", false).findFirst();
    }

    public static RealmList<Episode> getAllEpisodes(int showId, Realm UIRealm) {
        RealmResults<Episode> allEpisodes =  UIRealm.where(Episode.class).equalTo("show_id", showId).findAll();
        RealmList<Episode> returnList = new RealmList<>();
        returnList.addAll(allEpisodes.subList(0, allEpisodes.size()));
        return returnList;
    }

    public static Episode getLastEpisode(int showId, Realm UIRealm) {
        RealmList<Episode> allEpisodes = getAllEpisodes(showId, UIRealm);
        return allEpisodes.get(allEpisodes.size() - 1);
    }
    public static void watchEpisode(int showId, int seasonNumber, int episodeNumber, Realm UIRealm) {
        Episode episode = getEpisode(showId, seasonNumber, episodeNumber, UIRealm);
        UIRealm.beginTransaction();
        episode.setIsWatched(true);
        UIRealm.commitTransaction();
    }

    public static void watchEpisode(Episode episode, Realm UIRealm) {
        UIRealm.beginTransaction();
        episode.setIsWatched(true);
        UIRealm.commitTransaction();
    }

    public static RealmList<TVShow> getShowsWithUnwatchedEpisodes(Realm UIRealm){
        RealmResults<TVShow> tvShowRealmResults = UIRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.DESCENDING);
        RealmList<TVShow> returnShows = new RealmList<>();
        for (TVShow show : tvShowRealmResults){
            if (getNextUnwatchedEpisode(show.getId(), UIRealm) != null) {
                returnShows.add(show);
            }
        }
        return returnShows;
    }

    public static RealmList<TVShow> getSortedShowsWithUnwatchedEpisodes(Realm UIRealm, int showSort){
        RealmResults<TVShow> tvShowRealmResults = null;
        if (showSort == ShowSort.TOP_RATED){
            tvShowRealmResults = UIRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("voteAverage", Sort.DESCENDING);
        } else if (showSort == ShowSort.RECENTLY_ADDED){
            tvShowRealmResults = UIRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.DESCENDING);
        } else if (showSort == ShowSort.ADDED_FIRST){
            tvShowRealmResults = UIRealm.where(TVShow.class).equalTo("onYourShows", true).findAllSorted("date", Sort.ASCENDING);
        }
        RealmList<TVShow> returnShows = new RealmList<>();
        for (TVShow show : tvShowRealmResults){
            if (getNextUnwatchedEpisode(show.getId(), UIRealm) != null) {
                returnShows.add(show);
            }
        }
        return returnShows;
    }

    public static RealmList<Episode> getAllWatchedEpisodes(Realm UIRealm) {
        RealmResults<Episode> allWatchedEpisodes =  UIRealm.where(Episode.class).equalTo("isWatched", true).findAllSorted("date", Sort.DESCENDING);
        RealmList<Episode> returnList = new RealmList<>();
        returnList.addAll(allWatchedEpisodes.subList(0, allWatchedEpisodes.size()));
        return returnList;
    }

    public static RealmList<Episode> getAllEpisodes(Realm UIRealm) {
        RealmResults<Episode> allEpisodes =  UIRealm.where(Episode.class).findAll();
        RealmList<Episode> returnList = new RealmList<>();
        returnList.addAll(allEpisodes.subList(0, allEpisodes.size()));
        return returnList;
    }

    public static RealmList<TVShow> getAllWatchedShows(Realm UIRealm) {
        RealmList<TVShow> allShowsList = getAllShows(UIRealm);
        RealmList<TVShow> returnList = new RealmList<>();

        for (TVShow show : allShowsList) {
            Episode episode = getNextUnwatchedEpisode(show.getId(), UIRealm);
            if (episode == null) {
                returnList.add(show);
            }
        }

        return returnList;
    }

    public static RealmList<TVShow> getAllShows(Realm UIRealm) {
        RealmResults<TVShow> allEpisodes =  UIRealm.where(TVShow.class).equalTo("onYourShows", true).findAll();
        RealmList<TVShow> returnList = new RealmList<>();
        returnList.addAll(allEpisodes.subList(0, allEpisodes.size()));
        return returnList;
    }

}
