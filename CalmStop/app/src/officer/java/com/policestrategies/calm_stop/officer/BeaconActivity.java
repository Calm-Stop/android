package com.policestrategies.calm_stop.officer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.policestrategies.calm_stop.R;

/**
 * Currently WIP - testing beacon functionality in here.
 * @author Talal Abou Haiba
 */

public class BeaconActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        findViewById(R.id.beacon_debug).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.beacon_debug:
                System.out.println("Beacon debug has been pressed!");

        }
    }

} // end class BeaconActivity
