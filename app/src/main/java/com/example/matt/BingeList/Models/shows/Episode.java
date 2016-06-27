package com.example.matt.bingeList.models.shows;

import com.example.matt.bingeList.models.Crew;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Episode extends RealmObject{
    @SerializedName("air_date")
    @Expose
    private String airDate;

    @SerializedName("crew")
    @Expose
    private RealmList<Crew> crew = new RealmList<>();

    @SerializedName("episode_number")
    @Expose
    private Integer episodeNumber;

    /*@SerializedName("guest_stars")
    @Expose
    private RealmList<RealmString> guestStars = new RealmList<>();*/

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
    private String productionCode;

    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;

    @SerializedName("still_path")
    @Expose
    private String stillPath;

    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    private Boolean isWatched;
    private Integer season_id;
    private Integer show_id;

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

    /**
     * @return The crew
     */
    public RealmList<Crew> getCrew() {
        return crew;
    }

    /**
     * @param crew The crew
     */
    public void setCrew(RealmList<Crew> crew) {
        this.crew = crew;
    }

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

    /**
     * @return The guestStars
     */
    /*public RealmList<RealmString> getGuestStars() {
        return guestStars;
    }*/

    /**
     * @param guestStars The guest_stars
     */
   /* public void setGuestStars(RealmList<RealmString> guestStars) {
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

    /**
     * @return The productionCode
     */
    public String getProductionCode() {
        return productionCode;
    }

    /**
     * @param productionCode The production_code
     */
    public void setProductionCode(String productionCode) {
        this.productionCode = productionCode;
    }

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

    /**
     * @return The stillPath
     */
    public String getStillPath() {
        return stillPath;
    }

    /**
     * @param stillPath The still_path
     */
    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

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

    public Integer getShow_id() { return show_id; }
    public void setShow_id(Integer show_id) { this.show_id = show_id; }
}
