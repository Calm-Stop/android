package com.policestrategies.calm_stop.citizen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;

/**
 * Created by mariavizcaino on 2/19/17.
 */

public class PreviousStopsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previousstops);

        Toast.makeText(PreviousStopsActivity.this, "WELCOME TO THE PREVIOUS STOPS PAGE", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

    }
}