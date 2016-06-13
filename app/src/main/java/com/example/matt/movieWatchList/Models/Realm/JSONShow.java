package com.example.matt.movieWatchList.Models.Realm;

import com.example.matt.movieWatchList.Models.POJO.Genre;
import com.example.matt.movieWatchList.Models.POJO.shows.CreatedBy;
import com.example.matt.movieWatchList.Models.POJO.shows.Network;
import com.example.matt.movieWatchList.Models.POJO.shows.ProductionCompany;
import com.example.matt.movieWatchList.Models.POJO.shows.Season;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Matt on 6/13/2016.
 */
public class JSONShow extends RealmObject {

    private String backdropPath;
    //private RealmList<CreatedBy> createdBy = new RealmList<CreatedBy>();
    //private List<Integer> episodeRunTime = new ArrayList<>();
    private String firstAirDate;
    private RealmList<JSONGenre> genres = new RealmList<>();
    private String homepage;
    private Integer id;
    private Boolean inProduction;
    //private List<String> languages = new ArrayList<String>();
    private String lastAirDate;
    private String name;
    private RealmList<JSONNetwork> networks = new RealmList<>();
    private Integer numberOfEpisodes;
    private Integer numberOfSeasons;
    //private List<String> originCountry = new ArrayList<String>();
    private String originalLanguage;
    private String originalName;
    private String overview;
    private Double popularity;
    private String posterPath;
    //private List<ProductionCompany> productionCompanies = new ArrayList<ProductionCompany>();
    //private List<Season> seasons = new ArrayList<Season>();
    private String status;
    private String type;
    private Double voteAverage;
    private Integer voteCount;
    private RealmList<JSONCast> cast;
    private byte[] backdropBitmap;
    private RealmList<JSONCast> crew;
    private boolean isWatched;
    private boolean onWatchList;


    public JSONShow() { }

    /**
     *
     * @return
     *     The backdropPath
     */
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     *
     * @param backdropPath
     *     The backdrop_path
     */
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /**
     *
     * @return
     *     The firstAirDate
     */
    public String getFirstAirDate() {
        return firstAirDate;
    }

    /**
     *
     * @param firstAirDate
     *     The first_air_date
     */
    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    /**
     *
     * @return
     *     The genres
     */
    public RealmList<JSONGenre> getGenres() {
        return genres;
    }

    /**
     *
     * @param genres
     *     The genres
     */
    public void setGenres(RealmList<JSONGenre> genres) {
        this.genres = genres;
    }

    /**
     *
     * @return
     *     The homepage
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     *
     * @param homepage
     *     The homepage
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
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
     *     The inProduction
     */
    public Boolean getInProduction() {
        return inProduction;
    }

    /**
     *
     * @param inProduction
     *     The in_production
     */
    public void setInProduction(Boolean inProduction) {
        this.inProduction = inProduction;
    }


    /**
     *
     * @return
     *     The lastAirDate
     */
    public String getLastAirDate() {
        return lastAirDate;
    }

    /**
     *
     * @param lastAirDate
     *     The last_air_date
     */
    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
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
     *     The numberOfEpisodes
     */
    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    /**
     *
     * @param numberOfEpisodes
     *     The number_of_episodes
     */
    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    /**
     *
     * @return
     *     The numberOfSeasons
     */
    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    /**
     *
     * @param numberOfSeasons
     *     The number_of_seasons
     */
    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    /**
     *
     * @return
     *     The originalLanguage
     */
    public String getOriginalLanguage() {
        return originalLanguage;
    }

    /**
     *
     * @param originalLanguage
     *     The original_language
     */
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    /**
     *
     * @return
     *     The originalName
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     *
     * @param originalName
     *     The original_name
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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
     *     The popularity
     */
    public Double getPopularity() {
        return popularity;
    }

    /**
     *
     * @param popularity
     *     The popularity
     */
    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    /**
     *
     * @return
     *     The posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     *
     * @param posterPath
     *     The poster_path
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }


    /*public List<Season> getSeasons() {
        return seasons;
    }
    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }*/

    /**
     *
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
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



    public RealmList<JSONCast> getCast() { return cast; }
    public byte[] getBackdropBitmap() {return backdropBitmap; }
    public RealmList<JSONCast> getCrew() {return  crew; }
    public boolean getOnWatchList() { return onWatchList; }
    public boolean getIsWatched() { return isWatched; }
    public RealmList<JSONNetwork> getNetworks() {return  networks; }



    public void setBackdropBitmap(byte[] backdropBitmap) { this.backdropBitmap = backdropBitmap; }
    public void setCrew(RealmList<JSONCast> crew) { this.crew = crew; }
    public void setCast(RealmList<JSONCast> cast) { this.cast = cast; }
    public void setNetworks(RealmList<JSONNetwork> networks) { this.networks = networks; }


    public void setOnWatchList(boolean onWatchList) { this.onWatchList = onWatchList; }
    public void setWatched(boolean isWatched) { this.isWatched = isWatched; }


}
