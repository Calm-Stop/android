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
    private StorageReference mLocalPathRef;
    private StorageReference mFirebaseImageRef;
//mLicense, mInsurance and mRegistration expected to each store string path URI's
    private Uri mInsurance;
    private Uri mRegistration;
    private ImageView mLicense;
    String mLocalImagePath;
    String mFirebaseImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        mLicense = (ImageView) findViewById(R.id.license);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStopRef = mDatabaseRef.child("stops").child("temp_stop_id").getRef();
//ASSUMPTION: Images uploaded onto firebase have unique names (for unique path)
//If not, make it so
        //mStorageRef is the root of storage: gs://calm-stop.appspot.com
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mLocalImagePath = "/path/to/local/file";
        mFirebaseImagePath = "gs://retrieve/URL/from/citizen";

        mLocalPathRef = mStorageRef.child(mLocalImagePath);
        findViewById(R.id.license).setOnClickListener(this);
        findViewById(R.id.registration).setOnClickListener(this);
        findViewById(R.id.insurance).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {

            case R.id.license:
                StorageReference licenseRef = mStorageRef.child("temp_beacon_ID").child("license");
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(licenseRef)
                        .into(mLicense);
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
