package com.example.matt.bingeList.uitls;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.Enums.MovieSort;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.Enums.ViewType;


public class PreferencesHelper {
    public static SharedPreferences getSharedPreferences (Context context) {
        return context.getSharedPreferences("appPreferences", getDefaultSharedPreferencesMode());
    }

    public static SharedPreferences getViewAndThemeSharedPreferences (Context context) {
        return context.getSharedPreferences("viewAndThemePreferences", getDefaultSharedPreferencesMode());
    }

    public static int getDefaultSharedPreferencesMode() {
        return Context.MODE_PRIVATE;
    }

    public static void setViewAndThemeSharedPreferencesDefault(Context context){
        PreferenceManager.setDefaultValues(context, R.xml.view_and_theme_preferences, false);
    }

    public static void setRecyclerviewViewType(int viewType, Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.recyclerview_view_type_preference), viewType);
        editor.apply();
    }

    public static int getRecyclerviewViewType(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.recyclerview_view_type_preference), ViewType.CARD);
    }

    public static void setTheme(int theme, Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.theme_preference), theme);
        editor.apply();
    }

    public static int getTheme(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.theme_preference), ThemeEnum.DAY_THEME);
    }

    public static void setMovieSort(int sort, Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.movie_sort), sort);
        editor.apply();
    }

    public static int getMovieSort(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.movie_sort), MovieSort.RECENTLY_ADDED);
    }

    public static void setShowSort(int sort, Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.show_sort), sort);
        editor.apply();
    }

    public static int getShowSort(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.show_sort), 1);
    }

    public static void printValues(Context context) {
        Log.d("View Mode " , Integer.toString(PreferencesHelper.getRecyclerviewViewType(context)));
        Log.d("Theme " , Integer.toString(PreferencesHelper.getTheme(context)));

    }
}

