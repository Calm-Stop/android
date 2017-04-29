package com.policestrategies.calm_stop.officer.dashboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

/**
 * Manages beacon in {@link DashboardActivity}
 * @author Talal Abou Haiba
 */

class DashboardManager {

    private Activity mActivityReference;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog mProgressDialog;
    private BeaconManager mBeaconManager;

    DashboardManager(Activity ctx, DatabaseReference databaseReference, ProgressDialog pd,
                     BeaconManager manager) {
        mActivityReference = ctx;
        mDatabaseReference = databaseReference;
        mProgressDialog = pd;
        mBeaconManager = manager;
    }

    /**
     * Called from {@link BeaconRangeNotifier} following a successful scan of the registered beacon.
     * Activates the beacon and begins scanning for citizen responses.
     * @param instance id that was scanned
     * @param region that the instance was scanned in
     */
    void onSuccessfulBeaconScan(final String instance, final Region region) {

        mActivityReference.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activateBeacon(instance);
                scanForCitizenResponse(instance);

                try {
                    mBeaconManager.stopRangingBeaconsInRegion(region);
                } catch (Exception e) {
                    Toast.makeText(mActivityReference, "Failed to stop ranging beacons",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void activateBeacon(String beaconId) {
        DatabaseReference beaconDatabaseReference = mDatabaseReference.child("beacons")
                .child(beaconId).getRef();
        beaconDatabaseReference.child("active").setValue(true);
    }

    private void scanForCitizenResponse(String beaconId) {
        mProgressDialog = ProgressDialog.show(mActivityReference, "", "Scanning for citizen",
                true, false);
        DatabaseReference beaconDatabaseReference = mDatabaseReference.child("beacons")
                .child(beaconId).child("citizen").getRef();
        beaconDatabaseReference.addChildEventListener(new BeaconChildEventListener(mProgressDialog));
    }

} // end class DashboardManager
