package com.example.matt.movieWatchList.Models;

import io.realm.RealmObject;

/**
 * Created by Matt on 5/31/2016.
 */
public class JSONCompany extends RealmObject {
    private String companyName;
    private Integer id;

    public void setCompanyName(String companyName) { this.companyName = companyName;}
    public void setId(Integer id){
        this.id = id;
    }

    public String getCompanyName() { return companyName; }
    public Integer getId() {
        return id;
    }
}
