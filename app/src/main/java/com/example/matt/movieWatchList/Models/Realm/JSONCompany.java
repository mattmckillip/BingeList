package com.example.matt.movieWatchList.Models.Realm;

import io.realm.RealmObject;

/**
 * Created by Matt on 5/31/2016.
 */
public class JSONCompany extends RealmObject {
    private String companyName;
    private Integer id;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
