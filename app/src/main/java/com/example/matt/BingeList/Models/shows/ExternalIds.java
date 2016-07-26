package com.example.matt.bingeList.models.shows;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ExternalIds extends RealmObject {

    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("freebase_mid")
    @Expose
    private String freebaseMid;
    @SerializedName("freebase_id")
    @Expose
    private String freebaseId;
    @SerializedName("tvdb_id")
    @Expose
    private Integer tvdbId;
    @SerializedName("tvrage_id")
    @Expose
    private Integer tvrageId;

    /**
     *
     * @return
     * The imdbId
     */
    public String getImdbId() {
        return imdbId;
    }

    /**
     *
     * @param imdbId
     * The imdb_id
     */
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    /**
     *
     * @return
     * The freebaseMid
     */
    public String getFreebaseMid() {
        return freebaseMid;
    }

    /**
     *
     * @param freebaseMid
     * The freebase_mid
     */
    public void setFreebaseMid(String freebaseMid) {
        this.freebaseMid = freebaseMid;
    }

    /**
     *
     * @return
     * The freebaseId
     */
    public String getFreebaseId() {
        return freebaseId;
    }

    /**
     *
     * @param freebaseId
     * The freebase_id
     */
    public void setFreebaseId(String freebaseId) {
        this.freebaseId = freebaseId;
    }

    /**
     *
     * @return
     * The tvdbId
     */
    public Integer getTvdbId() {
        return tvdbId;
    }

    /**
     *
     * @param tvdbId
     * The tvdb_id
     */
    public void setTvdbId(Integer tvdbId) {
        this.tvdbId = tvdbId;
    }

    /**
     *
     * @return
     * The tvrageId
     */
    public Integer getTvrageId() {
        return tvrageId;
    }

    /**
     *
     * @param tvrageId
     * The tvrage_id
     */
    public void setTvrageId(Integer tvrageId) {
        this.tvrageId = tvrageId;
    }

}