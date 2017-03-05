package com.policestrategies.calm_stop.officer.beacon_registration;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.policestrategies.calm_stop.R;

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
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_registration);

        verifyBluetooth();

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        findViewById(R.id.button_scan_beacon_registration).setOnClickListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.beacon_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(null);
        mBeaconManager.bind(this);

    } // end onCreate

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_scan_beacon_registration:
                scanForBeacons();
                break;

            case R.id.beacon_recycler_view:
                System.out.println("Click!!!!!");
                break;
        }

    } // end onClick

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
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

        System.out.println(mDatabase.toString());
        DatabaseReference officerDatabaseReference = mDatabase.child("beacons")
                .child(beaconInstanceId[beaconInstanceId.length - 1]).child("officer").getRef();

        officerDatabaseReference.child("department").setValue("14566");
        officerDatabaseReference.child("uid").setValue(mAuth.getCurrentUser().getUid());
        finish(); // TODO: Validate success?
    }

    @Override
    public void onBeaconServiceConnect() {
        System.out.println("Beacon service connected!");

        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, final Region region) {

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

                        findViewById(R.id.button_scan_beacon_registration).setVisibility(View.GONE);
                    }
                });

            }
        });

    } // end onBeaconServiceConnect

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
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
