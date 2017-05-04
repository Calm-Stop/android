package com.policestrategies.calm_stop.citizen.stop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.policestrategies.calm_stop.R;

/**
 * @author Talal Abou Haiba
 */

public class StopActivity extends AppCompatActivity {

    private StopManager mStopManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        mStopManager = new StopManager(this);
    }

} // end class StopActivity
