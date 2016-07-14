package com.example.matt.bingeList.uitls;

import android.provider.Contacts;
import android.util.Log;

import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.shows.Episode;
import com.example.matt.bingeList.models.shows.TVShow;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Matt on 7/14/2016.
 */
public class MovieRealmStaticHelper {

    public static RealmList<Movie> getAllMovies(Realm UIRealm) {
        RealmResults<Movie> allMovies =  UIRealm.where(Movie.class).findAll();
        RealmList<Movie> returnList = new RealmList<>();
        returnList.addAll(allMovies.subList(0, allMovies.size()));
        return returnList;
    }

    public static RealmList<Movie> getAllWatchedMovies(Realm UIRealm) {
        RealmResults<Movie> allWatchedMovies =  UIRealm.where(Movie.class).equalTo("isWatched", true).findAll();
        RealmList<Movie> returnList = new RealmList<>();
        returnList.addAll(allWatchedMovies.subList(0, allWatchedMovies.size()));
        return returnList;
    }

    public static RealmList<Movie> getAllWatchListMovies(Realm UIRealm) {
        RealmResults<Movie> allWatchlistMovies =  UIRealm.where(Movie.class).equalTo("isWatched", false).equalTo("onWatchList", true).findAll();
        RealmList<Movie> returnList = new RealmList<>();
        returnList.addAll(allWatchlistMovies.subList(0, allWatchlistMovies.size()));
        return returnList;
    }

    public static int getTotalWatchedTime(Realm UIRealm) {
        RealmList<Movie> watchedMovies = getAllWatchedMovies(UIRealm);
        int totalRuntime = 0;
        for (Movie movie : watchedMovies){
            totalRuntime += movie.getRuntime();
        }
        Log.d("TOTAL RUNTIME", Integer.toString(totalRuntime));
        return totalRuntime;
    }
}
