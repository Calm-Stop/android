package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.citizen.beacon_detection.BeaconDetectionActivity;

/**
 * Created by mariavizcaino on 2/9/17.
 */

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener {

    //welcome citizen
    private TextView mHomeText;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    private String[] mMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mHomeText = (TextView) findViewById(R.id.AboutUsTitle);

        mHomeText.setText("Hello " + "Citizen" + "!\n\nSwipe from Left to Right -> to see menu!");

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mActivityTitle = getTitle().toString();

        mMenuList = new String[]{"Profile", "Previous Stops", "Help", "About Us", "Settings", "Logout"
        , "Debug - Beacon Detection"};

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //findViewById(R.id.button_logout).setOnClickListener(this);

        Toast.makeText(HomepageActivity.this, "Swipe Right for Menu", Toast.LENGTH_LONG).show();
    }

    private void addDrawerItems() {
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mMenuList);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        profile();
                        break;
                    case 1:
                        previousStops();
                        break;
                    case 2:
                        help();
                        break;
                    case 3:
                        aboutUs();
                        break;
                    case 4:
                        settings();
                        break;
                    case 5:
                        logout();
                        break;
                    case 6: // Beacon debug
                        Intent i = new Intent(getBaseContext(), BeaconDetectionActivity.class);
                        startActivity(i);
                        break;

                }

                //Toast.makeText(HomepageActivity.this, "position= " + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

        }
    }

    private void profile() {
        Intent i = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(i);
        finish();

    }

    private void previousStops() {
        Intent i = new Intent(getBaseContext(), PreviousStopsActivity.class);
        startActivity(i);

    }

    private void help() {
        Intent i = new Intent(getBaseContext(), HelpActivity.class);
        startActivity(i);

    }

    private void aboutUs() {
        Intent i = new Intent(getBaseContext(), AboutUsActivity.class);
        startActivity(i);

    }

    private void settings() {
        Intent i = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(i);
    }

    private void logout() {
        //You want to logout -> login page
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);

    }
}
