package com.example.matt.movieWatchList.Models.Realm;

import io.realm.RealmObject;

/**
 * Created by Matt on 5/31/2016.
 */
public class JSONGenre extends RealmObject {
    private String genreName;
    private Integer id;

    public void setGenreName(String genreName) { this.genreName = genreName;}
    public void setId(Integer id){
        this.id = id;
    }

    public String getGenreName() { return genreName; }
    public Integer getId() {
        return id;
    }
}
