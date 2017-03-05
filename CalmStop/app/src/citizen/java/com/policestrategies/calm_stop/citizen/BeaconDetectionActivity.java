package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

/**
 * Landing page after a beacon is detected. Find the UUID of the beacon and connect to the officer.
 * TODO: After finding valid officers, allow the user to leave this activity to begin communication
 * @author Talal Abou Haiba
 */
public class BeaconDetectionActivity extends AppCompatActivity {

    private BeaconManager mBeaconManager;

    private DatabaseReference mDatabase;

    private String mDepartmentNumber;
    private String mOfficerUid;
    private String mOfficerName;

    private Bitmap mOfficerPhoto;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detection);
        ((TextView) findViewById(R.id.text_namespace_id)).setText("Scanning....");
        findViewById(R.id.officer_information_card_view).setVisibility(View.INVISIBLE);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        // TODO: This is really dirty. Definitely need to clean this up - demo purposes only.
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

                        DatabaseReference beaconReference = mDatabase.child("beacons")
                                .child(instanceId.toString()).child("officer").getRef();

                        beaconReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mDepartmentNumber = dataSnapshot.child("department").getValue()
                                        .toString();
                                mOfficerUid = dataSnapshot.child("uid").getValue().toString();

                                DatabaseReference officerReference = mDatabase.child("officer")
                                        .child(mDepartmentNumber).child(mOfficerUid).getRef();

                                officerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        mOfficerName = "Officer " + dataSnapshot.child("profile")
                                                .child("last_name").getValue().toString();

                                        final String officerPhotoUrl = dataSnapshot.child("photo")
                                                .getValue().toString();

                                        System.out.println("Up here 1111");

                                        System.out.println("Set image");

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                new DownloadImageTask((ImageView) findViewById(R.id.officer_photo))
                                                        .execute(officerPhotoUrl);

                                                String deptNum = "Department # " + mDepartmentNumber;
                                                ((TextView) findViewById(R.id.officer_department_number))
                                                        .setText(deptNum);
                                                ((TextView) findViewById(R.id.officer_name)).setText(mOfficerName);
                                                findViewById(R.id.text_namespace_id).setVisibility(View.GONE);
                                                findViewById(R.id.officer_information_card_view).setVisibility(View.VISIBLE);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

} // end class BeaconDetectionActivity
