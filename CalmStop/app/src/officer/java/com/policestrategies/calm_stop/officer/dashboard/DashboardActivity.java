package com.policestrategies.calm_stop.officer.dashboard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.BeaconSimulator;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SharedUtil;
import com.policestrategies.calm_stop.officer.AccountActivity;
import com.policestrategies.calm_stop.officer.DocviewActivity;
import com.policestrategies.calm_stop.officer.HistoryActivity;
import com.policestrategies.calm_stop.officer.RatingActivity;
import com.policestrategies.calm_stop.officer.beacon_registration.BeaconRegistrationActivity;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

/**
 * Officer landing page. From here, an officer can begin a traffic stop.
 * @author Talal Abou Haiba
 */
public class DashboardActivity extends AppCompatActivity implements View.OnClickListener,
        BeaconConsumer {

    private BottomNavigationView mBottomNavigationView;
    private ProgressDialog mProgressDialog;
    private ImageView mProfileImageView;

    private BeaconManager mBeaconManager;

    private StorageReference mStorageReference;

    private DashboardManager mDashboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);

        findViewById(R.id.button_manage_beacon).setOnClickListener(this);
        findViewById(R.id.button_make_stop).setOnClickListener(this);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        mBeaconManager.bind(this);

        mProfileImageView = ((ImageView) findViewById(R.id.dashboard_officer_profile_picture));
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setBottomNavigationView();

        if (BeaconSimulator.USE_SIMULATED_BEACONS) {
            BeaconManager.setBeaconSimulator(new BeaconSimulator());
            ((BeaconSimulator) BeaconManager.getBeaconSimulator()).createBasicSimulatedBeacons();
        }

        mDashboardManager = new DashboardManager(this, mBeaconManager);

        loadCurrentProfile();
        obtainCurrentBeaconInfo();

    } // end onCreate

    @Override
    public void onResume() {
        super.onResume();
        updateNavigationMenuSelection(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBeaconManager.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_manage_beacon:
                manageBeacon();
                break;

            case R.id.button_make_stop:
                beginStop();
                break;
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        System.out.println("Beacon service connected!");
        mBeaconManager.addRangeNotifier(new BeaconRangeNotifier(mDashboardManager));

    } // end onBeaconServiceConnect


    private void manageBeacon() {
        Intent i = new Intent(getBaseContext(), DocviewActivity.class);
        startActivity(i);
        finish();
    }

    private void beginStop() {

        if (mDashboardManager.beaconRegistrationStatus()) {

            // Beacon is attached and active
            // TODO: This should be called when we enter the region - don't need to click make stop
            // TODO: This should only look for our specific beacons
            Region region = new Region("all-beacons-region", null, null, null);

            try {
                System.out.println("Starting ranging");
                mBeaconManager.startRangingBeaconsInRegion(region);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to start ranging beacons",
                        Toast.LENGTH_SHORT).show();
                System.out.println(e.toString());
            }

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("No active beacon")
                    .setMessage("You are not currently registered to any beacons. Would you like " +
                            "to register to a beacon now?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            manageBeacon();
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

    private void loadCurrentProfile() {

        final DatabaseReference profileReference;
        profileReference = FirebaseDatabase.getInstance().getReference("officer")
                .child(mDashboardManager.getDepartmentNumber())
                .child(mDashboardManager.getUid()).child("profile");

        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String lastName = snapshot.child("last_name").getValue().toString();
                String photoPath = snapshot.child("photo").getValue().toString();
                ((TextView) findViewById(R.id.dashboard_officer_welcome))
                        .setText(getResources().getString(R.string.officer_welcome, lastName));

                profileReference.child("photo").child(photoPath);

                Glide.with(DashboardActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(mStorageReference.child(photoPath))
                        .into(mProfileImageView);

                SharedUtil.dismissProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("", "Failed to read app title value.", error.toException());
            }
        });
    }

    private void obtainCurrentBeaconInfo() {

        DatabaseReference officerProfileReference = FirebaseDatabase.getInstance().getReference("officer")
                .child(mDashboardManager.getDepartmentNumber())
                .child(mDashboardManager.getUid()).child("profile");

        officerProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("beacon")) {
                    mDashboardManager.setCurrentlyRegisteredBeaconId(dataSnapshot.child("beacon")
                            .getValue().toString());
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
        if (mDashboardManager.beaconRegistrationStatus()) {
            beaconStatus = "Connected to beacon #" + mDashboardManager.getCurrentlyRegisteredBeaconId();
            ((ImageView) findViewById(R.id.dashboard_beacon_image))
                    .setImageResource(R.mipmap.ic_done_all_black_48dp);
        } else {
            beaconStatus = "No currently registered beacons";
            ((ImageView) findViewById(R.id.dashboard_beacon_image))
                    .setImageResource(R.mipmap.ic_warning_black_48dp);
        }
        ((TextView) findViewById(R.id.dashboard_beacon_textview)).setText(beaconStatus);
    }

    private void setBottomNavigationView() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                break;

                            case R.id.action_ratings:
                                intent = new Intent(getBaseContext(), RatingActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                break;

                            case R.id.action_history:
                                intent = new Intent(getBaseContext(), HistoryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                break;

                            case R.id.action_account:
                                intent = new Intent(getBaseContext(), AccountActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
    }

    private void updateNavigationMenuSelection(int menu) {
        for (int i = 0; i < 4; i++) {
            MenuItem item = mBottomNavigationView.getMenu().getItem(i);
            item.setChecked(i == menu);
        }
    }

} // end DashboardActivity

