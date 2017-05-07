package com.policestrategies.calm_stop.citizen.beacon_detection;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * @author Talal Abou Haiba
 */
class BeaconChildEventListener implements ChildEventListener {

    private final String TAG = "BeaconChildListener";

    private Activity mActivityReference;
    private BeaconObject mOfficer;

    BeaconChildEventListener(Activity ctx, BeaconObject officer) {
        mActivityReference = ctx;
        mOfficer = officer;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildAdded");
        System.out.println(dataSnapshot.getKey());
        if (dataSnapshot.getKey().equals("stop_id")) {
            String stopId = dataSnapshot.getValue().toString();
            System.out.println("Got stop id: "  + stopId);
            ((BeaconDetectionActivity) mActivityReference).beginStop(mOfficer, stopId);
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
