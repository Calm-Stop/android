package com.policestrategies.calm_stop.officer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SharedUtil;
import com.policestrategies.calm_stop.officer.beacon_registration.BeaconRegistrationActivity;

/**
 * @author Talal Abou Haiba
 */

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView mBottomNavigationView;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mDatabase;

    private String mUid;
    private String mDepartmentNumber;
    private String mCurrentlyRegisteredBeaconId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);

        findViewById(R.id.button_manage_beacon).setOnClickListener(this);
        findViewById(R.id.button_make_stop).setOnClickListener(this);

        mDepartmentNumber = Utility.getCurrentDepartmentNumber(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null && !mDepartmentNumber.isEmpty()) {
            mUid = mAuth.getCurrentUser().getUid();
        } else {
            mAuth.signOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        obtainCurrentBeaconInfo();

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setBottomNavigationView();

    } // end onCreate

    @Override
    public void onResume() {
        super.onResume();
        updateNavigationMenuSelection(0);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_manage_beacon:
                Intent i = new Intent(getBaseContext(), BeaconRegistrationActivity.class);
                startActivity(i);
                break;

            case R.id.button_make_stop:
                Intent j = new Intent(getBaseContext(), StopActivity.class);
                startActivity(j);
                break;

        }
    }

    private void obtainCurrentBeaconInfo() {

        DatabaseReference officerProfileReference = mDatabase.child("officer")
                .child(mDepartmentNumber).child(mUid).child("profile").getRef();

        officerProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("beacon")) {
                    mCurrentlyRegisteredBeaconId = dataSnapshot.child("beacon").getValue().toString();
                }
                updateCurrentBeaconInfo();
                SharedUtil.dismissProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    } // end obtainCurrentBeaconInfo


    private void updateCurrentBeaconInfo() {
        String beaconStatus;
        if (mCurrentlyRegisteredBeaconId != null && !mCurrentlyRegisteredBeaconId.isEmpty()) {
            beaconStatus = "Connected to beacon #" + mCurrentlyRegisteredBeaconId;
            ((ImageView) findViewById(R.id.beacon_icon))
                    .setImageResource(R.mipmap.ic_done_all_black_48dp);
        } else {
            beaconStatus = "No currently registered beacons";
            ((ImageView) findViewById(R.id.beacon_icon))
                    .setImageResource(R.mipmap.ic_warning_black_48dp);
        }
        ((TextView) findViewById(R.id.text_cardview_beacon_status)).setText(beaconStatus);
    }

    private void setBottomNavigationView() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                break;

                            case R.id.action_ratings:
                                intent = new Intent(getBaseContext(), RatingActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                break;

                            case R.id.action_history:
                                intent = new Intent(getBaseContext(), HistoryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                break;

                            case R.id.action_account:
                                intent = new Intent(getBaseContext(), AccountActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });

    }

    private void updateNavigationMenuSelection(int menu) {
        for (int i = 0; i < 4; i++) {
            MenuItem item = mBottomNavigationView.getMenu().getItem(i);
            item.setChecked(i == menu);
        }
    }

} // end DashboardActivity

