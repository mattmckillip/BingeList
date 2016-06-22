package com.example.matt.movieWatchList;

import android.app.Application;

import com.example.matt.movieWatchList.Models.Realm.JSONMovie;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Matt on 5/10/2016.
 */

public class MyApplication extends Application {
    private static Realm uiRealm;
    public RealmList<JSONMovie> watchList;
    public RealmList<JSONMovie> watched;

    public Realm getUiRealm() {
        return uiRealm;
    }

    public void setUiRealm(Realm uiRealm) {
        this.uiRealm = uiRealm;
    }
}