package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
<<<<<<< HEAD
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
=======
>>>>>>> master

import com.policestrategies.calm_stop.R;

/**
 * @author mariavizcaino
 */

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_36dp);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("About Us");

        setContentView(R.layout.activity_aboutus);
=======
        setContentView(R.layout.activity_about_us);
>>>>>>> master

        findViewById(R.id.backbutton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.backbutton:
                toHomepage();
                break;

        }

    }

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }
}