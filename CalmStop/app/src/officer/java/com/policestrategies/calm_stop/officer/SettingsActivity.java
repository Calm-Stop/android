package com.policestrategies.calm_stop.officer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.policestrategies.calm_stop.R;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        }
    }

} // end SettingsActivity
