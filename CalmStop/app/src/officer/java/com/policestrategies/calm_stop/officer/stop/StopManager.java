package com.policestrategies.calm_stop.officer.stop;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Handles backend of traffic stop in {@link StopActivity}
 * @author Talal Abou Haiba
 */

class StopManager {

    private Activity mActivityReference;
    private DatabaseReference mDatabaseReference;

    private CitizenInfo mCitizenInfo;

    private String mCitizenUid;
    private String mStopId;

    StopManager(Activity context) {
        mActivityReference = context;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mCitizenInfo = new CitizenInfo();
        loadIntentExtras();
        retrieveCitizenInfo();
    }

    private void loadIntentExtras() {
        Intent currentIntent = mActivityReference.getIntent();
        mCitizenUid = currentIntent.getExtras().getString("citizen_id");
        mStopId = currentIntent.getExtras().getString("stop_id");
    }

    private void retrieveCitizenInfo() {
        DatabaseReference citizenInfoReference = mDatabaseReference.child("citizen")
                .child(mCitizenUid).child("profile").child("info").getRef(); // TODO: NO PROFILE #45
        citizenInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 mCitizenInfo.setCitizenInfo(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

} // end class StopManager
