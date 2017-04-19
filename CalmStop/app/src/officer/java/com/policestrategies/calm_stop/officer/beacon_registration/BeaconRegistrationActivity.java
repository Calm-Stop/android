package com.policestrategies.calm_stop.officer.beacon_registration;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.policestrategies.calm_stop.officer.LoginActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Allows the officer to associate his UUID with a beacon.
 * @author Talal Abou Haiba
 */
public class BeaconRegistrationActivity extends AppCompatActivity implements View.OnClickListener,
        BeaconConsumer {

    private BeaconManager mBeaconManager;

    private DatabaseReference mDatabase;

    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private String mUid;

    private String mDepartmentNumber;
    private String mCurrentlyRegisteredBeaconId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_registration);

        verifyBluetooth();
        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mDepartmentNumber = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE).getString(getString(R.string.shared_preferences_department_number),
                "");

        if (mAuth.getCurrentUser() != null && !mDepartmentNumber.isEmpty()) {
            mUid = mAuth.getCurrentUser().getUid();
        } else {
            mAuth.signOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        obtainCurrentBeaconInfo();

        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        findViewById(R.id.button_scan_beacon_registration).setOnClickListener(this);
        findViewById(R.id.beacon_status_card_view).setOnClickListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.beacon_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(null);
        mBeaconManager.bind(this);

        if (BeaconSimulator.USE_SIMULATED_BEACONS) {
            BeaconManager.setBeaconSimulator(new BeaconSimulator());
            ((BeaconSimulator) BeaconManager.getBeaconSimulator()).createBasicSimulatedBeacons();
        }

    } // end onCreate

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_scan_beacon_registration:
                scanForBeacons();
                break;
            case R.id.beacon_status_card_view:
                promptBeaconStatus();
                break;
        }
    } // end onClick

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    private void promptBeaconStatus() {
        if (mCurrentlyRegisteredBeaconId != null && !mCurrentlyRegisteredBeaconId.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Unregister beacon")
                    .setMessage("Would you like to unregister beacon #" +
                            mCurrentlyRegisteredBeaconId + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deregisterBeacon(mCurrentlyRegisteredBeaconId);
                            updateCurrentBeaconInfo();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do nothing
                        }
                    })
                    .setCancelable(true)
                    .show();
        }
    }

    private void scanForBeacons() {

        System.out.println("Scanning for beacons!");

        // TODO: This should only look for our specific beacons
        Region region = new Region("all-beacons-region", null, null, null);

        try {
            System.out.println("Starting ranging");
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (Exception e) {
            Toast.makeText(BeaconRegistrationActivity.this, "Failed to start ranging beacons",
                    Toast.LENGTH_SHORT).show();
            System.out.println(e.toString());
        }

    } // end scanForBeacons

    private void synchronize_beacon(BeaconObject beacon) {

        String[] beaconInstanceId = beacon.getInstance().split(" ");
        final String beaconRegistrationId = beaconInstanceId[beaconInstanceId.length - 1];

        if (mCurrentlyRegisteredBeaconId != null && !mCurrentlyRegisteredBeaconId.isEmpty() &&
                !mCurrentlyRegisteredBeaconId.equals(beaconRegistrationId)) {
            // Override beacon
            new AlertDialog.Builder(this)
                    .setTitle("Override current beacon")
                    .setMessage("Would you like to override your currently registered beacon with" +
                            " beacon #" + beaconRegistrationId + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deregisterBeacon(mCurrentlyRegisteredBeaconId);
                            registerBeacon(beaconRegistrationId);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do nothing
                        }
                    })
                    .setCancelable(true)
                    .show();
        } else {
            registerBeacon(beaconRegistrationId);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        System.out.println("Beacon service connected!");

        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, final Region region) {

                if (collection.size() == 0) {
                    return;
                }

                final List<BeaconObject> scannedBeacons = new ArrayList<>();

                for (Beacon beacon : collection) {
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {

                        String namespace = "Namespace ID: " + beacon.getId1();
                        String instance = "Instance ID: " + beacon.getId2();
                        String range = String.format(Locale.getDefault(), "%.1f",
                                beacon.getDistance()) + "m";

                        scannedBeacons.add(new BeaconObject(namespace, instance, range));
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mRecyclerView.setAdapter(new BeaconRegistrationAdapter(scannedBeacons,
                                new BeaconRegistrationAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(BeaconObject item) {
                                        synchronize_beacon(item);
                                    }
                                }));

                        try {
                            mBeaconManager.stopRangingBeaconsInRegion(region);
                        } catch (Exception e) {
                            Toast.makeText(BeaconRegistrationActivity.this,
                                    "Failed to stop ranging beacons",
                                    Toast.LENGTH_SHORT).show();
                        }

                        mRecyclerView.setVisibility(View.VISIBLE);
                        findViewById(R.id.button_scan_beacon_registration).setVisibility(View.GONE);
                    }
                });

            }
        });

    } // end onBeaconServiceConnect

    private void obtainCurrentBeaconInfo() {

        DatabaseReference officerProfileReference = mDatabase.child("officer")
                .child(mDepartmentNumber).child(mUid).child("profile").getRef();

        officerProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("beacon")) {
                    mCurrentlyRegisteredBeaconId = dataSnapshot.child("beacon").getValue().toString();
                }
                updateCurrentBeaconInfo();
                SharedUtil.dismissProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    } // end obtainCurrentBeaconInfo

    private void updateCurrentBeaconInfo() {
        String beaconStatus;
        if (mCurrentlyRegisteredBeaconId != null && !mCurrentlyRegisteredBeaconId.isEmpty()) {
            beaconStatus = "Connected to beacon #" + mCurrentlyRegisteredBeaconId;
            ((ImageView) findViewById(R.id.beacon_icon))
                    .setImageResource(R.mipmap.ic_done_all_black_48dp);
        } else {
            beaconStatus = "No currently registered beacons";
            ((ImageView) findViewById(R.id.beacon_icon))
                    .setImageResource(R.mipmap.ic_warning_black_48dp);
        }
        ((TextView) findViewById(R.id.text_cardview_beacon_status)).setText(beaconStatus);
    }

    private void registerBeacon(String beaconId) {
        mCurrentlyRegisteredBeaconId = beaconId;

        DatabaseReference beaconDatabaseReference = mDatabase.child("beacons")
                .child(beaconId).getRef();
        DatabaseReference officerDatabaseReference = mDatabase.child("officer")
                .child(mDepartmentNumber).child(mUid).child("profile").getRef();

        beaconDatabaseReference.child("active").setValue(false);
        beaconDatabaseReference.child("stop_id").setValue("");
        beaconDatabaseReference.child("officer").child("department").setValue(mDepartmentNumber);
        beaconDatabaseReference.child("officer").child("uid").setValue(mUid);
        officerDatabaseReference.child("beacon").setValue(beaconId);

        updateCurrentBeaconInfo();
        Toast.makeText(this, "Beacon registration successful", Toast.LENGTH_SHORT).show();
        mRecyclerView.setVisibility(View.GONE);
        findViewById(R.id.button_scan_beacon_registration).setVisibility(View.VISIBLE);
    }

    private void deregisterBeacon(String beaconId) {
        mCurrentlyRegisteredBeaconId = "";

        DatabaseReference beaconDatabaseReference = mDatabase.child("beacons").getRef();
        DatabaseReference officerDatabaseReference = mDatabase.child("officer")
                .child(mDepartmentNumber).child(mUid).child("profile").getRef();

        beaconDatabaseReference.child(beaconId).removeValue();
        officerDatabaseReference.child("beacon").removeValue();
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                SharedUtil.dismissProgressDialog(mProgressDialog);
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this " +
                        "application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }

        } catch (RuntimeException e) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    } // end verifyBluetooth

} // end class BeaconRegistrationActivity
