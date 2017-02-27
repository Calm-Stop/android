package com.policestrategies.calm_stop.citizen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.policestrategies.calm_stop.R;

/**
 * Landing page after a beacon is detected. Find the UUID of the beacon and connect to the officer.
 * @author Talal Abou Haiba
 */
public class BeaconDetectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detection);
    }

} // end class BeaconDetectionActivity
