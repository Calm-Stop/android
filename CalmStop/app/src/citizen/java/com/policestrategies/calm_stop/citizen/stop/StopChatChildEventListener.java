package com.policestrategies.calm_stop.citizen.stop;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * @author Talal Abou Haiba
 */

class StopChatChildEventListener implements ChildEventListener {

    private final String TAG = "ChatChildEventListener";

    private StopManager mStopManager;

    StopChatChildEventListener(StopManager stopManager) {
        mStopManager = stopManager;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildAdded");
        System.out.println(dataSnapshot.getKey());
        if (dataSnapshot.getKey().equals("threadID")) {
            String threadId = dataSnapshot.getValue().toString();
            System.out.println("Got thread id: "  + threadId);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
    }

} // end class StopChatChildEventListener
