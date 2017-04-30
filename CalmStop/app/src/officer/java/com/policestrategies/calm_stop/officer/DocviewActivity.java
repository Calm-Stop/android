package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;


import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;

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

    private String LicenseUp;
    private String RegistrationUp;
    private String InsuranceUp;
    private Boolean ImageUploaded;

    private String mStopID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        mLicense = (ImageView) findViewById(R.id.license);
//Error catch; these should be set the moment stop occurs before entering this activity
        ImageUploaded = false;
        mStopRef.child("license").setValue(ImageUploaded);
        mStopRef.child("registration").setValue(ImageUploaded);
        mStopRef.child("insurance").setValue(ImageUploaded);
//Use database reference to get stopID
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStopID = "temp_stop_ID";
        mStopRef = mDatabaseRef.child("stops").child(mStopID).getRef();
        mStopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LicenseUp = dataSnapshot.child("license").getValue().toString();
                RegistrationUp = dataSnapshot.child("registration").getValue().toString();
                InsuranceUp = dataSnapshot.child("insurance").getValue().toString();
                if (LicenseUp.equalsIgnoreCase("true")) { SetImage(mLicense, "license"); }
                if (RegistrationUp.equalsIgnoreCase("true")) { SetImage(mRegistration, "registration"); }
                if (InsuranceUp.equalsIgnoreCase("true")) {SetImage(mInsurance, "insurance"); }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//ASSUMPTION: Images uploaded onto firebase have unique names (for unique path)
//mStorageRef is the root of storage: gs://calm-stop.appspot.com
        mStorageRef = FirebaseStorage.getInstance().getReference();

        findViewById(R.id.license).setOnClickListener(this);
        findViewById(R.id.registration).setOnClickListener(this);
        findViewById(R.id.insurance).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.license:
                SetImage(mLicense, "license");
                break;

            case R.id.registration:
                SetImage(mRegistration, "registration");
                break;

            case R.id.insurance:
                SetImage(mInsurance, "insurance");
                break;

        }
    }
    public void SetImage(ImageView imageType, String docType) {
        StorageReference imageRef = mStorageRef.child(mStopID).child(docType);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(imageRef)
                .into(imageType);
    }

}
