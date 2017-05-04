package com.policestrategies.calm_stop.citizen.stop;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Handles backend of traffic stop in {@link StopActivity}
 * @author Talal Abou Haiba
 */

class StopManager {

    private Activity mActivityReference;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    private OfficerInfo mOfficerInfo;

    private String mOfficerDepartment;
    private String mOfficerUid;
    private String mStopId;

    StopManager(Activity ctx) {
        mActivityReference = ctx;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mOfficerInfo = new OfficerInfo();
        loadIntentExtras();
        retieveOfficerInfo();
    }

    private void loadIntentExtras() {
        Intent currentIntent = mActivityReference.getIntent();
        mOfficerDepartment = currentIntent.getExtras().getString("officer_department");
        mOfficerUid = currentIntent.getExtras().getString("officer_id");
        mStopId = currentIntent.getExtras().getString("stop_id");
    }

    private void retieveOfficerInfo() {
        DatabaseReference officerProfileReference = mDatabaseReference.child("officer")
                .child(mOfficerDepartment).child(mOfficerUid).child("profile").getRef();
        officerProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mOfficerInfo.setOfficerProfile(dataSnapshot);
                mActivityReference.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("O B T A I N E D  D A T A");
                        System.out.println(mOfficerInfo.getFullName());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

} // end class StopManager
