package com.example.matt.movieWatchList.Models;

import io.realm.RealmObject;

/**
 * Created by Matt on 5/31/2016.
 */
public class JSONCast extends RealmObject {
    private String characterName;
    private String actorName;
    private String imagePath;
    private Integer id;

    public void setCharacterName(String characterName) { this.characterName = characterName;}
    public void setActorName(String actorName) { this.actorName = actorName; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setId(Integer id){
        this.id = id;
    }

    public String getCharacterName() { return characterName; }
    public String getActorName() { return actorName; }
    public String getImagePath() { return imagePath; }
    public Integer getId() {
        return id;
    }

}
