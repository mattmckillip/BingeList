package com.example.matt.movieWatchList.Models.POJO.shows;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("air_date")
    @Expose
    private String airDate;
    @SerializedName("crew")
    @Expose
    private List<Object> crew = new ArrayList<Object>();
    @SerializedName("episode_number")
    @Expose
    private Integer episodeNumber;
    @SerializedName("guest_stars")
    @Expose
    private List<Object> guestStars = new ArrayList<Object>();
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("production_code")
    @Expose
    private Object productionCode;
    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;
    @SerializedName("still_path")
    @Expose
    private Object stillPath;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    /**
     * 
     * @return
     *     The airDate
     */
    public String getAirDate() {
        return airDate;
    }

    /**
     * 
     * @param airDate
     *     The air_date
     */
    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    /**
     * 
     * @return
     *     The crew
     */
    public List<Object> getCrew() {
        return crew;
    }

    /**
     * 
     * @param crew
     *     The crew
     */
    public void setCrew(List<Object> crew) {
        this.crew = crew;
    }

    /**
     * 
     * @return
     *     The episodeNumber
     */
    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    /**
     * 
     * @param episodeNumber
     *     The episode_number
     */
    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    /**
     * 
     * @return
     *     The guestStars
     */
    public List<Object> getGuestStars() {
        return guestStars;
    }

    /**
     * 
     * @param guestStars
     *     The guest_stars
     */
    public void setGuestStars(List<Object> guestStars) {
        this.guestStars = guestStars;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     * 
     * @param overview
     *     The overview
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The productionCode
     */
    public Object getProductionCode() {
        return productionCode;
    }

    /**
     * 
     * @param productionCode
     *     The production_code
     */
    public void setProductionCode(Object productionCode) {
        this.productionCode = productionCode;
    }

    /**
     * 
     * @return
     *     The seasonNumber
     */
    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    /**
     * 
     * @param seasonNumber
     *     The season_number
     */
    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    /**
     * 
     * @return
     *     The stillPath
     */
    public Object getStillPath() {
        return stillPath;
    }

    /**
     * 
     * @param stillPath
     *     The still_path
     */
    public void setStillPath(Object stillPath) {
        this.stillPath = stillPath;
    }

    /**
     * 
     * @return
     *     The voteAverage
     */
    public Double getVoteAverage() {
        return voteAverage;
    }

    /**
     * 
     * @param voteAverage
     *     The vote_average
     */
    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     * 
     * @return
     *     The voteCount
     */
    public Integer getVoteCount() {
        return voteCount;
    }

    /**
     * 
     * @param voteCount
     *     The vote_count
     */
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

}
