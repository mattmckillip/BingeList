package com.example.matt.movieWatchList.Models;

import java.util.ArrayList;

import io.realm.RealmObject;

/**
 * Created by Matt on 5/10/2016.
 */
public class Movie extends RealmObject {

    private String name;
    private String plot;
    private String genre;
    private String country;
    private String releaseDate;


    public Movie() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
