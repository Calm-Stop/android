package com.policestrategies.calm_stop.citizen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.storage.UploadTask;
import com.policestrategies.calm_stop.DocumentViewActivity;
import com.policestrategies.calm_stop.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author mariavizcaino
 */
public class DocumentsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_LICENSE_SELECTED = 100;
    private static final int RESULT_INSURANCE_SELECTED = 101;
    private static final int RESULT_REGISTRATION_SELECTED = 102;

    private ImageView mLicenseImageView;
    private ImageView mRegistrationImageView;
    private ImageView mInsuranceImageView;

    private String mLicenseFilePath;
    private String mInsuranceFilePath;
    private String mRegistrationFilePath;

    private ProgressDialog mProgressDialog;
    private StorageReference mStorageRef;
    private StorageReference mImagePath;
    private String mStopID;
    private String ImageUploaded;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_documents);

        mStopID = "tempStopID";
        mProgressDialog = new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mImagePath = mStorageRef.child(mStopID);

        mLicenseImageView = (ImageView) findViewById(R.id.image_view_license);
        mRegistrationImageView = (ImageView) findViewById(R.id.image_view_registration);
        mInsuranceImageView = (ImageView) findViewById(R.id.image_view_insurance);

        findViewById(R.id.uploadInsurance).setOnClickListener(this);
        findViewById(R.id.uploadLicense).setOnClickListener(this);
        findViewById(R.id.uploadRegistration).setOnClickListener(this);
        findViewById(R.id.uploadAll).setOnClickListener(this);

        findViewById(R.id.image_view_insurance).setOnClickListener(this);
        findViewById(R.id.image_view_license).setOnClickListener(this);
        findViewById(R.id.image_view_registration).setOnClickListener(this);

        loadLicImage();
        loadRegImage();
        loadInsImage();

    }

    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");

        switch(v.getId()) {
            case R.id.uploadLicense:
                startActivityForResult(intent, RESULT_LICENSE_SELECTED);
                break;

            case R.id.uploadRegistration:
                startActivityForResult(intent, RESULT_REGISTRATION_SELECTED);
                break;

            case R.id.uploadInsurance:
                startActivityForResult(intent, RESULT_INSURANCE_SELECTED);
                break;

            case R.id.sendLicense:
                UriToFirebase(/*license uri*/);
                break;
            case R.id.sendInsurance:
                UriToFirebase(/*Insurance uri*/);
                break;
            case R.id.sendRegistration:
                UriToFirebase(/*Registration uri*/);
                break;
            case R.id.sendAll:
                AllUriToFirebase();
            case R.id.image_view_insurance:
            case R.id.image_view_license:
            case R.id.image_view_registration:
                loadDocumentViewActivity(v.getId());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            System.out.println("ERROR: Result for request " + requestCode + " returned NOT OK!");
            return;
        }

        switch(requestCode) {

            case RESULT_LICENSE_SELECTED:
                mLicenseImageView.setImageURI(data.getData());
                saveLicImage(data.getData());
                break;

            case RESULT_REGISTRATION_SELECTED:
                mRegistrationImageView.setImageURI(data.getData());
                saveRegImage(data.getData());
                break;

            case RESULT_INSURANCE_SELECTED:
                mInsuranceImageView.setImageURI(data.getData());
                saveInsImage(data.getData());
                break;
        }

    }

    private void UriToFirebase(Uri license_uri) {
        mProgressDialog.setMessage("Sending to Officer ...");
        mProgressDialog.show();
        mImagePath.child("license").putFile(license_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgressDialog.dismiss();
                ImageUploaded = "true";
                Toast.makeText(DocumentsActivity.this, "Documents sent.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                ImageUploaded = "false";
                Toast.makeText(DocumentsActivity.this, "Upload Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AllUriToFirebase(Uri license_uri, Uri insurance_uri, Uri registration_uri) {
        UriToFirebase(license_uri);
        UriToFirebase(insurance_uri);
        UriToFirebase(registration_uri);
    }

    private void loadDocumentViewActivity(int id) {
        Intent i = new Intent(DocumentsActivity.this, DocumentViewActivity.class);
        String filepath = "";

        switch (id) {
            case R.id.image_view_insurance:
                filepath = mInsuranceFilePath;
                break;

            case R.id.image_view_license:
                filepath = mLicenseFilePath;
                break;

            case R.id.image_view_registration:
                filepath = mRegistrationFilePath;
                break;
        }

        i.putExtra("document_path", filepath);
        startActivity(i);
    }

    //LOAD LICENSE IMAGE
    private void loadLicImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("License", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "license.JPG");
        mLicenseFilePath = f.toString();
        mLicenseImageView.setImageURI(Uri.fromFile(f));
    }

    //LOAD REGISTRATION IMAGE
    private void loadRegImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_Registration/registration.JPG
        File directory = cw.getDir("Registration", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "registration.JPG");
        mRegistrationFilePath = f.toString();
        mRegistrationImageView.setImageURI(Uri.fromFile(f));
    }

    //LOAD INSURANCE IMAGE
    private void loadInsImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("Insurance", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "insurance.JPG");
        mInsuranceFilePath = f.toString();
        mInsuranceImageView.setImageURI(Uri.fromFile(f));
    }

    private void saveLicImage(Uri data){
        Bitmap image = convertUriToBitmap(data);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File myfile = cw.getDir("License", Context.MODE_PRIVATE);
        File mypath = new File(myfile, "license.JPG");

        FileOutputStream fo;

        //saving image into internal storage
        try {
            //myfile.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(byteArray);
            fo.flush();
            fo.close();
        } catch (Exception e) {
            Toast.makeText(DocumentsActivity.this, "Error with License Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void saveRegImage(Uri data){
        Bitmap image = convertUriToBitmap(data);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File myfile = cw.getDir("Registration", Context.MODE_PRIVATE);
        File mypath = new File(myfile, "registration.JPG");

        FileOutputStream fo;

        //saving image into internal storage
        try {
            //myfile.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(byteArray);
            fo.flush();
            fo.close();
        } catch (Exception e) {
            Toast.makeText(DocumentsActivity.this, "Error with Registration Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void saveInsImage(Uri data){
        Bitmap image = convertUriToBitmap(data);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File myfile = cw.getDir("Insurance", Context.MODE_PRIVATE);
        File mypath = new File(myfile, "insurance.JPG");

        FileOutputStream fo;

        //saving image into internal storage
        try {
            //myfile.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(byteArray);
            fo.flush();
            fo.close();
        } catch (Exception e) {
            Toast.makeText(DocumentsActivity.this, "Error with Insurance Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private Bitmap convertUriToBitmap(Uri data) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

} // end class DocumentsActivity
