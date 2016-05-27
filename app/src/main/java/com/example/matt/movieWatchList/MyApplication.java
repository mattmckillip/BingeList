package com.example.matt.movieWatchList;

import android.app.Application;
import io.realm.Realm;

/**
 * Created by Matt on 5/10/2016.
 */

public class MyApplication extends Application {
    private static Realm uiRealm;

    public Realm getUiRealm() {
        return uiRealm;
    }

    public void setUiRealm(Realm uiRealm) {
        this.uiRealm = uiRealm;
    }

}