package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;
/**
 * Created by mariavizcaino on 2/9/17.
 */

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener {
    //welcome citizen
    //private String user = LoginActivity.getEmail();
    //private String uuser = SignupActivity.getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        findViewById(R.id.button_logout).setOnClickListener(this);

        //Toast.makeText(HomepageActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_logout: // The login button was pressed - let's run the login function
                logout();
                break;

        }
    }


    private void logout() {
        //You want to logout -> login page
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);

        // Now we need to attempt to log in - we'll add code for this later (once Firebase is integrated)

        //Toast.makeText(LoginActivity.this, "Logging Out", Toast.LENGTH_SHORT).show();
    }
}
