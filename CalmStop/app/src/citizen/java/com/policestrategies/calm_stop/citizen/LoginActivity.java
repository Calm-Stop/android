package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.policestrategies.calm_stop.R;

/**
 * Allows the user to log in or create an account.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.button_signup).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_signup:
                Intent i = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(i);
                break;

        }
    }

}
