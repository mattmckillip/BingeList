package com.example.matt.bingeList;

import android.app.Application;


import com.example.matt.bingeList.models.Migration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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

    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiate realms
        // Or you can add the migration code to the configuration. This will run the migration code without throwing
        // a RealmMigrationNeededException.
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("default")
                .schemaVersion(1)
                .migration(new Migration())
                .build();

        Realm uiRealm = Realm.getInstance(config); // Automatically run migration if needed
        setUiRealm(uiRealm);
    }

}