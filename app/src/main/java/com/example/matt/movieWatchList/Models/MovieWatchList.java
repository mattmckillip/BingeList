package com.example.matt.movieWatchList.Models;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Matt on 5/10/2016.
 */
public class MovieWatchList extends RealmObject {

    private RealmList<Movie> movieList;


    public MovieWatchList() { }

    public RealmList<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(RealmList<Movie> movieList) {
        this.movieList = movieList;
    }


}
