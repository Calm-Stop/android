package com.policestrategies.calm_stop.citizen.stop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;

/**
 * Allows the citizen to interact with and view the officer making the traffic stop.
 * @author Talal Abou Haiba
 */
public class StopActivity extends AppCompatActivity implements View.OnClickListener {

    private StopManager mStopManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        findViewById(R.id.stop_documents_button).setOnClickListener(this);
        findViewById(R.id.stop_chat_button).setOnClickListener(this);

        findViewById(R.id.stop_chat_button).setVisibility(View.INVISIBLE);

        mStopManager = new StopManager(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.stop_documents_button:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.stop_chat_button:
                mStopManager.handleChatButton();
                break;
        }
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

    void enableChat() {
        findViewById(R.id.stop_chat_button).setVisibility(View.VISIBLE);
    }

} // end class StopActivity
