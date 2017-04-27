package com.policestrategies.calm_stop.citizen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SharedUtil;
import com.policestrategies.calm_stop.citizen.beacon_detection.BeaconDetectionActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.policestrategies.calm_stop.R;

import static android.R.attr.action;
import static android.R.attr.drawable;
import static android.R.attr.phoneNumber;
import static android.R.id.toggle;
import static com.policestrategies.calm_stop.R.id.license;
import static com.policestrategies.calm_stop.R.id.nameDisplay;
import static com.policestrategies.calm_stop.R.layout.nav_header_main;

import com.policestrategies.calm_stop.citizen.beacon_detection.BeaconDetectionActivity;

public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private TextView mProfileName;

    private ProgressDialog mProgressDialog;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mProfileReference;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        ActionBar actionBar = getSupportActionBar();;
        actionBar.hide();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //mProfileName = (TextView) ((nav_header_main)context).findViewById(R.id.nameDisplay);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            mProfileReference = FirebaseDatabase.getInstance().getReference("citizen")
                    .child(mCurrentUser.getUid()).child("profile");

        }

        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);
        mProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String firstName = snapshot.child("first_name").getValue().toString();
                String lastName = snapshot.child("last_name").getValue().toString();

                String name = firstName + lastName;
                //FIXME
                //mProfileName.setText(name);

                SharedUtil.dismissProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }

        });

        findViewById(R.id.menu_main).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch(v.getId()) {
                    case R.id.menu_main:
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                        break;

                }

            }

        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id) {
            case R.id.profile:
                profile();
                break;
            case R.id.previous_stops:
                previousStops();
                break;
            case R.id.help:
                help();
                break;
            case R.id.about_us:
                aboutUs();
                break;
            case R.id.settings:
                settings();
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.documents:
                documents();
                break;

            case R.id.detect_beacon_debug:
                detectBecon();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void profile() {
        Intent i = new Intent(getBaseContext(), ProfileDisplayActivity.class);
        startActivity(i);
        finish();
    }

    private void previousStops() {
        Intent i = new Intent(getBaseContext(), PreviousStopsActivity.class);
        startActivity(i);
        finish();
    }

    private void help() {
        Intent i = new Intent(getBaseContext(), HelpActivity.class);
        startActivity(i);
        finish();
    }

    private void aboutUs() {
        Intent i = new Intent(getBaseContext(), AboutUsActivity.class);
        startActivity(i);
        finish();
    }

    private void settings() {
        Intent i = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(i);
        finish();
    }

    private void documents() {
        Intent i = new Intent(getBaseContext(), DocumentsActivity.class);
        startActivity(i);
        finish();
    }

    private void logout() {
        //You want to logout -> login page
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void detectBecon(){
        Intent i = new Intent(this, BeaconDetectionActivity.class);
        startActivity(i);
        finish();
    }


}
