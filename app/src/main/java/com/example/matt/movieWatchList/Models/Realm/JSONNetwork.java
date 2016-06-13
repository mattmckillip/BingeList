package com.example.matt.movieWatchList.Models.Realm;

import io.realm.RealmObject;

/**
 * Created by Matt on 6/13/2016.
 */
public class JSONNetwork extends RealmObject{

    private Integer id;
    private String name;

    public JSONNetwork(){}

    public Integer getId() {return id;}
    public String getName() {return name;}

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
