package com.policestrategies.calm_stop.citizen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.policestrategies.calm_stop.R;


public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int chosenImage = 1;
    private static final String DOCS = "Documents";
    private static final String LICEN = "license";
    private static final String REGI = "registration";
    private static final String INSUR = "insurance";

    private int PhotoUpdating;  //0 -> License; 1 -> registration; 2 -> insurance

    private ImageView mlicense;
    private ImageView mregistration;
    private ImageView minsurance;
    private Uri licensePic;
    private Uri registrationPic;
    private Uri insurancePic;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mStopRef;
    private StorageReference mStorageRef;
    private StorageReference mImagePath;
    private String mStopID;
    private String ImageUploaded;

    private ProgressDialog mProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);
//Stop ID is retrievable from Beacon ID; no need to explicitly have BeaconID
        mStopID = "temp_stop_ID";

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mImagePath = mStorageRef.child(mStopID);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mStopRef = mDatabaseRef.child("stops").child(mStopID).getRef();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_documents);

        mlicense = (ImageView) findViewById(R.id.license);
        mregistration = (ImageView) findViewById(R.id.registration);
        minsurance = (ImageView) findViewById(R.id.insurance);

        mlicense.setOnClickListener(this);
        mregistration.setOnClickListener(this);
        minsurance.setOnClickListener(this);

        SharedPreferences docs = getSharedPreferences(DOCS, 0);

        //READ FROM
        String licen = docs.getString(LICEN, null);
        String regi = docs.getString(REGI, null);
        String insur = docs.getString(INSUR, null);

        if(licen != null) {
            licensePic = Uri.parse(licen);
            mlicense.setImageURI(licensePic);
        }

        if(regi != null) {
            registrationPic = Uri.parse(regi);
            mregistration.setImageURI(registrationPic);
        }

        if(insur != null) {
            insurancePic = Uri.parse(insur);
            minsurance.setImageURI(insurancePic);
        }

        Toast.makeText(UploadActivity.this, "Click on Image To Upload", Toast.LENGTH_SHORT).show();
    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.license:
                PhotoUpdating = 0;
                Intent lisence = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                lisence.setType("image/");
                startActivityForResult(lisence, chosenImage);
                break;
            case R.id.registration:
                PhotoUpdating = 1;
                Intent reg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                reg.setType("image/");
                startActivityForResult(reg, chosenImage);
                break;
            case R.id.insurance:
                PhotoUpdating = 2;
                Intent insur = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                insur.setType("image/");
                startActivityForResult(insur, chosenImage);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(PhotoUpdating) {
            case 0:
                if (requestCode == chosenImage && resultCode == RESULT_OK && data != null) {
                    mProgressDialog.setMessage("Uploading to Firebase ...");
                    mProgressDialog.show();
                    licensePic = data.getData();
                    mImagePath.child("license").putFile(licensePic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.dismiss();
                            ImageUploaded = "true";
                            Toast.makeText(UploadActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            ImageUploaded = "false";
                            Toast.makeText(UploadActivity.this, "Upload Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mStopRef.child("license").setValue(ImageUploaded);
                    mlicense.setImageURI(licensePic);
                    String license = licensePic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(LICEN, license);
                    editor.commit();
                } else
                    Toast.makeText(UploadActivity.this, "Error with License", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if (requestCode == chosenImage && resultCode == RESULT_OK && data != null) {
                    mProgressDialog.setMessage("Uploading to Firebase ...");
                    mProgressDialog.show();
                    registrationPic = data.getData();
                    mImagePath.child("registration").putFile(licensePic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.dismiss();
                            ImageUploaded = "true";
                            Toast.makeText(UploadActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            ImageUploaded = "false";
                            Toast.makeText(UploadActivity.this, "Upload Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mStopRef.child("registration").setValue(ImageUploaded);
                    mregistration.setImageURI(registrationPic);
                    String regi = registrationPic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(REGI, regi);
                    editor.commit();
                } else
                    Toast.makeText(UploadActivity.this, "Error with Registration", Toast.LENGTH_LONG).show();
                break;
            case 2:
                if (requestCode == chosenImage && resultCode == RESULT_OK && data != null) {
                    mProgressDialog.setMessage("Uploading to Firebase ...");
                    mProgressDialog.show();
                    insurancePic = data.getData();
                    mImagePath.child("insurance").putFile(licensePic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.dismiss();
                            ImageUploaded = "true";
                            Toast.makeText(UploadActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            ImageUploaded = "false";
                            Toast.makeText(UploadActivity.this, "Upload Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mStopRef.child("registration").setValue(ImageUploaded);
                    minsurance.setImageURI(insurancePic);
                    String insur = insurancePic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(INSUR, insur);
                    editor.commit();
                } else
                    Toast.makeText(UploadActivity.this, "Error with Insurance", Toast.LENGTH_LONG).show();
                break;
        }

    }
}
