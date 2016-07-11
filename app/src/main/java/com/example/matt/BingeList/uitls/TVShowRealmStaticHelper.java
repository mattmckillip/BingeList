package com.example.matt.bingeList.uitls;

import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.TVShow;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

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
        RealmResults<TVShow> tvShowRealmResults = UIRealm.where(TVShow.class).equalTo("onYourShows", true).findAll();
        RealmList<TVShow> returnShows = new RealmList<>();
        for (TVShow show : tvShowRealmResults){
            if (getNextUnwatchedEpisode(show.getId(), UIRealm) != null) {
                returnShows.add(show);
            }
        }
        return returnShows;
    }

}
