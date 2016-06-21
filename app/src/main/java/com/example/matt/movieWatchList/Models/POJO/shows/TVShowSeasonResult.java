package com.example.matt.movieWatchList.Models.POJO.shows;

import com.example.matt.movieWatchList.Models.Realm.JSONEpisode;
import com.example.matt.movieWatchList.Models.Realm.JSONSeason;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class TVShowSeasonResult {

    @SerializedName("_id")
    @Expose
    private String Id;
    @SerializedName("air_date")
    @Expose
    private Object airDate;
    @SerializedName("episodes")
    @Expose
    private List<Episode> episodes = new ArrayList<Episode>();
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("poster_path")
    @Expose
    private Object posterPath;
    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;

    /**
     * @return The Id
     */
    public String getTId() {
        return Id;
    }

    /**
     * @param Id The _id
     */
    public void setTId(String Id) {
        this.Id = Id;
    }

    /**
     * @return The airDate
     */
    public Object getAirDate() {
        return airDate;
    }

    /**
     * @param airDate The air_date
     */
    public void setAirDate(Object airDate) {
        this.airDate = airDate;
    }

    /**
     * @return The episodes
     */
    public List<Episode> getEpisodes() {
        return episodes;
    }

    /**
     * @param episodes The episodes
     */
    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

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
     * @return The posterPath
     */
    public Object getPosterPath() {
        return posterPath;
    }

    /**
     * @param posterPath The poster_path
     */
    public void setPosterPath(Object posterPath) {
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

    public JSONSeason convertToRealm() {
        JSONSeason returnSeason = new JSONSeason();
        returnSeason.setAirDate((String) getAirDate());
        returnSeason.setId(getId());
        returnSeason.setEpisodeCount(getEpisodes().size());
        returnSeason.setPosterPath((String) getPosterPath());
        returnSeason.setSeasonNumber(getSeasonNumber());
        returnSeason.setIsWatched(false);

        RealmList<JSONEpisode> episodeRealmList = new RealmList<>();
        for (Episode episode : getEpisodes()){
            JSONEpisode curEpisode = episode.convertToRealm();
            curEpisode.setSeason_id(getId());
            episodeRealmList.add(curEpisode);
        }
        returnSeason.setEpisodes(episodeRealmList);
        return returnSeason;
    }
}
