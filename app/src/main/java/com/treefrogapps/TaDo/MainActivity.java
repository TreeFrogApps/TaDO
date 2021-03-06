package com.treefrogapps.TaDo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Toolbar mToolbar;
    private NavigationView mNavView;
    private DrawerLayout mDrawLayout;
    private ActionBarDrawerToggle mToggle;

    private int menuPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(mToolbar);

        if (savedInstanceState != null){
            menuPosition = savedInstanceState.getInt("menuPosition");
            updateTitle(menuPosition);
        } else {
            menuPosition = 0;
            updateDisplayFragment(getResources().getString(R.string.nav_home));
        }

        initialiseMenu();
        dbHelper = new DBHelper(this);
    }

    public void updateTitle(int menuPosition) {
        if (getSupportActionBar() != null) {

            switch (menuPosition) {
                case 0: getSupportActionBar().setTitle("My Lists"); break;
                case 1: getSupportActionBar().setTitle("TaDO Chooser"); break;
                case 2: getSupportActionBar().setTitle("Sync with Google Drive"); break;
                case 3: getSupportActionBar().setTitle("Scheduler"); break;
                default: break;
            }
        }
    }

    public void initialiseMenu() {

        mDrawLayout = (DrawerLayout) findViewById(R.id.navDrawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawLayout, mToolbar, R.string.app_name, R.string.app_name);

        mNavView = (NavigationView) findViewById(R.id.navView);
        mNavView.getMenu().getItem(menuPosition).setChecked(true);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {

                mDrawLayout.closeDrawers();
                menuItem.setChecked(true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        updateDisplayFragment(menuItem.getTitle().toString());
                    }
                }, 300);

                return true;
            }
        });

        mDrawLayout.setDrawerListener(mToggle);
        mToggle.syncState();

    }

    public void updateDisplayFragment(String menuTitle) {

        Fragment fragment = null;

        switch (menuTitle) {

            case "My Lists":
                menuPosition = 0;
                fragment = new MyListsFragment();
                break;
            case "TaDO Chooser":
                menuPosition = 1;
                fragment = new TaDOChooserFragment();
                break;
            case "Sync with Google Drive":
                menuPosition = 2;
                fragment = new SyncFragment();
                break;
            case "Scheduler":
                menuPosition = 3;
                fragment = new SchedulerFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameContainer, fragment);
            fragmentTransaction.commit();

            try {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(menuTitle);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("ERROR", "PROBLEM LOADING FRAGMENT");
            }
        }

    }

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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // include super as it will pass to the superclass and allow the fragment to
        // get the activity result - only if started from fragment (below to push to fragment)

        // get fragment in container and push the onActivityResult to it - necessary
        // if starting startingActivityForResult not from initiating fragment i.e. from recyclerAdapter
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameContainer);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("menuPosition",  menuPosition);
    }
}
