package com.example.matt.movieWatchList.Models;

/**
 * Created by Matt on 5/31/2016.
 */
public class JSONCast {
    String characterName;
    String actorName;
    String imagePath;

    public void setCharacterName(String characterName) { this.characterName = characterName;}
    public void setActorName(String actorName) { this.actorName = actorName; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getCharacterName() { return characterName; }
    public String getActorName() { return actorName; }
    public String getImagePath() { return imagePath; }
}
