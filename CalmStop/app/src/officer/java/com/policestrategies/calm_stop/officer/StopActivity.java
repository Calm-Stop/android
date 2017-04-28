package com.policestrategies.calm_stop.officer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.policestrategies.calm_stop.R;

/**
 * Allows the officer to begin a traffic stop
 * @author Talal Abou Haiba
 */

public class StopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
    }

} // end class StopActivity