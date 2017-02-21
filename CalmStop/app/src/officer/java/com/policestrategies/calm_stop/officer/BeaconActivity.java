package com.policestrategies.calm_stop.officer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Currently WIP - testing beacon functionality in here.
 * @author Talal Abou Haiba
 */

public class BeaconActivity extends AppCompatActivity implements View.OnClickListener, BeaconConsumer{

    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        verifyBluetooth();

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        findViewById(R.id.beacon_debug).setOnClickListener(this);

        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry (TLM) frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));

        mBeaconManager.bind(this);
        mBeaconManager.setDebug(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.beacon_debug:
                System.out.println("Beacon debug has been pressed!");
                try {
                    mBeaconManager.startMonitoringBeaconsInRegion(new Region("uniqueId", null, null, null));
                    mBeaconManager.startRangingBeaconsInRegion(new Region("uniqueId", null,null,null));
                } catch (RemoteException e) {
                    System.out.println("Caught remote exception E");
                }
                break;

        }
    }

    @Override
    public void onBeaconServiceConnect() {
        System.out.println("Beacon service connected!");
        mBeaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                System.out.println("Enter region");
                Toast.makeText(BeaconActivity.this, "Entered Region!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didExitRegion(Region region) {
                System.out.println("exit region");
                Toast.makeText(BeaconActivity.this, "Exited Region!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                String print = "Swapped states: " + i + ", region: " + region.toString();
                System.out.println(print);
                Toast.makeText(BeaconActivity.this, print, Toast.LENGTH_SHORT).show();

            }
        });
        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if (collection.size() > 0) {
                    String out = "The first beacon I see is about "+collection.iterator().next().getDistance()+" meters away.";
                    System.out.println(out);
                }
            }
        });

    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
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
        }
        catch (RuntimeException e) {
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

    }


} // end class BeaconActivity
