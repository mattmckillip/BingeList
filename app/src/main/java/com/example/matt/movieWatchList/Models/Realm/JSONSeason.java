package com.example.matt.movieWatchList.Models.Realm;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Matt on 6/13/2016.
 */
public class JSONSeason extends RealmObject {
    private String airDate;
    private Integer episodeCount;
    private Integer id;
    private String posterPath;
    private Integer seasonNumber;
    private RealmList<JSONEpisode> episodes;
    private Boolean isWatched;

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

    public RealmList<JSONEpisode> getEpisodes() { return episodes; }

    public void setEpisodes( RealmList<JSONEpisode> episodes) { this.episodes = episodes;}

    public Boolean getIsWatched() {return isWatched;}

    public void setIsWatched(Boolean isWatched) {this.isWatched = isWatched;}
}
