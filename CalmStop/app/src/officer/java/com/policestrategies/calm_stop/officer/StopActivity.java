package com.policestrategies.calm_stop.officer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.policestrategies.calm_stop.R;

/**
 * Allows the officer to begin a traffic stop
 * @author Talal Abou Haiba
 */

public class StopActivity extends AppCompatActivity {

    private String mCitizenUid;
    private String mStopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mCitizenUid = getIntent().getExtras().getString("citizen_id");
        mStopId = getIntent().getExtras().getString("stop_id");

        System.out.println("GOT: " + mCitizenUid);
        System.out.println("GOT: " + mStopId);
    }

} // end class StopActivity