package com.example.matt.bingeList.models.movies;


import java.util.ArrayList;
import java.util.List;

import com.example.matt.bingeList.models.Country;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Releases extends RealmObject {

    @SerializedName("countries")
    @Expose
    private RealmList<Country> countries = new RealmList<Country>();

    /**
     *
     * @return
     * The countries
     */
    public RealmList<Country> getCountries() {
        return countries;
    }

    /**
     *
     * @param countries
     * The countries
     */
    public void setCountries(RealmList<Country> countries) {
        this.countries = countries;
    }

}