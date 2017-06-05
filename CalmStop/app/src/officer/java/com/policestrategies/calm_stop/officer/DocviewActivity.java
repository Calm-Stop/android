package com.policestrategies.calm_stop.officer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;


public class DocviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mLicenseImageView;
    private ImageView mRegistrationImageView;
    private ImageView mInsuranceImageView;

    private StorageReference mStorageRef;
    private StorageReference mImagePath;
    private String mStopID;

    private ProgressDialog mProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_documents);

        mProgressDialog = new ProgressDialog(this);
        mStopID = "tempStopID";
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mImagePath = mStorageRef.child(mStopID);

        mLicenseImageView = (ImageView) findViewById(R.id.image_view_license);
        mRegistrationImageView = (ImageView) findViewById(R.id.image_view_registration);
        mInsuranceImageView = (ImageView) findViewById(R.id.image_view_insurance);
        findViewById(R.id.requestDocs).setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");

        switch(v.getId()) {
            case R.id.requestDocs:
                waitDocs();
                break;
        }
    }

    private boolean waitDocs() {
        mProgressDialog.setMessage("Waiting for Images");
        //TODO: check firebase boolean variable to see if citizen has pressed sendDoc buttons
        //TODO: send citizen prompt to send documents
        SetImage(mLicenseImageView, "license");
        SetImage(mRegistrationImageView, "registration");
        SetImage(mInsuranceImageView, "insurance");
        mProgressDialog.dismiss();
        Toast.makeText(this, "Images Received", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void SetImage(ImageView imageView, String docType) {
        StorageReference imageRef = mImagePath.child(docType);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(imageRef)
                .into(imageView);
    }
} // end class DocumentsActivity
