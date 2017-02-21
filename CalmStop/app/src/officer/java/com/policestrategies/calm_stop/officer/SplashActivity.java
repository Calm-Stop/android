package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

/**
 * First activity loaded when cold-booting the app. Determines whether the user should be sent to
 * {@link LoginActivity}, or {@link HomepageActivity}.
 * @author Talal Abou Haiba
 */

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        boolean loggedIn = mAuth.getCurrentUser() != null;

        Intent intent;

        if (!loggedIn) {
            //intent = new Intent(this, DemoActivity.class);
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, HomepageActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
