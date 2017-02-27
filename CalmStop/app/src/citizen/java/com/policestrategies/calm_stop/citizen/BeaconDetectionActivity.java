package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.Collection;

/**
 * Landing page after a beacon is detected. Find the UUID of the beacon and connect to the officer.
 * TODO: After finding valid officers, allow the user to leave this activity to begin communication
 * @author Talal Abou Haiba
 */
public class BeaconDetectionActivity extends AppCompatActivity {

    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detection);
        ((TextView) findViewById(R.id.text_namespace_id)).setText("Scanning....");


        mBeaconManager = BeaconManager.getInstanceForApplication(this);

        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if (collection.size() > 0) {
                    Beacon beacon = collection.iterator().next();
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {

                        Identifier namespaceId = beacon.getId1();
                        Identifier instanceId = beacon.getId2();


                        String scannedID = "Found a beacon with namespaceID: " + namespaceId + " and" +
                                " instanceID: " + instanceId;

                        final String namespace = "Namespace ID: " + namespaceId;
                        final String instance = "Instance ID: " + instanceId;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.text_namespace_id)).setText(namespace);
                                ((TextView) findViewById(R.id.text_instance_id)).setText(instance);
                            }
                        });

                        System.out.println("SCANNED ID::::::: " + scannedID);
                    }

                }
            }
        });

        Region region = new Region("all-beacons-region", null, null, null);

        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (Exception e) {
            Toast.makeText(BeaconDetectionActivity.this, "Failed to range beacons",
                    Toast.LENGTH_SHORT).show();
        }

        System.out.println("Down here-------------------------------");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

} // end class BeaconDetectionActivity
