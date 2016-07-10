package com.example.matt.bingeList.models.shows;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Season extends RealmObject{

    @SerializedName("air_date")
    @Expose
    private String airDate;

    @SerializedName("episode_count")
    @Expose
    private Integer episodeCount;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;

    private Boolean isWatched;
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
     * @return The episodeCount
     */
    public Integer getEpisodeCount() {
        return episodeCount;
    }

    /**
     * @param episodeCount The episode_count
     */
    public void setEpisodeCount(Integer episodeCount) {
        this.episodeCount = episodeCount;
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
     * @return The posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * @param posterPath The poster_path
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
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

    public Boolean getIsWatched() {return isWatched;}
    public void setIsWatched(Boolean isWatched) {this.isWatched = isWatched;}

    public Integer getShow_id() { return show_id; }
    public void setShow_id(Integer show_id) { this.show_id = show_id; }
}
