package com.policestrategies.calm_stop.citizen.stop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;

/**
 * @author Talal Abou Haiba
 */

// TODO: Scanning for stop_id in BeaconDetectionActivity. When detected, start this activity
// TODO: Scanning for thread_id in stop (should be handled by stopmanager). When detected, enable button

public class StopActivity extends AppCompatActivity {

    private StopManager mStopManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        mStopManager = new StopManager(this);
    }

    void displayOfficerInfo(OfficerInfo citizenInfo) {
        ((TextView) findViewById(R.id.stop_officer_name)).setText(
                getString(R.string.stop_officer_name, citizenInfo.getFullName()));
        ((TextView) findViewById(R.id.stop_officer_badge)).setText(
                getString(R.string.stop_officer_badge, citizenInfo.getBadgeNumber()));
        ((TextView) findViewById(R.id.stop_officer_department)).setText(
                getString(R.string.stop_officer_department, citizenInfo.getDepartmentNumber()));
    }

    void displayOfficerProfilePicture(StorageReference photoReference) {
        Glide.with(StopActivity.this)
                .using(new FirebaseImageLoader())
                .load(photoReference)
                .into(((ImageView) findViewById(R.id.stop_officer_profile_picture)));
    }
} // end class StopActivity
