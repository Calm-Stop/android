package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.WindowDecorActionBar;
import android.view.View;
import android.widget.TextView;

import com.policestrategies.calm_stop.R;

import org.w3c.dom.Text;

/**
 * Created by mariavizcaino on 5/7/17.
 */

public class HowToUseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_howtouse);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/avenir-next.ttf");


        TextView Title = (TextView) findViewById(R.id.howtouseTitle);
        TextView how = (TextView) findViewById(R.id.howParagraph);

        Title.setTypeface(custom_font);
        how.setTypeface(custom_font);

        how.setText("The way this app works: \n" +
                    "1. When you get pulled over, the cop will send \n" +
                    "   you a notification via bluetooth\n" +
                    "2. This initiates contact and you will be \n" +
                    "   requested to send your documents\n" +
                    "     -You can decline and the officer will \n" +
                    "      approach your car\n" +
                    "     -You can send your documents (Note this \n" +
                    "      will not prevent the officer from coming\n" +
                    "      to you \n" +
                    "3. Wait for officer to send a chat/audio request\n" +
                    "4. Incident is followed through with the form of \n " +
                    "   communication choosen\n" +
                    "5. Take a survey/rate the officer for statistical\n " +
                    "   and police department analysis purposes\n\n\n" +
                    "- Change profile details in Profile\n" +
                    "- Change password in Settings\n" +
                    "- See your stop history in Previous Stops \n" +
                    "- Read about Police Stategies in About Us\n");
    }

    @Override
    public void onBackPressed() {
        settings();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
        }
    }

    private void settings() {
        Intent i = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(i);
        finish();
    }
}
