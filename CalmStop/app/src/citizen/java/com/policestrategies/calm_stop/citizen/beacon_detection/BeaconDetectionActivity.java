package com.policestrategies.calm_stop.citizen.beacon_detection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.BeaconSimulator;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SharedUtil;
import com.policestrategies.calm_stop.citizen.LoginActivity;
import com.policestrategies.calm_stop.citizen.stop.StopActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Landing page after a beacon is detected. Find the UUID of the beacon and connect to the officer.
 * @author Talal Abou Haiba
 */
public class BeaconDetectionActivity extends AppCompatActivity {

    private BeaconManager mBeaconManager;
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;

    private ProgressDialog mProgressDialog;
    private String mUid;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detection);

        mRecyclerView = (RecyclerView) findViewById(R.id.beacon_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(null);

        mProgressDialog = ProgressDialog.show(this, "", "Scanning");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            mUid = firebaseAuth.getCurrentUser().getUid();
        } else {
            firebaseAuth.signOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (BeaconSimulator.USE_SIMULATED_BEACONS) {
            BeaconManager.setBeaconSimulator(new BeaconSimulator());
            ((BeaconSimulator) BeaconManager.getBeaconSimulator()).createBasicSimulatedBeacons();
        }

        enableBeaconRanging();
    }

    @Override
    protected void onDestroy() {
        SharedUtil.dismissProgressDialog(mProgressDialog);
        super.onDestroy();
    }

    private void enableBeaconRanging() {
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {

                if (collection.size() == 0) {
                    return;
                }

                final List<Pair<String, String>> scannedInstanceIds = new ArrayList<>();

                for (Beacon beacon : collection) {
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                        Pair<String, String> beaconInfo = new Pair<>(beacon.getId2().toString(),
                                String.format(Locale.getDefault(), "%.1f", beacon.getDistance())
                                        + "m");
                        scannedInstanceIds.add(beaconInfo);
                    }
                }
                try {
                    mBeaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    System.out.println("Failed to stop ranging beacons in BeaconDetectionActivity");
                }
                downloadOfficerInformation(scannedInstanceIds);
            }
        });

        Region region = new Region("all-beacons-region", null, null, null);

        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (Exception e) {
            SharedUtil.dismissProgressDialog(mProgressDialog);
            Toast.makeText(BeaconDetectionActivity.this, "Failed to range beacons",
                    Toast.LENGTH_SHORT).show();
        }

    } // end enableBeaconRanging

    /**
     * Obtains the officer information associated with each beacon instance id from firebase.
     * @param instanceIds detected by the didRangeBeaconsInRegion function
     */
    private void downloadOfficerInformation(final List<Pair<String, String>> instanceIds) {

        final List<BeaconObject> scannedBeacons = new ArrayList<>();

        final DatabaseReference databaseReference = mDatabase.getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Pair<String, String> instance : instanceIds) {
                    String officerUid;
                    String officerLastName;
                    String officerDepartmentNumber;
                    String officerBadgeNumber;
                    String officerProfilePicturePath;

                    if(!dataSnapshot.child("beacons").hasChild(instance.first)) {
                        continue;
                    }

                    if(!((boolean) dataSnapshot.child("beacons").child(instance.first)
                            .child("active").getValue())) {
                        continue;
                    }

                    // Look up the beacon and obtain officerUid and department number
                    officerUid = dataSnapshot.child("beacons").child(instance.first)
                            .child("officer").child("uid").getValue().toString();
                    officerDepartmentNumber = dataSnapshot.child("beacons").child(instance.first)
                            .child("officer").child("department").getValue().toString();

                    // Look up officer name
                    officerLastName = dataSnapshot.child("officer")
                            .child(officerDepartmentNumber).child(officerUid)
                            .child("profile").child("last_name").getValue().toString();

                    officerBadgeNumber = dataSnapshot.child("officer")
                            .child(officerDepartmentNumber).child(officerUid)
                            .child("profile").child("badge").getValue().toString();

                    officerProfilePicturePath = dataSnapshot.child("officer")
                            .child(officerDepartmentNumber).child(officerUid)
                            .child("profile").child("photo").getValue().toString();

                    scannedBeacons.add(new BeaconObject(officerLastName, officerDepartmentNumber,
                            officerUid, instance.first, officerBadgeNumber, instance.second,
                            officerProfilePicturePath));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mRecyclerView.setAdapter(
                                new BeaconDetectionAdapter(BeaconDetectionActivity.this, scannedBeacons,
                                new BeaconDetectionAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(BeaconObject item) {
                                        syncWithOfficer(item);
                                    }
                                }));
                        SharedUtil.dismissProgressDialog(mProgressDialog);
                    }
                });

            }
    
            @Override
            public void onCancelled(DatabaseError databaseError) {
                SharedUtil.dismissProgressDialog(mProgressDialog);
                System.out.println("Database error while retrieving officer information");
            }
        });

    } // end downloadOfficerInformation

    /**
     * Allows the citizen to choose to speak with a certain officer.
     * @param officer that was clicked on
     */
    private void syncWithOfficer(final BeaconObject officer) {

       new AlertDialog.Builder(this)
               .setTitle("Communicate with Officer")
               .setMessage("Would you like to speak with Officer " + officer.getOfficerName() + "?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       acceptStop(officer);
                   }
               })
               .setNegativeButton("No", null)
               .setCancelable(true)
               .show();
    }

    private void acceptStop(BeaconObject officer) {
        mDatabase.child("beacons").child(officer.getBeaconInstanceId()).child("citizens").setValue(mUid);

        DatabaseReference beaconReference = mDatabase.child("beacons")
                .child(officer.getBeaconInstanceId()).getRef();
        beaconReference.addChildEventListener(new BeaconChildEventListener(this, officer));
        ProgressDialog.show(this, "", "Waiting for officer", true, false);
    }

    void beginStop(BeaconObject officer, String stop) {
        Intent i = new Intent(this, StopActivity.class);
        i.putExtra("officer_department", officer.getDepartmentNumber());
        i.putExtra("officer_id", officer.getOfficerUid());
        i.putExtra("stop_id", stop); //"-Kj9XharhDa88IxXePwx"
        startActivity(i);
        finish();
    }

} // end class BeaconDetectionActivity
