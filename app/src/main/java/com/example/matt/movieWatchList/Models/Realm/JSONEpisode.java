package com.example.matt.movieWatchList.Models.Realm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Matt on 6/20/2016.
 */
public class JSONEpisode extends RealmObject {
    private String airDate;
    //private List<Object> crew = new ArrayList<Object>();
    private Integer episodeNumber;
   // private List<Object> guestStars = new ArrayList<Object>();
    private String name;
    private String overview;
    private Integer id;
    //private Object productionCode;
    private Integer seasonNumber;
    //private Object stillPath;
    private Double voteAverage;
    private Integer voteCount;
    private Boolean isWatched;
    private Integer season_id;


    /**
     * @return The airDate
     */
    public String getAirDate() {
        return airDate;
    }

    /**
     * @param airDate The air_date
     */
    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    /*public List<Object> getCrew() {
        return crew;
    }
    public void setCrew(List<Object> crew) {
        this.crew = crew;
    }*/

    /**
     * @return The episodeNumber
     */
    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    /**
     * @param episodeNumber The episode_number
     */
    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    /*public List<Object> getGuestStars() {
        return guestStars;
    }
    public void setGuestStars(List<Object> guestStars) {
        this.guestStars = guestStars;
    }*/

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     * @param overview The overview
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /*public Object getProductionCode() {
        return productionCode;
    }
    public void setProductionCode(Object productionCode) {
        this.productionCode = productionCode;
    }*/

    /**
     * @return The seasonNumber
     */
    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    /**
     * @param seasonNumber The season_number
     */
    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    /*public Object getStillPath() {
        return stillPath;
    }
    public void setStillPath(Object stillPath) {
        this.stillPath = stillPath;
    }*/

    /**
     * @return The voteAverage
     */
    public Double getVoteAverage() {
        return voteAverage;
    }

    /**
     * @param voteAverage The vote_average
     */
    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     * @return The voteCount
     */
    public Integer getVoteCount() {
        return voteCount;
    }

    /**
     * @param voteCount The vote_count
     */
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    
    public Boolean getIsWatched() {return isWatched;}
    public void setIsWatched(Boolean isWatched) {this.isWatched = isWatched;}

    public Integer getSeason_id() { return season_id; }
    public void setSeason_id(Integer season_id) { this.season_id = season_id; }
}
