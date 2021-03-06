package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.officer.dashboard.DashboardActivity;
import com.policestrategies.calm_stop.officer.profile.ProfileActivity;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        findViewById(R.id.aboutus_button).setOnClickListener(this);
        findViewById(R.id.profile_button).setOnClickListener(this);
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.help_button).setOnClickListener(this);
        findViewById(R.id.logout_button).setOnClickListener(this);

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

    } // end onCreate

    @Override
    public void onResume() {
        super.onResume();
        updateNavigationMenuSelection(3);
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
        }
    }

    private void profile() {
        Intent i = new Intent(getBaseContext(), ProfileActivity.class);
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
        // Clear department number
        getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
                .edit().putString(getString(R.string.shared_preferences_department_number),
                "").commit();

        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void home() {
        Intent i = new Intent(getBaseContext(), DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void ratings() {
        Intent i = new Intent(getBaseContext(), RatingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    private void history() {
        Intent i = new Intent(getBaseContext(), HistoryActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    private void account() {}

    private void updateNavigationMenuSelection(int menu) {
        ((BottomNavigationMenuView) bottomNavigationView.getChildAt(0)).getChildAt(menu).performClick();
    }

} // end AccountActivity
