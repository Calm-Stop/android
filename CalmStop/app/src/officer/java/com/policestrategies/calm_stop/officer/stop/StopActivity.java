package com.policestrategies.calm_stop.officer.stop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.policestrategies.calm_stop.R;

/**
 * Allows the officer to begin a traffic stop
 * @author Talal Abou Haiba
 */

public class StopActivity extends AppCompatActivity {

    private StopManager mStopManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mStopManager = new StopManager(this);

    }

} // end class StopActivity