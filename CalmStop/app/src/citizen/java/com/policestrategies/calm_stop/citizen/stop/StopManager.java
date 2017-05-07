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
import com.policestrategies.calm_stop.chat.ChatActivity;

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
    private String mThreadId;

    StopManager(Activity ctx) {
        mActivityReference = ctx;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mOfficerInfo = new OfficerInfo();
        loadIntentExtras();
        retieveOfficerInfo();
        listenForChatActivation();
    }

    void handleChatButton() {
        if (chatEnabled()) {
            Intent i = new Intent(mActivityReference, ChatActivity.class);
            i.putExtra("thread_id", mThreadId);
            mActivityReference.startActivity(i);
        }
    }

    void enableChat(String threadId) {
        if (!chatEnabled()) {
            mThreadId = threadId;
        }
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
                        ((StopActivity) mActivityReference).displayOfficerInfo(mOfficerInfo);
                        ((StopActivity) mActivityReference).displayOfficerProfilePicture(
                                mStorageReference.child(mOfficerInfo.getPhotoUrl()));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void listenForChatActivation() {
        DatabaseReference stopReference = mDatabaseReference.child("stops")
                .child(mStopId).getRef();
        stopReference.addChildEventListener(new StopChatChildEventListener(this));
    }

    private boolean chatEnabled() {
        return (mThreadId != null && !mThreadId.isEmpty());
    }

} // end class StopManager
