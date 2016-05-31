package com.example.matt.movieWatchList.Models;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Matt on 5/31/2016.
 */
public class JSONMovie {
    String title;
    String backdropURL;
    Integer budget;
    ArrayList<JSONGenre> genres;
    String officialURL;
    Integer id;
    Integer imdbID;
    String originalLanguage;
    String overview;
    Double popularity;
    String posterURL;
    ArrayList<JSONCompany> companies;
    ArrayList<JSONCountry> countries;
    String releaseDate;
    Integer revenue;
    Integer runtime;
    String status;
    String tagline;
    Double vote_average;
    ArrayList<JSONCast> cast = new ArrayList<JSONCast>();

    public JSONMovie() { }

    public String getTitle() {
        return title;
    }
    public String getBackdropURL() { return backdropURL; }
    public Integer getBudget() {  return budget; }
    public ArrayList<JSONGenre> getGenres() { return genres; }
    public String getOfficialURL() { return officialURL; }
    public Integer getId() { return id; }
    public Integer getImdbID() { return imdbID; }
    public String getOriginalLanguage() { return originalLanguage; }
    public String getOverview() { return overview; }
    public Double getPopularity() { return popularity; }
    public String getPosterURL() { return posterURL; }
    public ArrayList<JSONCompany> getCompanies() { return companies; }
    public ArrayList<JSONCountry> getCountries() { return countries; }
    public String getReleaseDate() { return releaseDate; }
    public Integer getRevenue() { return revenue; }
    public Integer getRuntime() { return runtime; }
    public String getStatus() { return status; }
    public String getTagline() { return tagline; }
    public Double getVote_average() { return vote_average; }
    public ArrayList<JSONCast> getCast() { return cast; }

    public void setTitle(String title) {this.title = title; }
    public void setBackdropURL(String backdropURL) { this.backdropURL = backdropURL; }
    public void setBudget(Integer budget) { this.budget = budget; }
    public void setGenres(ArrayList<JSONGenre> genres) { this.genres = genres; }
    public void setOfficialURL(String officialURL) { this.officialURL = officialURL; }
    public void setId(Integer id) {this.id = id; }
    public void setImdbID(Integer imdbID) { this.imdbID = imdbID; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setPopularity(Double popularity) { this.popularity = popularity; }
    public void setPosterURL(String posterURL) { this.posterURL = posterURL; }
    public void setCompanies(ArrayList<JSONCompany> companies) { this.companies = companies; }
    public void setCountries(ArrayList<JSONCountry> countries) { this.countries = countries; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setRevenue(Integer revenue) { this.revenue = revenue; }
    public void setRuntime(Integer runtime) { this.runtime = runtime; }
    public void setStatus(String status) { this.status = status; }
    public void setTagline(String tagline) {this.tagline = tagline; }
    public void setVote_average(Double vote_average) { this.vote_average = vote_average; }
    public void setCast(ArrayList<JSONCast> cast) { this.cast = cast; }

}
