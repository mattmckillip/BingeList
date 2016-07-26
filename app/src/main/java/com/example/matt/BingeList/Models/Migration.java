package com.example.matt.bingeList.models;
import android.util.Log;

import com.example.matt.bingeList.models.movies.Movie;
import com.example.matt.bingeList.models.shows.ExternalIds;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.internal.Table;

public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        // Migrate from version 0 to version 1
        /*if (oldVersion == 1) {
            RealmObjectSchema movieSchema = schema.get("Movie");
            movieSchema.addField("onNetflix", boolean.class, FieldAttribute.INDEXED);
            oldVersion++;
        }

        if (oldVersion == 2) {
            RealmObjectSchema movieSchema = schema.get("Movie");
            movieSchema
                    .addField("netflixStreaming", int.class, FieldAttribute.INDEXED)
                    .removeField("onNetflix");
            oldVersion++;

        }

        if (oldVersion == 3) {
            // Create a new class
            RealmObjectSchema externalIdsSchema = schema.create("ExternalIds")
                    .addField("imdbId", String.class, FieldAttribute.INDEXED)
                    .addField("freebaseMid", String.class, FieldAttribute.INDEXED)
                    .addField("freebaseId", String.class, FieldAttribute.INDEXED)
                    .addField("tvdbId", String.class, FieldAttribute.INDEXED)
                    .addField("tvrageId", String.class, FieldAttribute.INDEXED);
            oldVersion++;
        }

        if (oldVersion == 4) {
            RealmObjectSchema tvShowSchema = schema.get("TVShow");
            tvShowSchema
                    .addField("netflixStreaming", int.class, FieldAttribute.INDEXED)
                    .addField("externalIds", ExternalIds.class, FieldAttribute.INDEXED);
            oldVersion++;
        }*/
    }
}