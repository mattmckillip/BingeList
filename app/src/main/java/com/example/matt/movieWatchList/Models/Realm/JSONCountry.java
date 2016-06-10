package com.example.matt.movieWatchList.Models.Realm;

import io.realm.RealmObject;

/**
 * Created by Matt on 5/31/2016.
 */
public class JSONCountry extends RealmObject {
    private String countryName;
    private Integer id;

    public void setCountryName(String countryName) { this.countryName = countryName;}
    public void setId(Integer id){
        this.id = id;
    }

    public String getCountryName() { return countryName; }
    public Integer getId() {
        return id;
    }

}
