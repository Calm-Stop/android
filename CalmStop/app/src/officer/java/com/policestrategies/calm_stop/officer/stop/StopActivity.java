package com.policestrategies.calm_stop.officer.stop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.chat.ChatActivity;
import com.policestrategies.calm_stop.officer.dashboard.DashboardActivity;

/**
 * Allows the officer to begin a traffic stop
 * @author Talal Abou Haiba
 */

public class StopActivity extends AppCompatActivity implements View.OnClickListener {

    private StopManager mStopManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        findViewById(R.id.stop_request_call_button).setOnClickListener(this);
        findViewById(R.id.stop_request_text_button).setOnClickListener(this);
        findViewById(R.id.stop_request_documents_button).setOnClickListener(this);

        mStopManager = new StopManager(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.stop_request_call_button:
                break;

            case R.id.stop_request_text_button:
                requestTextChat();
                break;

            case R.id.stop_request_documents_button:
                break;
        }
    }

    private void requestTextChat() {
        String threadId = mStopManager.generateTextChat();
        Intent i = new Intent(StopActivity.this, ChatActivity.class);
        i.putExtra("thread_id", threadId);
        startActivity(i);
    }

    void displayCitizenInformation(CitizenInfo citizenInfo) {
        ((TextView) findViewById(R.id.stop_citizen_name)).setText(citizenInfo.getFullName());
        ((TextView) findViewById(R.id.stop_citizen_gender_age)).setText(citizenInfo.getAgeAndGender());
        ((TextView) findViewById(R.id.stop_citizen_language)).setText(citizenInfo.getPreferredLanguage());

        ((TextView) findViewById(R.id.stop_citizen_info_stops))
                .setText(getString(R.string.stop_citizen_num_stops, citizenInfo.getNumberOfStops()));
        ((TextView) findViewById(R.id.stop_citizen_info_alerts))
                .setText(getString(R.string.stop_citizen_num_alerts, citizenInfo.getNumberOfAlerts()));
        ((TextView) findViewById(R.id.stop_citizen_info_warnings))
                .setText(getString(R.string.stop_citizen_num_warnings, citizenInfo.getNumberOfWarnings()));
        ((TextView) findViewById(R.id.stop_citizen_info_threats))
                .setText(getString(R.string.stop_citizen_num_threats, citizenInfo.getNumberOfThreats()));
        ((TextView) findViewById(R.id.stop_citizen_info_citations))
                .setText(getString(R.string.stop_citizen_num_citations, citizenInfo.getNumberOfCitations()));
        ((TextView) findViewById(R.id.stop_citizen_info_intoxicated))
                .setText(getString(R.string.stop_citizen_num_intoxicated, citizenInfo.getNumberOfIntoxicated()));
        ((TextView) findViewById(R.id.stop_citizen_info_arrests))
                .setText(getString(R.string.stop_citizen_num_arrests, citizenInfo.getNumberOfArrests()));
        ((TextView) findViewById(R.id.stop_citizen_info_weapons))
                .setText(getString(R.string.stop_citizen_num_weapons, citizenInfo.getNumberOfWeapons()));
    }

    void displayCitizenProfilePicture(StorageReference photoReference) {
        Glide.with(StopActivity.this)
                .using(new FirebaseImageLoader())
                .load(photoReference)
                .into(((ImageView) findViewById(R.id.stop_officer_citizen_profile_picture)));
    }

} // end class StopActivity