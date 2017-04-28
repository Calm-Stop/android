package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.officer.beacon_registration.BeaconRegistrationActivity;

//PLAN: listen in firebase for image, then set image from firebase when image is pushed (ostensibly to stop)
//LOCATION: document images will be expected to be pushed under the stop instance specified by a unique stop_ID
//ASSUMPTION: stop_ID is passed to this class from intent EXTRA or SharedPreference
public class DocviewActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mStopRef;
    private StorageReference mStorageRef;
    private String mInsurance;
    private String mLicense;
    private String mRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStopRef = mDatabaseRef.child("stops").child("temp_stop_id").getRef();
        mStopRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mLicense = dataSnapshot.child("license_uri").getValue().toString();
                mRegistration = dataSnapshot.child("registration_uri").getValue().toString();
                mInsurance = dataSnapshot.child("insurance_uri").getValue().toString();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        findViewById(R.id.license).setOnClickListener(this);
        findViewById(R.id.registration).setOnClickListener(this);
        findViewById(R.id.insurance).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {

            case R.id.license:
                i = new Intent(getBaseContext(), BeaconRegistrationActivity.class);
                startActivity(i);
                break;

            case R.id.registration:
                i = new Intent(getBaseContext(), BeaconRegistrationActivity.class);
                startActivity(i);
                break;

            case R.id.insurance:
                i = new Intent(getBaseContext(), DocviewActivity.class);
                startActivity(i);
                break;

        }
    }

}
