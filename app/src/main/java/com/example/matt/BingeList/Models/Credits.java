package com.example.matt.bingeList.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Credits extends RealmObject{

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("cast")
    @Expose
    private RealmList<Cast> cast = new RealmList<Cast>();

    @SerializedName("crew")
    @Expose
    private RealmList<Crew> crew = new RealmList<Crew>();

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
     * @return The cast
     */
    public RealmList<Cast> getCast() {
        return cast;
    }

    /**
     * @param cast The cast
     */
    public void setCast(RealmList<Cast> cast) {
        this.cast = cast;
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

}
