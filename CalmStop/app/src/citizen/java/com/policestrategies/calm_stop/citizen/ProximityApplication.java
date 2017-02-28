package com.policestrategies.calm_stop.citizen;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.policestrategies.calm_stop.R;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Provides the ability to monitor for beacons in the background.
 * TODO: Enter battery saving mode if < 20%
 * @author Talal Abou Haiba
 */
public class ProximityApplication extends Application implements BootstrapNotifier {

    private RegionBootstrap mRegionBootstrap;
    private BeaconManager mBeaconManager;

    public void onCreate() {
        super.onCreate();

        Region region = new Region("all", null, null, null);
        mRegionBootstrap = new RegionBootstrap(this, region);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry (TLM) frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        mBeaconManager.setBackgroundScanPeriod(1100l);
        // set the time between each scan to be 1 minute (60 seconds)
        mBeaconManager.setBackgroundBetweenScanPeriod(5000);
        mBeaconManager.setDebug(true);


        try {
            // TODO: Validate bluetooth functionality
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (Exception e) { }

    }

    @Override
    public void didEnterRegion(Region arg0) {

        System.out.println("Entered region: " + arg0.toString());

        Intent intent = new Intent(this, BeaconDetectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent beaconDetectionIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri defaultSoundUri = RingtoneManager.
                getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Calm Stop")
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(beaconDetectionIntent)
                        .setSound(defaultSoundUri);
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().
                        getSystemService(Context.NOTIFICATION_SERVICE);

        String success = "Beacon detected";
        notificationBuilder.setContentText(success);

        // Generate random number for notification ID to avoid duplicates.
        int notifId = 456358;

        notificationManager.notify(notifId /* ID of notification */,
                notificationBuilder.build()); //Displays notification


        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        //this.startActivity(intent);
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        System.out.println("Did determine state for region with state: " + state);
    }

    @Override
    public void didExitRegion(Region arg0) {
        System.out.println("Did exit region: " + arg0.toString());
    }

}
