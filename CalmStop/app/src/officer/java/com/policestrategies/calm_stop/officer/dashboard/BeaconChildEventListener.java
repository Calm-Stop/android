package com.policestrategies.calm_stop.officer.dashboard;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * @author Talal Abou Haiba
 */
class BeaconChildEventListener implements ChildEventListener {

    private DashboardManager mDashboardManager;

    BeaconChildEventListener(DashboardManager dashboardManager) {
        mDashboardManager = dashboardManager;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String citizenUid = dataSnapshot.getValue().toString();
        System.out.println("Added child:" + dataSnapshot.getValue());
        mDashboardManager.writeStop(citizenUid);
        mDashboardManager.disableScanningIndicator();
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
        mDashboardManager.disableScanningIndicator();
    }

}
