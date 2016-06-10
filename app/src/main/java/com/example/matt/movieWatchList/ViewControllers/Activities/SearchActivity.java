package com.example.matt.movieWatchList.viewControllers.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.example.matt.movieWatchList.R;

/**
 * Created by Matt on 6/7/2016.
 */
public class SearchActivity extends AppCompatActivity {
    Adapter adapterViewPager;
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolber);
        setSupportActionBar(toolbar);

        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(this.getAssets(), "fonts/Lobster-Regular.ttf");
                if(tv.getText().equals(this.getTitle())){
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);
                        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

                        //Check to see which item was being clicked and perform appropriate action
                        switch (menuItem.getItemId()) {

                            //Replacing the main content with ContentFragment
                            case R.id.watch_list_menu_item:
                                Intent watchListIntent = new Intent(SearchActivity.this, MainActivity.class);
                                startActivity(watchListIntent);
                                return true;

                            case R.id.browse_menu_item:
                                Intent browseIntent = new Intent(SearchActivity.this, BrowseActivity.class);
                                startActivity(browseIntent);
                                return true;

                            case R.id.search_menu_item:
                                mDrawerLayout.closeDrawers();
                                return true;
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });



        /*// Cast recycler view
        castRecyclerView = (RecyclerView) findViewById(R.id.cast_recycler_view);
        castAdapter = new CastAdapter(castList, getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        castRecyclerView.setLayoutManager(castLayoutManager);
        castRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castRecyclerView.setAdapter(castAdapter);

        // Cast recycler view
        crewRecyclerView = (RecyclerView) findViewById(R.id.crew_recycler_view);
        crewAdapter = new CastAdapter(crewList, getApplicationContext(), NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager crewLayoutManager = new LinearLayoutManager(getApplicationContext());
        crewRecyclerView.setLayoutManager(crewLayoutManager);
        crewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        crewRecyclerView.setAdapter(crewAdapter);*/

    }

    /*
     * Drawer Functions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
