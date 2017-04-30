package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.officer.beacon_registration.BeaconRegistrationActivity;

//PLAN: listen in firebase for image, then set image from firebase when image is pushed (ostensibly to stop)
//LOCATION: document image URL's will be expected to be unique and created by citizen and pushed under the stop instance specified by a unique stop_ID
//ASSUMPTION: stop_ID is passed to this class from intent EXTRA or SharedPreference
public class DocviewActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mStopRef;
    private StorageReference mStorageRef;

    private ImageView mLicense;
    private ImageView mRegistration;
    private ImageView mInsurance;

    private String mStopID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        mLicense = (ImageView) findViewById(R.id.license);
//Use database reference to get stopID
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStopID = "temp_stop_id";
        mStopRef = mDatabaseRef.child("stops").child(mStopID).getRef();
//ASSUMPTION: Images uploaded onto firebase have unique names (for unique path)
//mStorageRef is the root of storage: gs://calm-stop.appspot.com
        mStorageRef = FirebaseStorage.getInstance().getReference();

        findViewById(R.id.license).setOnClickListener(this);
        findViewById(R.id.registration).setOnClickListener(this);
        findViewById(R.id.insurance).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {

            case R.id.license:
                StorageReference licenseRef = mStorageRef.child(mStopID).child("license");
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(licenseRef)
                        .into(mLicense);
                break;

            case R.id.registration:
                StorageReference registrationRef = mStorageRef.child(mStopID).child("registration");
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(registrationRef)
                        .into(mLicense);
                break;

            case R.id.insurance:
                StorageReference insuranceRef = mStorageRef.child(mStopID).child("insurance");
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(insuranceRef)
                        .into(mLicense);
                break;

        }
    }

}
