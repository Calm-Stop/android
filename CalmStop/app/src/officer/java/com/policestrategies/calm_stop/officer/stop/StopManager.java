package com.policestrategies.calm_stop.officer.stop;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.chat.Message;

/**
 * Handles backend of traffic stop in {@link StopActivity}
 * @author Talal Abou Haiba
 */

class StopManager {

    private Activity mActivityReference;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    private CitizenInfo mCitizenInfo;

    private String mOfficerUid;
    private String mCitizenUid;
    private String mStopId;
    private String mThreadId;


    StopManager(Activity context) {
        mActivityReference = context;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mCitizenInfo = new CitizenInfo();
        loadIntentExtras();
        retrieveCitizenInfo();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            mOfficerUid = firebaseAuth.getCurrentUser().getUid();
        }
    }

    String generateTextChat() {
        if (mThreadId != null && !mThreadId.isEmpty()) {
            return mThreadId;
        }
        DatabaseReference threadReference = mDatabaseReference.child("threads").getRef().push();
        mThreadId = threadReference.getKey();

        Message starterMessage = new Message("Hello there!", System.currentTimeMillis(), mOfficerUid);
        threadReference.child("messages").push().setValue(starterMessage);

        // Post thread id to the current stop
        DatabaseReference stopReference = mDatabaseReference.child("stops").child(mStopId).getRef();
        stopReference.child("threadID").setValue(mThreadId);

        return mThreadId;
    }

    private void loadIntentExtras() {
        Intent currentIntent = mActivityReference.getIntent();
        mCitizenUid = currentIntent.getExtras().getString("citizen_id");
        mStopId = currentIntent.getExtras().getString("stop_id");
    }

    private void retrieveCitizenInfo() {
        DatabaseReference citizenInfoReference = mDatabaseReference.child("citizen")
                .child(mCitizenUid).getRef();
        citizenInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCitizenInfo.setCitizenInfo(dataSnapshot);
                mActivityReference.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((StopActivity) mActivityReference).displayCitizenInformation(mCitizenInfo);
                        ((StopActivity) mActivityReference).displayCitizenProfilePicture(
                                mStorageReference.child(mCitizenInfo.getPhotoUrl()));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

} // end class StopManager
