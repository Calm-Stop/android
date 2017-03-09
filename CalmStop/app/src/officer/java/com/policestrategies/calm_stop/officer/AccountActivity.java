package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.policestrategies.calm_stop.R;

/**
 * Created by mariavizcaino on 2/27/17.
 */

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    //private UserLocalStore localStore;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.bringToFront();

        findViewById(R.id.aboutus_button).setOnClickListener(this);
        findViewById(R.id.profile_button).setOnClickListener(this);
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.help_button).setOnClickListener(this);
        findViewById(R.id.logout_button).setOnClickListener(this);
        findViewById(R.id.backbutton).setOnClickListener(this);
        //localStore = new UserLocalStore(this);

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

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.logout_button:
                logout();
                break;
            case R.id.aboutus_button:
                aboutUs();
                break;
            case R.id.profile_button:
                profile();
                break;
            case R.id.settings_button:
                settings();
                break;
            case R.id.help_button:
                help();
                break;
            case R.id.backbutton:
                toHomepage();
                break;

        }
    }

    private void profile() {
        Intent i = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(i);
        finish();

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

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);
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
