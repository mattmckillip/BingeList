package com.example.matt.movieWatchList.uitls;

import android.content.Context;
import android.content.res.TypedArray;

import com.example.matt.movieWatchList.R;

/**
 * Created by Matt on 6/10/2016.
 */
public class Dimensions {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    /*public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }*/
}