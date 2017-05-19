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


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;


public class DocviewActivity extends AppCompatActivity {

    private ImageView mLicenseImageView;
    private ImageView mRegistrationImageView;
    private ImageView mInsuranceImageView;

    private StorageReference mStorageRef;
    private StorageReference mImagePath;
    private String mStopID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_documents);

        mStopID = "tempStopID";
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mImagePath = mStorageRef.child(mStopID);

        mLicenseImageView = (ImageView) findViewById(R.id.image_view_license);
        mRegistrationImageView = (ImageView) findViewById(R.id.image_view_registration);
        mInsuranceImageView = (ImageView) findViewById(R.id.image_view_insurance);
        ;
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
        return true;
    }

} // end class DocumentsActivity
