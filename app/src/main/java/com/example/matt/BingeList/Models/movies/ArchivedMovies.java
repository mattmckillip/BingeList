package com.example.matt.bingeList.models.movies;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Matt on 6/29/2016.
 */
public class ArchivedMovies extends RealmObject{
    @PrimaryKey
    private Integer movieId;

    /**
     * @return The movieId
     */
    public Integer getMovieId() {
        return movieId;
    }

    /**
     * @param movieId The movieId
     */
    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }
}