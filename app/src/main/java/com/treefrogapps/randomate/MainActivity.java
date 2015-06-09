package com.treefrogapps.randomate;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    Toolbar mToolbar;
    NavigationView mNavView;
    DrawerLayout mDrawLayout;
    ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseMenu();



        dbHelper = new DBHelper(this);
    }

    public void initialiseMenu(){

        mToolbar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(mToolbar);

        mDrawLayout = (DrawerLayout) findViewById(R.id.navDrawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawLayout, mToolbar, R.string.app_name, R.string.app_name);

        mNavView = (NavigationView) findViewById(R.id.navView);
        mNavView.getMenu().getItem(0).setChecked(true);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawLayout.closeDrawers();
                menuItem.setChecked(true);
                return true;
            }
        });

        mDrawLayout.setDrawerListener(mToggle);
        mToggle.syncState();

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
}
