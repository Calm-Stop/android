package com.policestrategies.calm_stop.officer.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.officer.LoginActivity;
import com.policestrategies.calm_stop.officer.StopActivity;
import com.policestrategies.calm_stop.officer.Utility;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Manages beacon in {@link DashboardActivity}
 * @author Talal Abou Haiba
 */

class DashboardManager {

    private Activity mActivityReference;
    private DatabaseReference mDatabaseReference;
    private BeaconManager mBeaconManager;

    private String mUid;
    private String mDepartmentNumber;
    private String mCurrentlyRegisteredBeaconId;


    DashboardManager(Activity ctx, BeaconManager manager) {
        mActivityReference = ctx;
        mBeaconManager = manager;

        initialize();
    }

    private void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDepartmentNumber = Utility.getCurrentDepartmentNumber(mActivityReference);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null && !mDepartmentNumber.isEmpty()) {
            mUid = mAuth.getCurrentUser().getUid();
        } else {
            mAuth.signOut();
            Intent i = new Intent(mActivityReference, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mActivityReference.startActivity(i);
        }

    }

    boolean beaconRegistrationStatus() {
        return mCurrentlyRegisteredBeaconId != null && !mCurrentlyRegisteredBeaconId.isEmpty();
    }

    void setCurrentlyRegisteredBeaconId(String newBeaconId) {
        mCurrentlyRegisteredBeaconId = newBeaconId;
    }

    String getCurrentlyRegisteredBeaconId() {
        return mCurrentlyRegisteredBeaconId;
    }

    String getUid() {
        return mUid;
    }

    String getDepartmentNumber() {
        return mDepartmentNumber;
    }

    void writeStop(String citizenUid) {
        DatabaseReference stopReference = mDatabaseReference.child("stops").getRef().push();
        String stopId = stopReference.getKey();

        // Generate date and time
        Calendar c = Calendar.getInstance();
        String formattedDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(c.getTime());
        String formattedTime = new SimpleDateFormat("HH:mm", Locale.US).format(c.getTime());

        String latitude = "35.478";
        String longitude = "115.116";

        stopReference.child("citizenID").setValue(citizenUid);
        stopReference.child("officerID").setValue(mUid);
        stopReference.child("date").setValue(formattedDate);
        stopReference.child("time").setValue(formattedTime);
        stopReference.child("lat").setValue(latitude);
        stopReference.child("long").setValue(longitude);
        stopReference.child("reason").setValue("");

        // Write stop to officer uid
        final DatabaseReference officerStopsReference;
        officerStopsReference = FirebaseDatabase.getInstance().getReference("officer")
                .child(mDepartmentNumber).child(mUid).child("stops").getRef();

        mDatabaseReference.child("beacons").child(mCurrentlyRegisteredBeaconId)
                .child("stop_id").getRef().setValue(stopId);

        officerStopsReference.push().getRef().child("stop_id").setValue(stopId);

        Intent i = new Intent(mActivityReference, StopActivity.class);
        i.putExtra("stop_id", stopId);
        i.putExtra("citizen_id", citizenUid);
        mActivityReference.startActivity(i);
        mActivityReference.finish();
    }

    private void enableScanningIndicator() {
        mActivityReference.findViewById(R.id.loading_indicator_layout).setVisibility(View.VISIBLE);
    }

    void disableScanningIndicator() {
        mActivityReference.findViewById(R.id.loading_indicator_layout).setVisibility(View.GONE);
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
                System.out.println("Got instance: " + instance);
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
        enableScanningIndicator();
        DatabaseReference beaconDatabaseReference = mDatabaseReference.child("beacons")
                .child(beaconId).child("citizen").getRef();
        beaconDatabaseReference.addChildEventListener(new BeaconChildEventListener(this));
    }

} // end class DashboardManager
