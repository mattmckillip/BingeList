package com.example.matt.movieWatchList.Models;

import android.graphics.Bitmap;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Matt on 5/31/2016.
 */
public class JSONMovie extends RealmObject {
    private String title;
    private String backdropURL;
    private Integer budget;
    private RealmList<JSONGenre> genres;
    private String officialURL;
    private Integer id;
    private Integer imdbID;
    private String originalLanguage;
    private String overview;
    private Double popularity;
    private String posterURL;
    private RealmList<JSONCompany> companies;
    private RealmList<JSONCountry> countries;
    private String releaseDate;
    private Integer revenue;
    private Integer runtime;
    private String status;
    private String tagline;
    private Double vote_average;
    private RealmList<JSONCast> cast;
    private byte[] backdropBitmap;
    private RealmList<JSONCast> crew;
    private boolean isWatched;
    private boolean onWatchList;


    public JSONMovie() { }

    public String getTitle() {
        return title;
    }
    public String getBackdropURL() { return backdropURL; }
    public Integer getBudget() {  return budget; }
    public RealmList<JSONGenre> getGenres() { return genres; }
    public String getOfficialURL() { return officialURL; }
    public Integer getId() { return id; }
    public Integer getImdbID() { return imdbID; }
    public String getOriginalLanguage() { return originalLanguage; }
    public String getOverview() { return overview; }
    public Double getPopularity() { return popularity; }
    public String getPosterURL() { return posterURL; }
    public RealmList<JSONCompany> getCompanies() { return companies; }
    public RealmList<JSONCountry> getCountries() { return countries; }
    public String getReleaseDate() { return releaseDate; }
    public Integer getRevenue() { return revenue; }
    public Integer getRuntime() { return runtime; }
    public String getStatus() { return status; }
    public String getTagline() { return tagline; }
    public Double getVote_average() { return vote_average; }
    public RealmList<JSONCast> getCast() { return cast; }
    public byte[] getBackdropBitmap() {return backdropBitmap; }
    public RealmList<JSONCast> getCrew() {return  crew; }
    public boolean getOnWatchList() { return onWatchList; }
    public boolean getIsWatched() { return isWatched; }


    public void setTitle(String title) {this.title = title; }
    public void setBackdropURL(String backdropURL) { this.backdropURL = backdropURL; }
    public void setBudget(Integer budget) { this.budget = budget; }
    public void setGenres(RealmList<JSONGenre> genres) { this.genres = genres; }
    public void setOfficialURL(String officialURL) { this.officialURL = officialURL; }
    public void setId(Integer id) {this.id = id; }
    public void setImdbID(Integer imdbID) { this.imdbID = imdbID; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setPopularity(Double popularity) { this.popularity = popularity; }
    public void setPosterURL(String posterURL) { this.posterURL = posterURL; }
    public void setCompanies(RealmList<JSONCompany> companies) { this.companies = companies; }
    public void setCountries(RealmList<JSONCountry> countries) { this.countries = countries; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setRevenue(Integer revenue) { this.revenue = revenue; }
    public void setRuntime(Integer runtime) { this.runtime = runtime; }
    public void setStatus(String status) { this.status = status; }
    public void setTagline(String tagline) {this.tagline = tagline; }
    public void setVote_average(Double vote_average) { this.vote_average = vote_average; }
    public void setCast(RealmList<JSONCast> cast) { this.cast = cast; }
    public void setBackdropBitmap(byte[] backdropBitmap) { this.backdropBitmap = backdropBitmap; }
    public void setCrew(RealmList<JSONCast> crew) { this.crew = crew; }
    public void setOnWatchList(boolean onWatchList) { this.onWatchList = onWatchList; }
    public void setWatched(boolean isWatched) { this.isWatched = isWatched; }

}
