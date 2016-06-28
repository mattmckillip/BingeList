package com.example.matt.bingeList.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class PersonCredits extends RealmObject {

    @SerializedName("cast")
    @Expose
    private RealmList<PersonCast> cast = new RealmList<>();

    @SerializedName("crew")
    @Expose
    private RealmList<PersonCrew> crew = new RealmList<>();

    @SerializedName("id")
    @Expose
    private Integer id;

    /**
     * 
     * @return
     *     The cast
     */
    public RealmList<PersonCast> getCast() {
        return cast;
    }

    /**
     * 
     * @param cast
     *     The cast
     */
    public void setCast(RealmList<PersonCast> cast) {
        this.cast = cast;
    }

    /**
     * 
     * @return
     *     The crew
     */
    public RealmList<PersonCrew> getCrew() {
        return crew;
    }

    /**
     * 
     * @param crew
     *     The crew
     */
    public void setCrew(RealmList<PersonCrew> crew) {
        this.crew = crew;
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

}
