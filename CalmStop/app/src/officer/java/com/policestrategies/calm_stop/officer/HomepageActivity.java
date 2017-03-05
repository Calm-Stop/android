package com.policestrategies.calm_stop.officer;

/**
 * Created by mariavizcaino on 2/9/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.officer.beacon_registration.BeaconRegistrationActivity;

/**
 * Created by mariavizcaino on 2/9/17.
 */

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener {
    //welcome Officer
    //private String user = LoginActivity.getEmail();
    //private String uuser = SignupActivity.getEmail();

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

        //findViewById(R.id.button_logout).setOnClickListener(this);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mActivityTitle = getTitle().toString();

        mMenuList = new String[]{"Home", "Ratings", "History", "Account"};

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        findViewById(R.id.button_logout).setOnClickListener(this);
        findViewById(R.id.button_manage_beacon).setOnClickListener(this);
        findViewById(R.id.button_ratings).setOnClickListener(this);


        Toast.makeText(HomepageActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_ratings: // Ratings button was pressed
                System.out.println("Rating button pressed");
                Intent h = new Intent(getBaseContext(), RatingActivity.class);
                startActivity(h);
                break;

            case R.id.button_manage_beacon:
                System.out.println("Beacon button pressed");
                Intent i = new Intent(getBaseContext(), BeaconRegistrationActivity.class);
                startActivity(i);
                break;

        }
    }

    private void addDrawerItems() {
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mMenuList);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        home();
                        break;
                    case 1:
                        ratings();
                        break;
                    case 2:
                        history();
                        break;
                    case 3:
                        account();
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

    private void home() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
        finish();

    }

    private void ratings() {
        Intent i = new Intent(getBaseContext(), RatingActivity.class);
        startActivity(i);

    }
    private void account() {
        Intent i = new Intent(getBaseContext(), AccountActivity.class);
        startActivity(i);

    }
    private void history() {
        Intent i = new Intent(getBaseContext(), HistoryActivity.class);
        startActivity(i);

    }


    private void logout() {
        //You want to logout -> login page
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);

        // Now we need to attempt to log in - we'll add code for this later (once Firebase is integrated)

        //Toast.makeText(LoginActivity.this, "Logging Out", Toast.LENGTH_SHORT).show();
    }
}

