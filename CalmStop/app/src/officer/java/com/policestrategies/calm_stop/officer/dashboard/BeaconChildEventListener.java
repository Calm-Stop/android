package com.policestrategies.calm_stop.officer.dashboard;

import android.app.ProgressDialog;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.policestrategies.calm_stop.SharedUtil;

/**
 * @author Talal Abou Haiba
 */
class BeaconChildEventListener implements ChildEventListener {

    private ProgressDialog mProgressDialog;

    BeaconChildEventListener(ProgressDialog progressDialogReference) {
        mProgressDialog = progressDialogReference;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String citizenUid = dataSnapshot.getValue().toString();
        System.out.println("Added child:" + dataSnapshot.getValue());
        SharedUtil.dismissProgressDialog(mProgressDialog);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        System.out.println("Changed child");
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        System.out.println("Removed child");
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        System.out.println("Moved child");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        System.out.println("Cancelled child");
        SharedUtil.dismissProgressDialog(mProgressDialog);
    }

}
