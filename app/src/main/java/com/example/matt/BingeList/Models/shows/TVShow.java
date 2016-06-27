package com.example.matt.bingeList.models.shows;

import com.example.matt.bingeList.models.Genre;
import com.example.matt.bingeList.models.movies.ProductionCompany;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class TVShow extends RealmObject{

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("created_by")
    @Expose
    private RealmList<CreatedBy> createdBy = new RealmList<>();

    /*@SerializedName("episode_run_time")
    @Expose
    private RealmList<RealmInt> episodeRunTime = new RealmList<>();*/

    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;

    @SerializedName("genres")
    @Expose
    private RealmList<Genre> genres = new RealmList<>();

    @SerializedName("homepage")
    @Expose
    private String homepage;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("in_production")
    @Expose
    private Boolean inProduction;

    /*@SerializedName("languages")
    @Expose
    private RealmList<RealmString> languages = new RealmList<>();*/

    @SerializedName("last_air_date")
    @Expose
    private String lastAirDate;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("networks")
    @Expose
    private RealmList<Network> networks = new RealmList<>();

    @SerializedName("number_of_episodes")
    @Expose
    private Integer numberOfEpisodes;

    @SerializedName("number_of_seasons")
    @Expose
    private Integer numberOfSeasons;

    /*@SerializedName("origin_country")
    @Expose
    private RealmList<RealmString> originCountry = new RealmList<>();*/

    @SerializedName("original_language")
    @Expose
    private String originalLanguage;

    @SerializedName("original_name")
    @Expose
    private String originalName;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("popularity")
    @Expose
    private Double popularity;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("production_companies")
    @Expose
    private RealmList<ProductionCompany> productionCompanies = new RealmList<>();

    @SerializedName("seasons")
    @Expose
    private RealmList<Season> seasons = new RealmList<>();

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    private boolean isWatched;

    private boolean onWatchList;

    private byte[] backdropBitmap;

    /**
     * @return The backdropPath
     */
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     * @param backdropPath The backdrop_path
     */
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /**
     * @return The createdBy
     */
    public RealmList<CreatedBy> getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy The created_by
     */
    public void setCreatedBy(RealmList<CreatedBy> createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return The episodeRunTime
     */
    /*public RealmList<RealmInt> getEpisodeRunTime() {
        return episodeRunTime;
    }*/

    /**
     * @param episodeRunTime The episode_run_time
     */
    /*public void setEpisodeRunTime(RealmList<RealmInt> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }*/

    /**
     * @return The firstAirDate
     */
    public String getFirstAirDate() {
        return firstAirDate;
    }

    /**
     * @param firstAirDate The first_air_date
     */
    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    /**
     * @return The genres
     */
    public RealmList<Genre> getGenres() {
        return genres;
    }

    /**
     * @param genres The genres
     */
    public void setGenres(RealmList<Genre> genres) {
        this.genres = genres;
    }

    /**
     * @return The homepage
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * @param homepage The homepage
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
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
     * @return The inProduction
     */
    public Boolean getInProduction() {
        return inProduction;
    }

    /**
     * @param inProduction The in_production
     */
    public void setInProduction(Boolean inProduction) {
        this.inProduction = inProduction;
    }

    /**
     * @return The languages
     */
    /*public RealmList<RealmString> getLanguages() {
        return languages;
    }*/

    /**
     * @param languages The languages
     */
    /*public void setLanguages(RealmList<RealmString> languages) {
        this.languages = languages;
    }*/

    /**
     * @return The lastAirDate
     */
    public String getLastAirDate() {
        return lastAirDate;
    }

    /**
     * @param lastAirDate The last_air_date
     */
    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
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
     * @return The networks
     */
    public RealmList<Network> getNetworks() {
        return networks;
    }

    /**
     * @param networks The networks
     */
    public void setNetworks(RealmList<Network> networks) {
        this.networks = networks;
    }

    /**
     * @return The numberOfEpisodes
     */
    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    /**
     * @param numberOfEpisodes The number_of_episodes
     */
    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    /**
     * @return The numberOfSeasons
     */
    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    /**
     * @param numberOfSeasons The number_of_seasons
     */
    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    /**
     * @return The originCountry
     */
    /*public RealmList<RealmString> getOriginCountry() {
        return originCountry;
    }*/

    /**
     * @param originCountry The origin_country
     */
    /*public void setOriginCountry(RealmList<RealmString> originCountry) {
        this.originCountry = originCountry;
    }*/

    /**
     * @return The originalLanguage
     */
    public String getOriginalLanguage() {
        return originalLanguage;
    }

    /**
     * @param originalLanguage The original_language
     */
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    /**
     * @return The originalName
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @param originalName The original_name
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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
     * @return The popularity
     */
    public Double getPopularity() {
        return popularity;
    }

    /**
     * @param popularity The popularity
     */
    public void setPopularity(Double popularity) {
        this.popularity = popularity;
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
     * @return The productionCompanies
     */
    public RealmList<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    /**
     * @param productionCompanies The production_companies
     */
    public void setProductionCompanies(RealmList<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    /**
     * @return The seasons
     */
    public RealmList<Season> getSeasons() {
        return seasons;
    }

    /**
     * @param seasons The seasons
     */
    public void setSeasons(RealmList<Season> seasons) {
        this.seasons = seasons;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
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

    public boolean getOnWatchList() {
        return onWatchList;
    }

    public void setOnWatchList(boolean onWatchList) {
        this.onWatchList = onWatchList;
    }

    public boolean getIsWatched() {
        return isWatched;
    }

    public void setWatched(boolean isWatched) {
        this.isWatched = isWatched;
    }

    public byte[] getBackdropBitmap() {
        return backdropBitmap;
    }

    public void setBackdropBitmap(byte[] backdropBitmap) {
        this.backdropBitmap = backdropBitmap;
    }

}
