package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.policestrategies.calm_stop.R;

/**
 * Created by mariavizcaino on 2/27/17.
 */

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    //private UserLocalStore localStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //localStore = new UserLocalStore(this);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.logout_button: // The login button was pressed - let's run the login function
                logout();
                break;
            case R.id.aboutus_button:
                aboutUs();
                break;
            case R.id.profile_button:
                profile();
                break;
            case R.id.settings_button:
                settings();
                break;
            case R.id.help_button:
                help();
                break;

        }
    }

    private void profile() {
        Intent i = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(i);
        finish();

    }
    private void help() {
        Intent i = new Intent(getBaseContext(), HelpActivity.class);
        startActivity(i);

    }
    private void aboutUs() {
        Intent i = new Intent(getBaseContext(), AboutUsActivity.class);
        startActivity(i);

    }
    private void settings() {
        Intent i = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(i);

    }

    private void logout() {
        //You want to logout -> login page
        //UserLocalStore localStore = new UserLocalStore(this);
        //localStore.clearUserData();
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);

    }
}
