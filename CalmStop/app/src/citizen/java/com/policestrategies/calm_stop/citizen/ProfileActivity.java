package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Toast;


/**
 * Created by mariavizcaino on 2/19/17.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        Toast.makeText(ProfileActivity.this, "WELCOME TO YOUR PROFILE", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {


        }
    }

}
