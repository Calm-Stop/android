package com.policestrategies.calm_stop.officer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.policestrategies.calm_stop.R;

/**
 * Created by mariavizcaino on 2/27/17.
 */

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
/*
            case R.id.button_logout: // The login button was pressed - let's run the login function
                logout();
                break;
*/
        }
    }


}
