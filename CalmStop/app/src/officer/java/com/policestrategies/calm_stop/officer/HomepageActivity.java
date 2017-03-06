package com.policestrategies.calm_stop.officer;

/**
 * Created by mariavizcaino on 2/9/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.officer.beacon_registration.BeaconRegistrationActivity;

/**
 * Created by mariavizcaino on 2/9/17.
 */

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener {
    //welcome Officer

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //bottomNavigationView.SetOnItemSelectedListener(this);

        //bottomNavigationView.bringToFront();

        findViewById(R.id.button_manage_beacon).setOnClickListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                home();
                                break;

                            case R.id.action_ratings:
                                ratings();
                                break;

                            case R.id.action_history:
                                history();
                                break;

                            case R.id.action_account:
                                account();
                                break;

                        }
                        return true;
                    }
                });

        Toast.makeText(HomepageActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_manage_beacon:
                System.out.println("Beacon button pressed");
                Intent i = new Intent(getBaseContext(), BeaconRegistrationActivity.class);
                startActivity(i);
                break;

        }
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

}

