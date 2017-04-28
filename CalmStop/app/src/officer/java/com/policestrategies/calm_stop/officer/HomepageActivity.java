package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.officer.beacon_registration.BeaconRegistrationActivity;

/**
 * @author mariavizcaino
 */

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        findViewById(R.id.button_manage_beacon).setOnClickListener(this);
        findViewById(R.id.button_make_stop).setOnClickListener(this);

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

        Toast.makeText(HomepageActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
    } // end onCreate

    @Override
    public void onResume() {
        super.onResume();

        updateNavigationMenuSelection(0);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {

            case R.id.button_manage_beacon:
                i = new Intent(getBaseContext(), BeaconRegistrationActivity.class);
                startActivity(i);
                break;

            case R.id.button_make_stop:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_image_dock:
                i = new Intent(getBaseContext(), DocviewActivity.class);
                startActivity(i);
                break;

        }
    }

    private void home() {}

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

    private void account() {
        Intent i = new Intent(getBaseContext(), AccountActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    private void updateNavigationMenuSelection(int menu) {
        for (int i = 0; i < 4; i++) {
            MenuItem item = bottomNavigationView.getMenu().getItem(i);
            item.setChecked(i == menu);
        }
    }

} // end HomepageActivity

