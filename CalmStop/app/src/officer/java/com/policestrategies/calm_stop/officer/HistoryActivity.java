package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.policestrategies.calm_stop.R;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


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
    public void onResume() {
        super.onResume();

        updateNavigationMenuSelection(2);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        }
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

    private void history() {}

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

} // end HistoryActivity
