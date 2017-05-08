package com.policestrategies.calm_stop.officer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

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

//PLAN: listen in firebase for image, then set image from firebase when image is pushed (ostensibly to stopID)
//LOCATION: document image URL's will be expected to be unique and created by citizen and pushed under the stop instance specified by a unique stop_ID
//ASSUMPTION: stop_ID is passed to this class from intent EXTRA or SharedPreference
//ASSUMPTION: stop_ID is already stored in database; reference to stop_ID will not be null on call of this activity
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
    private String ImageUploaded;

    private String mStopID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        mLicense = (ImageView) findViewById(R.id.license);
        mRegistration = (ImageView) findViewById(R.id.registration);
        mInsurance = (ImageView) findViewById(R.id.insurance);
//Use database reference to get stopID
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStopID = "temp_stop_ID";
//The following retrieval of stopID is not compatible with current database schema
//the stopID should already exist and is passed to this activity via beacon
        mStopRef = mDatabaseRef.child("stops").child(mStopID).getRef();
        LicenseUp = RegistrationUp = InsuranceUp = "false";
        ImageUploaded = "true";
        mStopRef.child("license").setValue(ImageUploaded);
        mStopRef.child("registration").setValue(ImageUploaded);
        mStopRef.child("insurance").setValue(ImageUploaded);

// Once image is uploaded, grab it. Don't grab again unless citizen does another upload
        mStopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (LicenseUp.equalsIgnoreCase("false")) { LicenseUp = dataSnapshot.child("license").getValue().toString(); }

                if (RegistrationUp.equalsIgnoreCase("false")) { RegistrationUp = dataSnapshot.child("registration").getValue().toString(); }

                if (InsuranceUp.equalsIgnoreCase("false")) { InsuranceUp = dataSnapshot.child("insurance").getValue().toString(); }

                if (LicenseUp.equalsIgnoreCase("true")) {
                    mStopRef.child("license").setValue("false");
                    SetImage(mLicense, "license");
                    LicenseUp = "false";
                }
                if (RegistrationUp.equalsIgnoreCase("true")) {
                    SetImage(mRegistration, "registration");
                    mStopRef.child("registration").setValue("false");
                    RegistrationUp = "false";
                }
                if (InsuranceUp.equalsIgnoreCase("true")) {
                    SetImage(mInsurance, "insurance");
                    mStopRef.child("insurance").setValue("false");
                    InsuranceUp = "false";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//ASSUMPTION: Images uploaded onto firebase have unique names (for unique path)
//mStorageRef is the root of storage: gs://calm-stop.appspot.com
        mStorageRef = FirebaseStorage.getInstance().getReference();

        findViewById(R.id.uploadLicense).setOnClickListener(this);
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
