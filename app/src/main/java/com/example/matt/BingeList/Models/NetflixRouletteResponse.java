package com.example.matt.bingeList.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NetflixRouletteResponse {

    @SerializedName("results")
    @Expose
    private Integer results;
    @SerializedName("statuscode")
    @Expose
    private Integer statuscode;
    @SerializedName("responsetime")
    @Expose
    private Double responsetime;
    @SerializedName("netflix_id")
    @Expose
    private Integer netflixId;

    /**
     * 
     * @return
     *     The results
     */
    public Integer getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(Integer results) {
        this.results = results;
    }

    /**
     * 
     * @return
     *     The statuscode
     */
    public Integer getStatuscode() {
        return statuscode;
    }

    /**
     * 
     * @param statuscode
     *     The statuscode
     */
    public void setStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
    }

    /**
     * 
     * @return
     *     The responsetime
     */
    public Double getResponsetime() {
        return responsetime;
    }

    /**
     * 
     * @param responsetime
     *     The responsetime
     */
    public void setResponsetime(Double responsetime) {
        this.responsetime = responsetime;
    }

    /**
     * 
     * @return
     *     The netflixId
     */
    public Integer getNetflixId() {
        return netflixId;
    }

    /**
     * 
     * @param netflixId
     *     The netflix_id
     */
    public void setNetflixId(Integer netflixId) {
        this.netflixId = netflixId;
    }

}
